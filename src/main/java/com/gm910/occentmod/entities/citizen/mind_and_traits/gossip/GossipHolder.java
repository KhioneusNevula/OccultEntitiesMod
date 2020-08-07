package com.gm910.occentmod.entities.citizen.mind_and_traits.gossip;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.EntityDependentInformationHolder;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;

public class GossipHolder extends EntityDependentInformationHolder<CitizenEntity> {

	/**
	 * Int: ticks until the citizen stops spreading gossip and forgets it.
	 */
	private Object2IntMap<CitizenGossip> gossip = Object2IntMaps.emptyMap();

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T gmemo = ops.createMap(gossip.entrySet().stream().map((trait) -> {
			return Pair.of(trait.getKey().serialize(ops), ops.createInt(trait.getValue()));
		}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		return ops.createMap(ImmutableMap.of(ops.createString("gossip"), gmemo));
	}

	public GossipHolder(CitizenEntity en, Dynamic<?> dyn) {
		super(en);
		Map<CitizenGossip, Integer> map2 = dyn.get("gossip").asMap((d) -> CitizenGossip.deserialize(en, d),
				(d) -> d.asInt(0));
		gossip.putAll(map2);
	}

	public GossipHolder(CitizenEntity en) {
		super(en);
	}

	public void tick() {
		for (CitizenGossip gos : gossip.keySet()) {
			gossip.put(gos, gossip.getInt(gos) - 1);
			if (gossip.getInt(gos) <= 0) {
				gossip.removeInt(gos);
			}
		}
	}

	public Set<CitizenGossip> getGossip() {
		return this.gossip.keySet();
	}

}
