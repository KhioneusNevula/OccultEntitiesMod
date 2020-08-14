package com.gm910.occentmod.entities.citizen.mind_and_traits.gossip;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.deeds.CitizenDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.deeds.OccurrenceType;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class MemoryOfDeed extends CitizenMemory {

	private CitizenDeed deed;

	public MemoryOfDeed(CitizenEntity owner, CitizenDeed deed) {
		super(owner, CitizenMemoryType.DEED);
		this.deed = deed;
	}

	public MemoryOfDeed(CitizenEntity owner, Dynamic<?> dyn) {
		this(owner, (CitizenDeed) OccurrenceType.deserialize(dyn.get("deed").get().get()));
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

	@Override
	public void affectCitizen(CitizenEntity en) {
		en.getRelationships().changeLikeValue(en.getIdentity(), deed.getRelationshipChange(en));
		deed.affectCitizen(en.getInfo());
	}

}
