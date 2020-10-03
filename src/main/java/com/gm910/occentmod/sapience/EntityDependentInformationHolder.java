package com.gm910.occentmod.sapience;

import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;

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

	@Override
	public long getTicksExisted() {
		return entityIn.ticksExisted;
	}

	public SapientInfo<E> getInfo() {
		return SapientInfo.get(this.entityIn);
	}
}
