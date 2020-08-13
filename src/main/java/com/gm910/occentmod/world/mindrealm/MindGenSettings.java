package com.gm910.occentmod.world.mindrealm;

import com.gm910.occentmod.init.BlockInit;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.GenerationSettings;

public class MindGenSettings extends GenerationSettings {

	private BlockPos spawnPos;

	public MindGenSettings(BlockPos spawn) {
		this.spawnPos = spawn;
	}

	@Override
	public int getStrongholdCount() {

		return 0;
	}

	@Override
	public int getBedrockFloorHeight() {

		return 0;
	}

	public BlockPos getSpawnPos() {
		return spawnPos;
	}

	public MindGenSettings setSpawnPos(BlockPos spawnPos) {
		this.spawnPos = spawnPos;
		return this;
	}

	@Override
	public BlockState getDefaultBlock() {
		return BlockInit.MIND_JELLY.get().getDefaultState();
	}

	@Override
	public BlockState getDefaultFluid() {
		return Blocks.WATER.getDefaultState();
	}

}
