package com.gm910.occentmod.capabilities.citizeninfo;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class CitinfoStorage implements IStorage<CitizenInfo> {

	@Override
	public INBT writeNBT(Capability<CitizenInfo> capability, CitizenInfo instance, Direction side) {

		return instance.serializeNBT();
	}

	@Override
	public void readNBT(Capability<CitizenInfo> capability, CitizenInfo instance, Direction side, INBT nbt) {
		instance.deserializeNBT((CompoundNBT) nbt);
	}

}
