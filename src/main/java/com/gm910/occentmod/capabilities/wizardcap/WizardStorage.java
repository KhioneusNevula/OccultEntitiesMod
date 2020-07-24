package com.gm910.occentmod.capabilities.wizardcap;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class WizardStorage implements IStorage<IWizard> {

	@Override
	public INBT writeNBT(Capability<IWizard> capability, IWizard instance, Direction side) {
		
		return instance.serializeNBT();
	}

	@Override
	public void readNBT(Capability<IWizard> capability, IWizard instance, Direction side, INBT nbt) {
		instance.deserializeNBT((CompoundNBT) nbt);
	}

}
