package com.gm910.occentmod.capabilities.speciallocs;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class SpecialLocationStorage implements IStorage<SpecialLocationManager> {

	@Override
	public INBT writeNBT(Capability<SpecialLocationManager> capability, SpecialLocationManager instance,
			Direction side) {

		return instance.serializeNBT();
	}

	@Override
	public void readNBT(Capability<SpecialLocationManager> capability, SpecialLocationManager instance, Direction side,
			INBT nbt) {
		instance.deserializeNBT((CompoundNBT) nbt);
	}

}
