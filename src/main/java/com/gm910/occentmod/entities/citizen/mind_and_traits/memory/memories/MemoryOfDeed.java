package com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories;

import com.gm910.occentmod.capabilities.citizeninfo.CitizenInfo;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.MemoryType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.deeds.CitizenDeed;
import com.mojang.datafixers.Dynamic;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public class MemoryOfDeed extends MemoryOfOccurrence {

	public MemoryOfDeed(LivingEntity owner, CitizenDeed deed) {
		super(owner, deed);
		this.type = MemoryType.DEED;
	}

	public MemoryOfDeed(LivingEntity owner, Dynamic<?> dyn) {
		this(owner,
				(CitizenDeed) OccurrenceType.deserialize(((ServerWorld) owner.world), dyn.get("event").get().get()));
	}

	public CitizenDeed getDeed() {
		return (CitizenDeed) this.getEvent();
	}

	@Override
	public void affectCitizen(LivingEntity en1) {
		CitizenInfo<LivingEntity> en = CitizenInfo.get(en1).orElse(null);
		en.getRelationships().changeLikeValue(en.getIdentity(), getDeed().getRelationshipChange(en.$getOwner()));
		getDeed().affectCitizen(en);
	}

}
