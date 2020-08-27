package com.gm910.occentmod.capabilities.speciallocs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.api.util.IWorldTickable;
import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.api.util.NonNullMap;
import com.gm910.occentmod.capabilities.GMCaps;
import com.gm910.occentmod.capabilities.IModCapability;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class SpecialLocationManager implements INBTSerializable<CompoundNBT>, IModCapability<ServerWorld> {

	public static final ResourceLocation LOC = new ResourceLocation(OccultEntities.MODID, "speciallocations");

	private ServerWorld chunk;

	/**
	 * YXZ
	 */
	private Map<BlockPos, List<SpecialLocation>> pointMap = new NonNullMap<>(() -> new ArrayList<>());

	public static final Map<ResourceLocation, SpecialLocationType<?>> POINT_TYPES = new HashMap<>();

	public static ResourceLocation getForPoint(SpecialLocation poi) {
		SpecialLocationType<?> type = poi.getType();
		for (ResourceLocation key : POINT_TYPES.keySet()) {
			if (POINT_TYPES.get(key) == type) {
				return key;
			}
		}
		return null;
	}

	public static ResourceLocation getForType(SpecialLocationType<?> type) {
		for (ResourceLocation key : POINT_TYPES.keySet()) {
			if (POINT_TYPES.get(key) == type) {
				return key;
			}
		}
		return null;
	}

	public static <L extends SpecialLocation> SpecialLocationType<L> registerPointType(ResourceLocation loc,
			SpecialLocationType<L> type) {
		POINT_TYPES.put(loc, type);
		return type;
	}

	public static <L extends SpecialLocation> SpecialLocationType<L> registerPointType(String modid, String loc,
			SpecialLocationType<L> type) {
		return registerPointType(new ResourceLocation(modid, loc), type);
	}

	@Override
	public CompoundNBT serializeNBT() {
		pointMap.keySet().removeIf((block) -> {
			List<SpecialLocation> list = pointMap.get(block);
			return list.isEmpty();
		});
		CompoundNBT comp = new CompoundNBT();
		ListNBT list = GMNBT.makeList(pointMap.keySet(), (element) -> {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putLong("Pos", element.toLong());
			ListNBT list2 = new ListNBT();
			for (SpecialLocation point1 : pointMap.get(element)) {
				CompoundNBT c = new CompoundNBT();
				INBT data = point1.serialize(NBTDynamicOps.INSTANCE);
				c.put("Data", data);
				c.putString("Key", point1.getRegistryKey().toString());
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
		pointMap = GMNBT.createMap((ListNBT) nbt.get("List"), (c) -> {
			CompoundNBT m = (CompoundNBT) c;
			BlockPos pos = BlockPos.fromLong(m.getLong("Pos"));
			List<SpecialLocation> ls = GMNBT.createList((ListNBT) m.get("List"), (inbt) -> {
				CompoundNBT ztag = (CompoundNBT) inbt;
				ResourceLocation loc = new ResourceLocation(ztag.getString("Key"));
				SpecialLocationType<?> type = POINT_TYPES.get(loc);
				INBT data = ztag.get("Data");
				SpecialLocation point = type.getDeserializer().apply(new Dynamic<>(NBTDynamicOps.INSTANCE, data), pos,
						this);
				return point;
			});
			return new Pair<>(pos, ls);
		});
	}

	public static void registerType(ResourceLocation loc, SpecialLocationType<?> type) {
		POINT_TYPES.put(loc, type);
	}

	public List<SpecialLocation> get(BlockPos pos) {
		return pointMap.get(pos);
	}

	public void add(BlockPos pos, SpecialLocation point) {
		pointMap.get(pos).add(point);
		point.setManager(this);
		point.setPos(pos);
	}

	public void add(SpecialLocation point) {
		pointMap.get(point.getPos()).add(point);
		point.setManager(this);
	}

	public void remove(BlockPos pos, SpecialLocation point) {
		pointMap.get(pos).remove(point);

	}

	public void remove(SpecialLocation point) {
		pointMap.get(point.getPos()).remove(point);
	}

	public void remove(BlockPos pos, Predicate<SpecialLocation> point) {

		pointMap.get(pos).removeIf(point);
	}

	public void set(BlockPos pos, List<SpecialLocation> point) {
		pointMap.put(pos, point);
		point.forEach((e) -> {
			e.setManager(this);
			e.setPos(pos);
		});
	}

	public void clear(BlockPos pos) {
		pointMap.get(pos).clear();
	}

	public Map<BlockPos, List<SpecialLocation>> getPointMap() {
		return pointMap;
	}

	public void clearPoints() {
		this.pointMap.clear();
	}

	@Override
	public ServerWorld $getOwner() {
		// TODO Auto-generated method stub
		return chunk;
	}

	@Override
	public void $setOwner(ServerWorld e) {
		// TODO Auto-generated method stub
		chunk = e;
	}

	@SuppressWarnings("unchecked")
	public <T extends SpecialLocation> List<T> getPoints(BlockPos pos) {
		// TODO Auto-generated method stub
		return ModReflect.<List<T>>instanceOf(get(pos), List.class) ? (List<T>) get(pos) : new ArrayList<>();
	}

	public List<SpecialLocationType<?>> getTypesAt(BlockPos pos) {
		List<SpecialLocationType<?>> ls = new ArrayList<>();
		for (SpecialLocation loc : get(pos)) {
			if (!ls.contains(loc.getType())) {
				ls.add(loc.getType());
			}
		}
		return ls;
	}

	public List<SpecialLocation> getByPredicate(BlockPos pos, Predicate<SpecialLocation> predicate) {
		List<SpecialLocation> list = new ArrayList<>(pointMap.get(pos));
		list.removeIf(predicate.negate());
		return list;
	}

	public <T extends SpecialLocation> List<T> getByType(BlockPos pos, SpecialLocationType<T> type) {

		List<SpecialLocation> list2 = pointMap.get(pos);
		List<T> list = new ArrayList<>();
		for (SpecialLocation loc : list2) {
			if (type.getPredicate().test(loc)) {
				list.add((T) loc);
			}
		}
		return list;
	}

	public List<SpecialLocation> getAll(Predicate<SpecialLocation> type) {

		List<SpecialLocation> list = new ArrayList<>();
		for (BlockPos pos : pointMap.keySet()) {
			List<SpecialLocation> ls1 = getByPredicate(pos, type);
			list.addAll(ls1);
		}
		return list;
	}

	public List<SpecialLocation> getAllInRadius(Predicate<SpecialLocation> type, BlockPos center, double radius) {
		Stream<Chunk> chunks = ChunkPos.getAllInBox(new ChunkPos(center), (int) radius)
				.map((e) -> (Chunk) chunk.getWorld().getChunk(e.asBlockPos()));
		List<SpecialLocation> ls = new ArrayList<>();
		List<SpecialLocation> ml = this.getAll(type);
		for (SpecialLocation loc : new ArrayList<>(ml)) {
			if (!loc.getPos().withinDistance(center, radius)) {
				ml.remove(loc);
			}
		}
		ls.addAll(ml);
		return ls;
	}

	public void tick(WorldTickEvent event, long time, long dayTime) {
		List<SpecialLocation> list = getAll();
		list.forEach((element) -> {
			if (element instanceof IWorldTickable && (((IWorldTickable) element).canTickWhileUnloaded()
					|| event.world.isBlockLoaded(element.getPos()))) {
				((IWorldTickable) element).tick(event, time, dayTime);
			}
		});
	}

	public List<SpecialLocation> getAll() {
		List<SpecialLocation> list = new ArrayList<>();
		for (BlockPos pos : this.pointMap.keySet()) {
			list.addAll(pointMap.get(pos));
		}
		return list;
	}

	public void chunkload(ChunkEvent.Load event) {
		for (SpecialLocationType<?> type : POINT_TYPES.values()) {
			NonNullMap<BlockPos, ?> positions = type.generate(event, this);
			positions.forEach((bp, list) -> {
				List<SpecialLocation> ls = new ArrayList<>((List) bp);
				pointMap.put(bp, ls);
			});
		}
	}

	public void blockchange(BlockEvent.NeighborNotifyEvent event) {
		List<SpecialLocation> list = getAll();
		list.forEach((element) -> {
			element.blockChange(event, this);
		});

		for (SpecialLocationType<?> type : POINT_TYPES.values()) {
			type.getOnChange().accept(event, this);
		}
	}

	public ServerWorld getWorld() {
		return chunk;
	}

	public static SpecialLocationManager getForWorld(ServerWorld world) {
		return world.getCapability(GMCaps.SPECIAL_LOCS).orElse(null);
	}

}
