package com.gm910.occentmod.entities.citizen.mind_and_traits.personality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.gm910.occentmod.entities.citizen.mind_and_traits.InformationHolder;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.CitizenMemoryType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.PersonalityTrait.TraitLevel;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.ResourceLocation;

public class Personality extends InformationHolder {

	private Object2FloatMap<PersonalityTrait> traits = new Object2FloatOpenHashMap<>();

	private List<CitizenMemoryType<?>> gossipPriority = new ArrayList<>();

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
		Map<PersonalityTrait, Float> map = dyn.get("traits").asMap((d) -> PersonalityTrait.fromName(d.asString("")),
				(d) -> d.asFloat(0));
		traits.putAll(map);
		List<CitizenMemoryType<?>> gos = dyn.get("gossipPriority")
				.asList((ee) -> CitizenMemoryType.get(new ResourceLocation(ee.asString(""))));
		this.gossipPriority.addAll(gos);
	}

	public Personality() {
		for (PersonalityTrait trait : PersonalityTrait.values()) {
			traits.put(trait, 0.0f);
		}
		Collection<CitizenMemoryType<?>> ls = CitizenMemoryType.getMemoryTypes();
		this.gossipPriority = new ArrayList<>(ls);
		Collections.shuffle(gossipPriority);
	}

	public static double gaussian(Random rand, double mean, double standev) {
		return rand.nextGaussian() * standev + mean;
	}

	public Personality initializeRandomTraits(Random e) {
		for (PersonalityTrait trait : PersonalityTrait.values()) {

			traits.put(trait, clamp((float) (gaussian(e, ((trait.max + trait.min) / 2), ((trait.max - trait.min) / 4))),
					trait.min, trait.max));
		}
		return this;
	}

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

	public void setTrait(PersonalityTrait trait, float value) {
		this.traits.put(trait, clamp(value, trait.min, trait.max));
	}

	public float getTrait(PersonalityTrait trait) {
		return this.traits.getFloat(trait);
	}

	public Map<PersonalityTrait, TraitLevel> generateTraitReactionMap() {
		Map<PersonalityTrait, TraitLevel> mapa = new HashMap<>();
		for (PersonalityTrait trait : PersonalityTrait.values()) {
			mapa.put(trait, trait.getWeightedRandomReaction(this.getTrait(trait)));
		}
		return mapa;
	}

	@Override
	public long getTicksExisted() {
		return 0;
	}

}
