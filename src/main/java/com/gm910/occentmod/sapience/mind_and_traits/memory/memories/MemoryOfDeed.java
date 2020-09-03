package com.gm910.occentmod.sapience.mind_and_traits.memory.memories;

import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.sapience.mind_and_traits.memory.MemoryType;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceEffect.Connotation;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceType;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds.SapientDeed;
import com.mojang.datafixers.Dynamic;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public class MemoryOfDeed<E extends LivingEntity> extends MemoryOfOccurrence<E> {

	public MemoryOfDeed(E owner, SapientDeed deed) {
		super(owner, deed);
		this.type = MemoryType.DEED;
	}

	public MemoryOfDeed(E owner, Dynamic<?> dyn) {
		this(owner,
				(SapientDeed) OccurrenceType.deserialize(((ServerWorld) owner.world), dyn.get("event").get().get()));
	}

	public SapientDeed getDeed() {
		return (SapientDeed) this.getEvent();
	}

	@Override
	public void affectCitizen(E en1) {
		SapientInfo<E> en = SapientInfo.get(en1);
		en.getRelationships().changeLikeValue(en.getIdentity(), getDeed().getRelationshipChange(en.$getOwner()));
		getDeed().affectCitizen(en);
	}

	@Override
	public Connotation getOpinion() {
		return getDeed().getEffect().getEffect(SapientInfo.get(this.getOwner()).getIdentity());
	}

}
