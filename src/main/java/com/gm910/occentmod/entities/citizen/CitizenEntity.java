package com.gm910.occentmod.entities.citizen;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class CitizenEntity extends AgeableEntity {

	public CitizenEntity(EntityType<? extends AgeableEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	public AgeableEntity createChild(AgeableEntity ageable) {
		return null;
	}

}
