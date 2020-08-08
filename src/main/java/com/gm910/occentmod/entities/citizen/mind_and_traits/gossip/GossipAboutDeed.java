package com.gm910.occentmod.entities.citizen.mind_and_traits.gossip;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.deeds.CitizenDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.deeds.CitizenDeedType;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class GossipAboutDeed extends CitizenGossip {

	private CitizenDeed deed;

	public GossipAboutDeed(CitizenEntity owner, CitizenDeed deed) {
		super(owner, GossipType.DEED);
		this.deed = deed;
	}

	public GossipAboutDeed(CitizenEntity owner, Dynamic<?> dyn) {
		this(owner, CitizenDeedType.deserialize(dyn.get("deed").get().get()));
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {
		T deed1 = deed.serialize(ops);
		T id = ops.createString(deed.getType().getName().toString());
		return ops.createMap(ImmutableMap.of(ops.createString("deed"), deed1, ops.createString("id"), id));
	}

	public CitizenDeed getDeed() {
		return deed;
	}

}
