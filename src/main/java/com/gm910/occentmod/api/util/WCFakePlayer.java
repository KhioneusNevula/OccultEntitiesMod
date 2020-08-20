package com.gm910.occentmod.api.util;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class WCFakePlayer extends PlayerEntity {
	GameType gameType = GameType.SURVIVAL;

	public WCFakePlayer(World worldIn, GameProfile gameProfileIn) {
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

	@Override
	public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar) {
	}

	@Override
	public void sendMessage(ITextComponent component) {
	}

	@Override
	public void addStat(Stat par1StatBase, int par2) {
	}

	@Override
	public void tick() {
		return;
	}

	@Override
	@Nullable
	public MinecraftServer getServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}

}
