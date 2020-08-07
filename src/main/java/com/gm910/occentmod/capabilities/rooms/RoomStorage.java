package com.gm910.occentmod.capabilities.rooms;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class RoomStorage implements IStorage<RoomManager> {

	@Override
	public INBT writeNBT(Capability<RoomManager> capability, RoomManager instance,
			Direction side) {

		return instance.serializeNBT();
	}

	@Override
	public void readNBT(Capability<RoomManager> capability, RoomManager instance, Direction side,
			INBT nbt) {
		instance.deserializeNBT((CompoundNBT) nbt);
	}

}
