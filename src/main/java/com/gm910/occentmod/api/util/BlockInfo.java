package com.gm910.occentmod.api.util;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public class BlockInfo implements INBTSerializable<CompoundNBT>{

	private BlockState state;
	private TileEntity tile = null;
	
	public BlockInfo() {}
	
	public BlockInfo(CompoundNBT nbt) {
		this.deserializeNBT(nbt);
	}
	
	public BlockInfo(BlockState state) {
		this.state = state;
	}
	
	public BlockInfo(BlockState state, TileEntity tile) {
		this.state = state;
		this.tile = tile;
	}
	
	public BlockInfo(BlockState state, CompoundNBT tile) {
		this.state = state;
		this.tile = TileEntity.create(tile);
	}
	
	public BlockInfo(World world, BlockPos pos) {
		this.state = world.getBlockState(pos);
		this.tile = world.getTileEntity(pos);
	}
	
	
	public BlockState getState() {
		return state;
	}
	
	public TileEntity getTile() {
		return tile;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("State", NBTUtil.writeBlockState(state));
		if (tile != null) nbt.put("Tile", tile.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.state = NBTUtil.readBlockState(nbt.getCompound("State"));
		if (nbt.contains("Tile")) this.tile = TileEntity.create(nbt.getCompound("Tile"));
	}

}
