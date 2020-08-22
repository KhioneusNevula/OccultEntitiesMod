package com.gm910.occentmod.api.util;

import java.util.List;
import java.util.Spliterator.OfInt;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.client.Minecraft;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants.NBT;

public class ServerPos extends BlockPos implements IDynamicSerializable {

	private int d;

	public ServerPos(GlobalPos source) {
		this(source.getPos(), source.getDimension());
	}

	public ServerPos(ServerPos source) {
		super(source);
		this.d = source.d;
	}

	public ServerPos(Entity source) {
		super(source);
		d = source.dimension.getId();
	}

	public ServerPos(TileEntity source) {
		super(source.getPos());
		d = source.getWorld().dimension.getType().getId();
	}

	public ServerPos(Vec3d vec, int d) {
		super(vec);
		this.d = d;
	}

	public ServerPos(IPosition p_i50799_1_, int d) {
		super(p_i50799_1_);
		this.d = d;

	}

	public ServerPos(Vec3i source, int d) {
		super(source);
		this.d = d;
		// TODO Auto-generated constructor stub
	}

	public ServerPos(Vec3d vec, DimensionType d) {
		super(vec);
		this.d = d.getId();
	}

	public ServerPos(IPosition p_i50799_1_, DimensionType d) {
		super(p_i50799_1_);
		this.d = d.getId();

	}

	public ServerPos(Vec3i source, DimensionType d) {
		super(source);
		this.d = d.getId();
		// TODO Auto-generated constructor stub
	}

	public ServerPos(int x, int y, int z, int d) {
		super(x, y, z);
		this.d = d;
	}

	public ServerPos(double x, double y, double z, int d) {
		super(x, y, z);
		this.d = d;
	}

	public ServerPos(int x, int y, int z, DimensionType d) {
		super(x, y, z);
		this.d = d.getId();
	}

	public ServerPos(double x, double y, double z, DimensionType d) {
		super(x, y, z);
		this.d = d.getId();
	}

	public int getD() {
		return d;
	}

	public DimensionType getDimension() {
		return DimensionType.getById(d);
	}

	public ServerPos setDimension(int d) {
		return new ServerPos(this);
	}

	public ServerPos setDimension(DimensionType d) {
		return new ServerPos(this);
	}

	public BlockPos getPos() {
		return new BlockPos(this);
	}

	public BlockPos castToPos() {
		return (BlockPos) this;
	}

	@Override
	public boolean equals(Object p_equals_1_) {
		if (!(p_equals_1_ instanceof ServerPos)) {
			if (p_equals_1_ instanceof GlobalPos) {
				return this.getPos().equals(p_equals_1_)
						&& this.getDimension() == ((GlobalPos) p_equals_1_).getDimension();
			} else {
				return super.equals(p_equals_1_);
			}
		} else {
			return super.equals(p_equals_1_) && ((ServerPos) p_equals_1_).d == d;
		}
	}

	public ServerPos up(int n) {
		return new ServerPos(super.up(n), d);
	}

	public ServerPos up() {
		return up(1);
	}

	public ServerPos down(int n) {
		return new ServerPos(super.down(n), d);
	}

	public ServerPos down() {
		return down(1);
	}

	public ServerPos north(int n) {
		return new ServerPos(super.north(n), d);
	}

	public ServerPos north() {
		// TODO Auto-generated method stub
		return new ServerPos(super.north(), d);
	}

	public ServerPos south(int n) {
		return new ServerPos(super.south(n), d);
	}

	public ServerPos south() {
		// TODO Auto-generated method stub
		return new ServerPos(super.south(), d);
	}

	public ServerPos east(int n) {
		return new ServerPos(super.east(n), d);
	}

	public ServerPos east() {
		// TODO Auto-generated method stub
		return new ServerPos(super.east(), d);
	}

	public ServerPos west(int n) {
		return new ServerPos(super.west(n), d);
	}

	public ServerPos west() {
		// TODO Auto-generated method stub
		return new ServerPos(super.west(), d);
	}

	public ServerPos offset(Direction facing, int n) {
		return new ServerPos(super.offset(facing, n), d);
	}

	public ServerPos offset(Direction facing) {
		return new ServerPos(super.offset(facing), d);
	}

	public ServerPos add(double x, double y, double z) {
		return new ServerPos(super.add(x, y, z), d);
	}

	public ServerPos add(int x, int y, int z) {
		return new ServerPos(super.add(x, y, z), d);
	}

