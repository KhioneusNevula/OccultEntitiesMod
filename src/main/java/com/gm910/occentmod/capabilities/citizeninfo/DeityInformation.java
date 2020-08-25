package com.gm910.occentmod.capabilities.citizeninfo;

import com.gm910.occentmod.empires.gods.Deity;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompoundNBT;

public class DeityInformation extends CitizenInfo<Deity> {

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {

	}

	@Override
	public IInventory getInventory() {
		return this.$getOwner().inventory;
	}

}
