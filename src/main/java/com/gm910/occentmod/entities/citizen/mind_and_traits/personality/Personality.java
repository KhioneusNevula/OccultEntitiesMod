package com.gm910.occentmod.entities.citizen.mind_and_traits.personality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.InformationHolder;
import com.gm910.occentmod.entities.citizen.mind_and_traits.gossip.GossipType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.NumericPersonalityTrait.TraitLevel;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.ResourceLocation;

public class Personality extends InformationHolder {

	private Object2FloatMap<NumericPersonalityTrait> traits = new Object2FloatOpenHashMap<>();

	private List<GossipType<?>> gossipPriority = new ArrayList<>();

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T trait1 = ops.createMap(traits.entrySet().stream().map((trait) -> {
			return Pair.of(ops.createString(trait.getKey().getName()), ops.createFloat(trait.getValue()));
		}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		T gos = ops.createList(gossipPriority.stream().map((e) -> ops.createString(e.regName.toString())));
		return ops.createMap(
				ImmutableMap.of(ops.createString("traits"), trait1, ops.createString("gossipPriority"), gos));
	}

	public Personality(Dynamic<?> dyn) {
		Map<NumericPersonalityTrait, Float> map = dyn.get("traits")
				.asMap((d) -> NumericPersonalityTrait.fromName(d.asString("")), (d) -> d.asFloat(0));
		traits.putAll(map);
		List<GossipType<?>> gos = dyn.get("gossipPriority")
				.asList((ee) -> GossipType.get(new ResourceLocation(ee.asString(""))));
		this.gossipPriority.addAll(gos);
	}

	public Personality() {
		for (NumericPersonalityTrait trait : NumericPersonalityTrait.values()) {
			traits.put(trait, 0.0f);
		}
		Collection<GossipType<?>> ls = GossipType.getGossipTypes();
		this.gossipPriority = new ArrayList<>(ls);
		Collections.shuffle(gossipPriority);
	}

	public static double gaussian(Random rand, double mean, double standev) {
		return rand.nextGaussian() * standev + mean;
	}

	public Personality initializeRandomTraits(CitizenEntity e) {
		for (NumericPersonalityTrait trait : NumericPersonalityTrait.values()) {

			traits.put(trait,
					clamp((float) (gaussian(e.getRNG(), ((trait.max + trait.min) / 2), ((trait.max - trait.min) / 4))),
							trait.min, trait.max));
		}
		return this;
	}

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

	public void setTrait(NumericPersonalityTrait trait, float value) {
		this.traits.put(trait, clamp(value, trait.min, trait.max));
	}

	public float getTrait(NumericPersonalityTrait trait) {
		return this.traits.getFloat(trait);
	}

	public Map<NumericPersonalityTrait, TraitLevel> generateTraitReactionMap() {
		Map<NumericPersonalityTrait, TraitLevel> mapa = new HashMap<>();
		for (NumericPersonalityTrait trait : NumericPersonalityTrait.values()) {
			mapa.put(trait, trait.getWeightedRandomReaction(this.getTrait(trait)));
		}
		return mapa;
	}

}
