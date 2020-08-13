package com.gm910.occentmod.capabilities.formshifting;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class FormStorage implements IStorage<Formshift> {

	@Override
	public INBT writeNBT(Capability<Formshift> capability, Formshift instance, Direction side) {
		
		return instance.serializeNBT();
	}

	@Override
	public void readNBT(Capability<Formshift> capability, Formshift instance, Direction side, INBT nbt) {
		instance.deserializeNBT((CompoundNBT) nbt);
	}

}
