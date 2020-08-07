package com.gm910.occentmod.entities.citizen.mind_and_traits.genetics;

import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.genetype.GeneType;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;

public class Gene<R> implements IDynamicSerializable {

	private GeneType<R, ?> type;
	private R value;
	private Race raceMarker = Race.MIXED;

	public Gene(GeneType<R, ?> type, R value) {
		this.type = type;
		this.value = value;
	}

	public static <T> Gene<?> deserialize(Dynamic<T> dyn) {
		GeneType type = GeneType.get(new ResourceLocation(dyn.get("type").asString("")));
		Race raceMarker = Race.fromId(dyn.get("race").asInt(0));
		Gene<?> gene = type.deserialize(dyn.get("data").get().get());
		gene.type = type;
		gene.raceMarker = raceMarker;
		return gene;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T val = this.type.serialize(ops, this);
		T type = ops.createString(this.type.getResource().toString());
		T rac = ops.createInt(this.raceMarker.id);
		return ops.createMap(ImmutableMap.of(ops.createString("data"), val, ops.createString("type"), type,
				ops.createString("race"), rac));
	}

	public Gene<R> setRaceMarker(Race race) {
		this.raceMarker = race;
		return this;
	}

	public Race getRaceMarker() {
		return raceMarker;
	}

	public GeneType<R, ?> getType() {
		return type;
	}

	public Gene<R> copy() {
		return new Gene<R>(type, value).setRaceMarker(this.raceMarker);
	}

	public R getValue() {
		return value;
	}
}