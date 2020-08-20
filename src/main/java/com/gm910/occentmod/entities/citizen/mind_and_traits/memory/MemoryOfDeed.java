package com.gm910.occentmod.entities.citizen.mind_and_traits.memory;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.deeds.CitizenDeed;
import com.mojang.datafixers.Dynamic;

public class MemoryOfDeed extends MemoryOfOccurrence {

	public MemoryOfDeed(CitizenEntity owner, CitizenDeed deed) {
		super(owner, deed);
		this.type = CitizenMemoryType.DEED;
	}

	public MemoryOfDeed(CitizenEntity owner, Dynamic<?> dyn) {
		this(owner, (CitizenDeed) OccurrenceType.deserialize(dyn.get("event").get().get()));
	}

	public CitizenDeed getDeed() {
		return (CitizenDeed) this.getEvent();
	}

	@Override
	public void affectCitizen(CitizenEntity en) {
		en.getRelationships().changeLikeValue(en.getIdentity(), getDeed().getRelationshipChange(en));
		getDeed().affectCitizen(en.getInfo());
	}

}
