package com.gm910.occentmod.capabilities.citizeninfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class PlayerCitizenInformation extends CitizenInfo<PlayerEntity> {

	@Override
	public CompoundNBT serializeNBT() {
		return new CompoundNBT();
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {

	}

}