	public ServerPos add(Vec3i vec) {
		return new ServerPos(super.add(vec), d);
	}

	@Override
	public String func_229422_x_() {
		return super.func_229422_x_() + ", " + d;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ())
				.add("d", this.getD()).toString();
	}

	public ServerPos toImmutable() {
		return this;
	}

	public ServerPos subtract(Vec3i vec) {
		// TODO Auto-generated method stub
		return new ServerPos(super.subtract(vec), d);
	}

	public ServerPos rotate(Rotation rotationIn) {
		// TODO Auto-generated method stub
		return new ServerPos(super.rotate(rotationIn), d);
	}

	@Override
	public <T> T serialize(DynamicOps<T> p_218175_1_) {
		// TODO Auto-generated method stub
		return p_218175_1_.createIntList(IntStream.of(this.getX(), this.getY(), this.getZ(), this.getD()));
	}

	/**
	 * Gets blockpos from nbt OR serverpos depending on whether the nbt is
	 * configured for a serverpos or blockpos
	 * 
	 * @param nbt
	 * @return
	 */
	public static BlockPos bpFromNBT(CompoundNBT nbt) {

		if (nbt.contains("D", NBT.TAG_INT)) {
			return new ServerPos(nbt.getInt("X"), nbt.getInt("Y"), nbt.getInt("Z"), nbt.getInt("D"));
		} else {
			return new BlockPos(nbt.getInt("X"), nbt.getInt("Y"), nbt.getInt("Z"));
		}
	}

	/**
	 * If returntype is not a serverpos, returns null;
	 */
	public static ServerPos fromNBT(CompoundNBT nbt) {
		BlockPos pos = bpFromNBT(nbt);
		return pos instanceof ServerPos ? (ServerPos) pos : null;
	}

	/**
	 * Works for blockpos or serverpos
	 * 
	 * @param pos
	 * @return
	 */
	public static CompoundNBT toNBT(BlockPos pos) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("X", pos.getX());
		nbt.putInt("Y", pos.getY());
		nbt.putInt("Z", pos.getZ());
		if (pos instanceof ServerPos)
			nbt.putInt("D", ((ServerPos) pos).getD());
		return nbt;
	}

	public CompoundNBT toNBT() {
		return ServerPos.toNBT(this);
	}

	public World getWorld(MinecraftServer server) {
		return server.getWorld(this.getDimension());
	}

	public boolean isClientInWorld(Minecraft mc) {
		return mc.world.dimension.getType().getId() == this.d;
	}

	public static Entity getEntityFromUUID(UUID en, MinecraftServer server) {
		for (ServerWorld world : server.getWorlds()) {
			if (world.getEntityByUuid(en) != null)
				return world.getEntityByUuid(en);
		}
		return null;
	}

	public static Entity getEntityFromUUID(UUID en, World world, BlockPos pos, double range) {
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class, (new AxisAlignedBB(pos)).grow(range),
				(e) -> e.getUniqueID().equals(en));
		if (list.isEmpty())
			return null;
		return list.get(0);
	}

	public static Entity getEntityFromID(int en, ServerWorld server) {

		return server.getEntityByID(en);
	}

	public static <T> ServerPos deserialize(Dynamic<T> p_218286_0_) {
		OfInt ofint = p_218286_0_.asIntStream().spliterator();
		int[] aint = new int[4];
		if (ofint.tryAdvance((Integer p_218285_1_) -> {
			aint[0] = p_218285_1_;
		}) && ofint.tryAdvance((Integer p_218280_1_) -> {
			aint[1] = p_218280_1_;
		}) && ofint.tryAdvance((Integer p_218284_1_) -> {
			aint[2] = p_218284_1_;
		})) {
			ofint.tryAdvance((Integer p_218282_1_) -> {
				aint[3] = p_218282_1_;
			});
		}

		return new ServerPos(aint[0], aint[1], aint[2], aint[3]);
	}

	public static <T> T serializeVec3d(Vec3d vec, DynamicOps<T> ops) {

		return ops.createList(
				Lists.newArrayList(ops.createDouble(vec.x), ops.createDouble(vec.y), ops.createDouble(vec.z)).stream());
	}

	public static <T> Vec3d deserializeVec3d(Dynamic<T> dyn) {
		List<Double> ls = dyn.asStream().map((d) -> d.asDouble(0)).collect(Collectors.toList());
		return new Vec3d(ls.get(0), ls.get(1), ls.get(2));
	}

}
