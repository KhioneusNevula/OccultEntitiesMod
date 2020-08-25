package com.gm910.occentmod.capabilities.magicdata;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class WizardStorage implements IStorage<MagicData> {

	@Override
	public INBT writeNBT(Capability<MagicData> capability, MagicData instance, Direction side) {

		return instance.serializeNBT();
	}

	@Override
	public void readNBT(Capability<MagicData> capability, MagicData instance, Direction side, INBT nbt) {
		instance.deserializeNBT((CompoundNBT) nbt);
	}

}
