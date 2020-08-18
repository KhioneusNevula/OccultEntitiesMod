package com.gm910.occentmod.api.util;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

public class FakePlayer extends PlayerEntity {
	GameType gameType = GameType.SURVIVAL;

	public FakePlayer(World worldIn, GameProfile gameProfileIn) {
		super(worldIn, gameProfileIn);
	}

	@Override
	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	@Override
	public boolean blockActionRestricted(World p_223729_1_, BlockPos p_223729_2_, GameType p_223729_3_) {
		return super.blockActionRestricted(p_223729_1_, p_223729_2_, gameType);
	}

	@Override
	public boolean isSpectator() {
		return false;
	}

	@Override
	public boolean isCreative() {
		return gameType.isCreative();
	}
}