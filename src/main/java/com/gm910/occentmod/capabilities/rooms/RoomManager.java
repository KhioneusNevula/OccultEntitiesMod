package com.gm910.occentmod.capabilities.rooms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.api.util.IWorldTickable;
import com.gm910.occentmod.api.util.NonNullMap;
import com.gm910.occentmod.capabilities.GMCaps;
import com.gm910.occentmod.capabilities.IModCapability;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class RoomManager implements INBTSerializable<CompoundNBT>, IModCapability<ServerWorld> {

	public static final ResourceLocation LOC = new ResourceLocation(OccultEntities.MODID, "rooms");

	private ServerWorld world;

	/**
	 * YXZ
	 */
	private Map<ChunkPos, Set<Room>> roomMap = new NonNullMap<>(() -> new HashSet<>());

	@Override
	public CompoundNBT serializeNBT() {
		roomMap.keySet().removeIf((block) -> {
			Set<Room> list = roomMap.get(block);
			return list.isEmpty();
		});
		CompoundNBT comp = new CompoundNBT();
		ListNBT list = GMNBT.makeList(roomMap.keySet(), (element) -> {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putLong("Pos", element.asLong());
			ListNBT list2 = new ListNBT();
			for (Room point1 : roomMap.get(element)) {
				CompoundNBT c = new CompoundNBT();
				INBT data = point1.serialize(NBTDynamicOps.INSTANCE);
				c.put("Data", data);
				list2.add(c);
			}
			nbt.put("List", list2);
			return nbt;
		});
		comp.put("List", list);
		return comp;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		roomMap = GMNBT.createMap((ListNBT) nbt.get("List"), (c) -> {
			CompoundNBT m = (CompoundNBT) c;
			ChunkPos pos = new ChunkPos(m.getLong("Pos"));
			Set<Room> ls = new HashSet<>(GMNBT.createList((ListNBT) m.get("List"), (inbt) -> {
				CompoundNBT ztag = (CompoundNBT) inbt;
				INBT data = ztag.get("Data");
				Dynamic<?> dyn = new Dynamic<>(NBTDynamicOps.INSTANCE, data);

				Room room = new Room(this.world, dyn);
				return room;
			}));
			return new Pair<>(pos, ls);
		});
	}

	public Set<Room> get(ChunkPos pos) {
		return roomMap.get(pos);
	}

	public Room get(BlockPos pos) {
		ChunkPos f = new ChunkPos(pos);
		Set<Room> ls = this.roomMap.get(f);
		for (Room room : ls) {
			if (room.contains(pos)) {
				return room;
			}
		}
		return null;
	}

	public void add(Room room) {
		this.roomMap.get(new ChunkPos(room.getImportantPos())).add(room);
	}

	public Set<Room> createRooms(BlockPos start) {
		if (this.get(start) != null) {
			return Sets.newHashSet();
		}
		Set<Room> rooms = Room.create(world, start);
		for (Room room : rooms) {
			this.add(room);
		}

		return rooms;
	}

	public void remove(Room room) {
		this.roomMap.remove(room);
	}

	public void remove(BlockPos room) {
		if (this.get(room) != null) {
			roomMap.remove(this.get(room));
		}
	}

	public void clear(ChunkPos pos) {
		roomMap.get(pos).clear();
	}

	public void clearRooms() {
		this.roomMap.clear();
	}

	@Override
	public ServerWorld $getOwner() {
		// TODO Auto-generated method stub
		return world;
	}

	@Override
	public void $setOwner(ServerWorld e) {
		// TODO Auto-generated method stub
		world = e;
	}

	public Set<Room> getAll(Predicate<Room> type, Set<ChunkPos> chunks) {

		Set<Room> list = new HashSet<>();
		for (ChunkPos pos : chunks) {
			Set<Room> ls1 = roomMap.get(pos).stream().filter(type).collect(Collectors.toSet());
			list.addAll(ls1);
		}
		return list;
	}

	public Set<Room> getAllInRadius(Predicate<Room> type, BlockPos center, double radius) {
		Set<ChunkPos> chunks = ChunkPos.getAllInBox(new ChunkPos(center), (int) radius).collect(Collectors.toSet());
		Set<Room> ls = new HashSet<>();
		Set<Room> ml = this.getAll(type, chunks);
		for (Room loc : new ArrayList<>(ml)) {
			Set<BlockPos> poses = loc.allPositions();
			for (BlockPos pos1 : poses) {
				if (pos1.withinDistance(center, radius)) {
					ls.add(loc);
				}
			}
		}
		return ls;
	}

	public void tick(WorldTickEvent event, long time, long dayTime) {
		roomMap.forEach((pos, element) -> {
			if (element instanceof IWorldTickable
					&& (((IWorldTickable) element).canTickWhileUnloaded() || event.world.chunkExists(pos.x, pos.z))) {
				((IWorldTickable) element).tick(event, time, dayTime);
			}
		});
	}

	public Set<Room> getAll() {
		Set<Room> list = new HashSet<>();
		for (ChunkPos pos : this.roomMap.keySet()) {
			list.addAll(roomMap.get(pos));
		}
		return list;
	}

	public void chunkload(ChunkEvent.Load event) {

	}

	public void blockchange(BlockEvent.NeighborNotifyEvent event) {
		if (this.get(event.getPos()) != null) {
			this.get(event.getPos()).refresh();
		}
		for (Direction dir : event.getNotifiedSides()) {
			if (this.get(event.getPos().offset(dir)) != null) {
				this.get(event.getPos().offset(dir)).refresh();
			}
		}
	}

	public ServerWorld getWorld() {
		return world;
	}

	public static RoomManager getForWorld(ServerWorld world) {
		return world.getCapability(GMCaps.ROOMS).orElse(null);
	}

}
