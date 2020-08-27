package com.gm910.occentmod.capabilities.rooms;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.BlockInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class Room implements IDynamicSerializable {

	/**
	 * Maps positions to a height to determine the height of the room at a certain
	 * position
	 */
	private Long2IntMap inside = new Long2IntOpenHashMap();

	/**
	 * The highest a room's ceiling can be
	 */
	public static final int MAX_HEIGHT = 7;

	/**
	 * The biggest a room can physically be
	 */
	public static final int SIZE_TOLERANCE = 1000;

	// private LongSet edges = new LongOpenHashBigSet();

	private Long2ObjectMap<BlockInfo> roomParts = new Long2ObjectOpenHashMap<>();

	private ServerWorld world;

	private BlockPos importantPos;

	private boolean invalid;

	public static final Set<Material> GENERAL_WALL_MATERIALS = ImmutableSet.of(Material.BARRIER, Material.WOOD,
			Material.SAND, Material.EARTH, Material.GLASS, Material.IRON, Material.WOOL, Material.PACKED_ICE,
			Material.ROCK, Material.ORGANIC);

	private Set<Material> wallMaterials = ImmutableSet.copyOf(GENERAL_WALL_MATERIALS);

	public static final BiPredicate<ServerWorld, BlockPos> GENERAL_BLOCK_CHECKER = (world, pos) -> {
		return world.getPointOfInterestManager().getType(pos).isPresent();
	};

	private BiPredicate<ServerWorld, BlockPos> checkBlocks = GENERAL_BLOCK_CHECKER;

	public static final BiPredicate<ServerWorld, BlockPos> CANCELER = (world, pos) -> {
		return false;
	};

	private BiPredicate<ServerWorld, BlockPos> canceler = CANCELER;

	public Room(ServerWorld world, BlockPos createdFrom) {

		setImportantPos(createdFrom);
		setWorld(world);
	}

	private Room(ServerWorld world, BlockPos createdFrom, Long2IntMap inside, Long2ObjectMap<BlockInfo> parts,
			BiPredicate<ServerWorld, BlockPos> checkBlocks, Set<Material> wallMaterials) {
		setInside(inside);
		setRoomParts(parts);
		setImportantPos(createdFrom);
		setWorld(world);
		setCheckBlockPredicate(checkBlocks);
		setWallMaterials(wallMaterials);
	}

	public Room setCanceler(BiPredicate<ServerWorld, BlockPos> canceler) {
		this.canceler = canceler;
		return this;
	}

	public BiPredicate<ServerWorld, BlockPos> getCanceler() {
		return canceler;
	}

	public void copy(Room other) {
		this.setInside(other.inside);
		this.setRoomParts(other.roomParts);
		this.setImportantPos(other.importantPos);
		this.setWallMaterials(other.wallMaterials);
		this.setCheckBlockPredicate(other.checkBlocks);
		this.setWorld(other.world);
	}

	public static Set<Room> create(ServerWorld world, BlockPos createdFrom) {
		Set<Room> rooms = new HashSet<>();
		rooms = Room.makeRoom(world, createdFrom);
		return rooms;
	}

	public BiPredicate<ServerWorld, BlockPos> getCheckBlockPredicate() {
		return checkBlocks;
	}

	public Room setCheckBlockPredicate(BiPredicate<ServerWorld, BlockPos> checkBlocks) {
		this.checkBlocks = checkBlocks;
		return this;
	}

	public static boolean shouldAddBlock(ServerWorld world, BlockPos pos,
			BiPredicate<ServerWorld, BlockPos> checkBlocks) {
		return checkBlocks.test(world, pos);
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public Set<Material> getWallMaterials() {
		return wallMaterials;
	}

	public Room setWallMaterials(Set<Material> wallMaterials) {
		this.wallMaterials = wallMaterials;
		return this;
	}

	public BlockPos getImportantPos() {
		return importantPos;
	}

	public LongSet getInside() {
		return inside.keySet();
	}

	public Set<BlockPos> allPositions() {
		Set<BlockPos> poses = Sets.newHashSet();
		for (long lpos : this.inside.keySet()) {
			int height = inside.get(lpos);
			BlockPos pos = BlockPos.fromLong(lpos);
			for (int y = 0; y <= height; y++) {
				poses.add(pos.add(0, y, 0));
			}
		}
		return poses;
	}

	public boolean contains(BlockPos pos) {
		return this.allPositions().contains(pos);
	}

	public ServerWorld getWorld() {
		return world;
	}

	public void setImportantPos(BlockPos importantPos) {
		this.importantPos = importantPos;
	}

	public void setInside(Long2IntMap inside) {
		this.inside = inside;
	}

	public void setWorld(ServerWorld world) {
		this.world = world;
	}

	public static boolean validWallMaterial(ServerWorld world, BlockPos pos, Set<Material> wallMaterials) {
		return wallMaterials.contains(world.getBlockState(pos).getMaterial());
	}

	public static boolean canMoveToNextPos(ServerWorld world, BlockPos check, Set<Material> materials,
			BiPredicate<ServerWorld, BlockPos> shouldAdd) {
		return (!validWallMaterial(world, check.offset(Direction.UP), materials)
				|| shouldAddBlock(world, check.offset(Direction.UP), shouldAdd))
				&& (!validWallMaterial(world, check, materials) || shouldAddBlock(world, check, shouldAdd));
	}

	public static Set<Room> makeRoom(ServerWorld world, BlockPos start) {
		Direction[] horizontals = (Sets.newHashSet(Direction.Plane.HORIZONTAL)).toArray(new Direction[0]);
		Direction[] verticals = (Sets.newHashSet(Direction.Plane.VERTICAL)).toArray(new Direction[0]);
		Object2IntMap<BlockPos> nextPositions = new Object2IntOpenHashMap<>();
		for (Direction dir : horizontals) {
			BlockPos check = start.offset(dir);
			if (canMoveToNextPos(world, check, GENERAL_WALL_MATERIALS, GENERAL_BLOCK_CHECKER)
					&& !CANCELER.test(world, check.offset(Direction.UP)) && !CANCELER.test(world, check)) {
				int height = 0;
				BlockPos neqst = check;
				for (int y = 1; y <= MAX_HEIGHT || height > 0; y++) {
					neqst = check.add(0, y, 0);
					if (!canMoveToNextPos(world, neqst.up(), GENERAL_WALL_MATERIALS, GENERAL_BLOCK_CHECKER)
							&& !CANCELER.test(world, check.offset(Direction.UP)) && !CANCELER.test(world, check)) {
						height = y;
						break;
					}
				}
				if (height > 0) {
					nextPositions.put(check, height);
				}
			}
		}
		if (nextPositions.isEmpty()) {
			return Sets.newHashSet();
		}
		Set<Room> rooms = new HashSet<>();
		for (BlockPos posflood : new HashSet<>(nextPositions.keySet())) {
			Long2IntMap shape = new Long2IntOpenHashMap();
			shape.put(posflood.toLong(), nextPositions.getInt(posflood));
			Long2ObjectMap<BlockInfo> significantPoints = Long2ObjectMaps.emptyMap();
			boolean foundRoom = floodFill(world, posflood, shape, significantPoints, GENERAL_WALL_MATERIALS,
					GENERAL_BLOCK_CHECKER, CANCELER);
			if (foundRoom) {
				rooms.add(
						new Room(world, start, shape, significantPoints, GENERAL_BLOCK_CHECKER, GENERAL_WALL_MATERIALS)
								.setCanceler(CANCELER));
			}
		}
		return rooms;
	}

	public static boolean floodFill(ServerWorld world, BlockPos startPos, Long2IntMap shape,
			Long2ObjectMap<BlockInfo> sigPoints, Set<Material> wallMaterials,
			BiPredicate<ServerWorld, BlockPos> shouldAdd, BiPredicate<ServerWorld, BlockPos> canceler) {
		Direction[] horizontals = (Sets.newHashSet(Direction.Plane.HORIZONTAL)).toArray(new Direction[0]);
		if (shouldAddBlock(world, startPos, shouldAdd)) {
			sigPoints.put(startPos.toLong(), new BlockInfo(world, startPos));
		}
		for (Direction dir : horizontals) {
			BlockPos check = startPos.offset(dir);
			if (canMoveToNextPos(world, check, wallMaterials, shouldAdd)) {
				if (canceler.test(world, check.offset(Direction.UP)) || canceler.test(world, check)) {
					return false;
				} else {
					int height = 0;
					BlockPos neqst = check;
					for (int y = 1; y <= MAX_HEIGHT || height > 0; y++) {
						neqst = check.add(0, y, 0);
						if (!canMoveToNextPos(world, neqst.up(), wallMaterials, shouldAdd)
								&& !canceler.test(world, check.offset(Direction.UP)) && !canceler.test(world, check)) {
							height = y + 1;
							break;
						}
					}
					if (height > 0) {
						shape.put(check.toLong(), height);
						boolean continues = floodFill(world, check, shape, sigPoints, wallMaterials, shouldAdd,
								canceler);
						if (!continues) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public Long2ObjectMap<BlockInfo> getRoomParts() {
		return roomParts;
	}

	public void setRoomParts(Long2ObjectMap<BlockInfo> roomParts) {
		this.roomParts = roomParts;
	}

	/**
	 * Refreshes room
	 */
	public void refresh() {
		this.roomParts.clear();
		this.inside.clear();

		Set<Room> rooms = Room.makeRoom(world, importantPos);
		for (Room room : rooms) {
			Set<Long> poses = this.inside.keySet().stream().filter((l) -> !BlockPos.fromLong(l).equals(importantPos))
					.collect(Collectors.toSet());
			if (poses.stream().findAny().isPresent()) {
				if (room.inside.containsKey(poses.stream().findAny().get().longValue())) {
					this.copy(room);
					return;
				}
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		return this.inside.keySet().toLongArray().equals(((Room) obj).inside.keySet().toLongArray());
	}

	public Room(ServerWorld world, Dynamic<?> dyn) {
		setWorld(world);
		deserialize(dyn);
	}

	public void deserialize(Dynamic<?> dyn) {
		setImportantPos(BlockPos.deserialize(dyn.get("center").get().get()));
		setInside(new Long2IntOpenHashMap(
				ImmutableMap.copyOf(dyn.get("inside").asMap((e) -> e.asLong(0), (e) -> e.asInt(0)))));
		setRoomParts(new Long2ObjectOpenHashMap<>(
				ImmutableMap.copyOf(dyn.get("parts").asMap((e) -> e.asLong(0), BlockInfo::fromDynamic))));
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T longlist = ops.createMap(inside.long2IntEntrySet().stream()
				.map((entry) -> Pair.of(ops.createLong(entry.getLongKey()), ops.createInt(entry.getIntValue())))
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		T pos = importantPos.serialize(ops);
		T partsobj = ops.createMap(roomParts.long2ObjectEntrySet().stream().map((entry) -> {
			return Pair.of(ops.createLong(entry.getLongKey()), entry.getValue().serialize(ops));
		}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		return ops.createMap(ImmutableMap.of(ops.createString("center"), pos, ops.createString("inside"), longlist,
				ops.createString("parts"), partsobj));
	}

}
