package com.gm910.occentmod.empires.gods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.gm910.occentmod.empires.Empire;
import com.gm910.occentmod.entities.citizen.mind_and_traits.emotions.Emotions;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.Personality;
import com.google.common.collect.Sets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.CapabilityProvider;

public class Deity extends CapabilityProvider<Deity> implements IDynamicSerializable {

	private Set<DeityElement> elements = new HashSet<>();
	private Empire empire;
	private Personality personality;
	private Emotions emotions;
	private Set<DeityPower> powers = new HashSet<>();// TODO
	private UUID uuid = MathHelper.getRandomUUID();

	public Deity(Empire e, Set<DeityElement> elements) {
		super(Deity.class);
		empire = e;
		this.elements.addAll(elements);
		this.personality = new Personality();
		personality.initializeRandomTraits(e.getCenterWorld().rand);
		this.emotions = new Emotions();
		this.gatherCapabilities();
	}

	public Deity(Empire e, Dynamic<?> d) {
		this(e, Sets.newHashSet());
		empire = e;
		this.elements.addAll(d.get("elements").asList((m) -> DeityElement.get(new ResourceLocation(m.asString("")))));
		this.emotions = new Emotions(d.get("emotions").get().get());
		this.personality = new Personality(d.get("personality").get().get());
		if (d.get("caps").get().isPresent()) {
			try {
				this.deserializeCaps(JsonToNBT.getTagFromJson(d.get("caps").asString("")));
			} catch (CommandSyntaxException e1) {
				e1.printStackTrace();
			}
		}
	}

	public Emotions getEmotions() {
		return emotions;
	}

	public void addPower(DeityPower power) {
		this.powers.add(power);
		// TODO
	}

	public UUID getUuid() {
		return uuid;
	}

	public Personality getPersonality() {
		return personality;
	}

	public Empire getEmpire() {
		return empire;
	}

	public Set<DeityElement> getElements() {
		return new HashSet<>(elements);
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Map<T, T> map = new HashMap<>();
		map.put(ops.createString("elements"),
				ops.createList(elements.stream().map((e) -> ops.createString(e.getResource().toString()))));
		map.put(ops.createString("personality"), this.personality.serialize(ops));
		map.put(ops.createString("emotions"), this.personality.serialize(ops));
		CompoundNBT capa = this.serializeCaps();
		if (capa != null) {
			map.put(ops.createString("caps"), ops.createString(capa.getString()));
		}
		return ops.createMap(map);
	}
}
