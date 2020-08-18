package com.gm910.occentmod.blocks.worldcontroller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.gm910.occentmod.api.util.BlockInfo;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.init.TileInit;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class WorldControllerTileEntity extends TileEntity implements ITickableTileEntity {
	private DimensionType containedDimType = DimensionType.OVERWORLD;

	private ChunkPos containedChunk = new ChunkPos(0, 0);
	private int minYLevel = 100;
	Map<BlockPos, BlockInfo> unitHashMap = new HashMap<>();
	public int upb = 4;

	public WorldControllerTileEntity() {
		super(TileInit.WORLD_CONTROLLER.get()); // tetype

	}

	boolean isEnchanted = false;
	boolean useManual = false;

	public ChunkPos getContainedChunk() {
		return containedChunk;
	}

	public World getContainedWorld() {
		if (this.world.isRemote) {
			return this.world;
		} else {
			return this.world.getServer().getWorld(containedDimType);
		}
	}

	public DimensionType getContainedDimType() {
		return containedDimType;
	}

	public int getMinYLevel() {
		return minYLevel;
	}

	@Override
	public void read(CompoundNBT compound) {
		this.containedChunk = new ChunkPos(compound.getLong("Chunk"));
		this.containedDimType = DimensionType.getById(compound.getInt("Dim"));
		this.minYLevel = compound.getInt("MinY");
		this.unitHashMap = GMNBT.createMap((ListNBT) compound.get("Blocks"), (el) -> {
			return Pair.of(ServerPos.bpFromNBT(((CompoundNBT) el).getCompound("Pos")),
					new BlockInfo(((CompoundNBT) el).getCompound("State")));
		});
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		if (containedDimType != null) {
			compound.putInt("Dim", containedDimType.getId());
		}
		if (containedChunk != null) {
			compound.putLong("Chunk", containedChunk.asLong());
		}
		compound.putInt("MinY", minYLevel);
		compound.put("Blocks", GMNBT.makeList(unitHashMap.entrySet(), (entry) -> {
			CompoundNBT nbt = new CompoundNBT();
			nbt.put("Pos", ServerPos.toNBT(entry.getKey()));
			nbt.put("State", entry.getValue().serializeNBT());
			return nbt;
		}));
		return compound;
	}

	public void setContainedChunk(ChunkPos containedChunk) {
		this.containedChunk = containedChunk;
	}

	public void setContainedWorld(DimensionType containedWorld) {
		this.containedDimType = containedWorld;
	}

	public void setMinYLevel(int minYLevel) {
		this.minYLevel = minYLevel;
	}

	public Map<BlockPos, BlockInfo> getUnitHashMap() {
		return unitHashMap;
	}

	@Override
	public TileEntity getTileEntity() {
		return this;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		read(nbt);
	}

	@Override
	public CompoundNBT serializeNBT() {
		return write(new CompoundNBT());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		deserializeNBT(pkt.getNbtCompound());
	}

	@Override
	public void handleUpdateTag(CompoundNBT tag) {
		this.read(tag);
	}

	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = this.serializeNBT();
		return new SUpdateTileEntityPacket(this.pos, 1, nbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return super.getUpdateTag();
	}

	@Override
	public BlockState getBlockState() {
		return super.getBlockState();
	}

	@Override
	public void tick() {
		if (world.isRemote)
			return;
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 256; y++) {
				for (int z = 0; z < 16; z++) {
					BlockPos bp = new BlockPos(this.containedChunk.x + x, y, this.containedChunk.z + z);
					this.unitHashMap.put(bp, new BlockInfo(this.world, bp));
				}
			}
		}
	}
}