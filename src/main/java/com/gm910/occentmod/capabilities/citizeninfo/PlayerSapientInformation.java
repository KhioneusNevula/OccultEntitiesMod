package com.gm910.occentmod.capabilities.citizeninfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompoundNBT;

public class PlayerSapientInformation extends SapientInfo<PlayerEntity> {

	@Override
	public CompoundNBT serializeNBT() {
		return new CompoundNBT();
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {

	}

	@Override
	public IInventory getInventory() {
		return this.$getOwner().inventory;
	}

	@Override
	public void onCreation() {

	}

	@Override
	public PlayerEntity getPlayerDelegate() {
		// TODO Auto-generated method stub
		return this.$getOwner();
	}

}
