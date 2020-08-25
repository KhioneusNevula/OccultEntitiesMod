package com.gm910.occentmod.capabilities.citizeninfo;

import com.gm910.occentmod.api.util.GMNBT;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class CitinfoStorage<T extends LivingEntity> implements IStorage<CitizenInfo<T>> {

	@Override
	public INBT writeNBT(Capability<CitizenInfo<T>> capability, CitizenInfo<T> instance, Direction side) {

		return instance.serialize(NBTDynamicOps.INSTANCE);
	}

	@Override
	public void readNBT(Capability<CitizenInfo<T>> capability, CitizenInfo<T> instance, Direction side, INBT nbt) {
		instance.deserialize(GMNBT.makeDynamic(nbt));
	}

}
