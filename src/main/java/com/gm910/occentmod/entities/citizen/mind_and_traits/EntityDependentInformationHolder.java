package com.gm910.occentmod.entities.citizen.mind_and_traits;

import net.minecraft.entity.LivingEntity;

public abstract class EntityDependentInformationHolder<E extends LivingEntity> extends InformationHolder {

	private E entityIn;

	public EntityDependentInformationHolder(E entity) {
		this.entityIn = entity;
	}

	public E getEntityIn() {
		return entityIn;
	}

	public void setEntityIn(E entityIn) {
		this.entityIn = entityIn;
	}
}
