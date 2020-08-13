package com.gm910.occentmod.capabilities.mental_inventory;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class MindInventoryStorage implements IStorage<MindInventory> {

	@Override
	public INBT writeNBT(Capability<MindInventory> capability, MindInventory instance, Direction side) {
		
		return instance.serializeNBT();
	}

	@Override
	public void readNBT(Capability<MindInventory> capability, MindInventory instance, Direction side, INBT nbt) {
		instance.deserializeNBT((CompoundNBT) nbt);
	}

}
