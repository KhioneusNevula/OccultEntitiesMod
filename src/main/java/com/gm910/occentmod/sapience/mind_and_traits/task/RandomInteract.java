package com.gm910.occentmod.sapience.mind_and_traits.task;

import com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds.SapientDeed;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.LivingEntity;

public class RandomInteract extends SapientTask<LivingEntity> {

	public RandomInteract() {
		super(LivingEntity.class, ImmutableMap.of());
	}

	@Override
	public SapientDeed getDeed(SapientIdentity doer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TaskType<LivingEntity, ? extends SapientTask<LivingEntity>> getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
