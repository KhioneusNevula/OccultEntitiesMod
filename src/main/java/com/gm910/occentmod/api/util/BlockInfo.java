package com.gm910.occentmod.api.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public class BlockInfo implements INBTSerializable<CompoundNBT>, IDynamicSerializable {

	private BlockState state;
	private TileEntity tile = null;

	public BlockInfo() {
	}

	public BlockInfo(CompoundNBT nbt) {
		this.deserializeNBT(nbt);
		if (state == null)
			throw new IllegalArgumentException("State of block is unreadable in nbt tag");
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

	public BlockInfo withState(BlockState state) {
		return new BlockInfo(state);
	}

	public BlockInfo withTile(TileEntity te) {
		return new BlockInfo(state, te);
	}

	/**
	 * Places block and returns the info of the block that was previously there
	 * 
	 * @param world
	 * @param pos
	 * @return
	 */
	public BlockInfo place(World world, BlockPos pos) {
		BlockState state1 = world.getBlockState(pos);
		world.setBlockState(pos, this.state);
		TileEntity tile1 = world.getTileEntity(pos);
		if (this.tile != null) {
			world.setTileEntity(pos, this.tile);
		}
		return new BlockInfo(state1, tile1);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("State", NBTUtil.writeBlockState(state));
		if (tile != null)
			nbt.put("Tile", tile.serializeNBT());

		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.state = NBTUtil.readBlockState(nbt.getCompound("State"));
		if (nbt.contains("Tile"))
			this.tile = TileEntity.create(nbt.getCompound("Tile"));

	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		// TODO Auto-generated method stub
		return ops.createString(this.serializeNBT().getString());
	}

	public static BlockInfo fromDynamic(Dynamic<?> op) {
		String data = op.asString("");
		try {
			CompoundNBT nbt = JsonToNBT.getTagFromJson(data);
			return new BlockInfo(nbt);
		} catch (CommandSyntaxException e) {
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.serializeNBT().equals(((BlockInfo) obj).serializeNBT());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.serializeNBT().toString();
	}

	public static BlockInfo fromString(String s) {
		try {
			return new BlockInfo(JsonToNBT.getTagFromJson(s));
		} catch (CommandSyntaxException e) {
		}
		return null;
	}

}
