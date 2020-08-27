package com.gm910.occentmod.capabilities.citizeninfo;

import com.gm910.occentmod.api.util.GMNBT;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class SapinfoStorage<T extends LivingEntity> implements IStorage<SapientInfo<T>> {

	@Override
	public INBT writeNBT(Capability<SapientInfo<T>> capability, SapientInfo<T> instance, Direction side) {

		return instance.serialize(NBTDynamicOps.INSTANCE);
	}

	@Override
	public void readNBT(Capability<SapientInfo<T>> capability, SapientInfo<T> instance, Direction side, INBT nbt) {
		instance.deserialize(GMNBT.makeDynamic(nbt));
	}

}
