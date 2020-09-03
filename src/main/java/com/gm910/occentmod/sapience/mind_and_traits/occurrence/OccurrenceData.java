package com.gm910.occentmod.sapience.mind_and_traits.occurrence;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.empires.Empire;
import com.google.common.collect.Sets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class OccurrenceData extends WorldSavedData {

	public static final String NAME = OccultEntities.MODID + "_occurrences";

	private Set<Occurrence> occurrences = new HashSet<>();

	private ServerWorld world;

	public OccurrenceData(String name) {
		super(name);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public OccurrenceData() {
		this(NAME);
	}

	public ServerWorld getWorld() {
		return world;
	}

	public OccurrenceData setWorld(ServerWorld world) {
		this.world = world;
		return this;
	}

	public void addOccurrence(Occurrence oc) {
		this.occurrences.add(oc);
	}

	public void removeOccurrence(Occurrence oc) {
		this.occurrences.remove(oc);
	}

	public Set<Occurrence> getWithinEmpire(Empire em) {
		Set<ChunkPos> chunks = em.getChunkPositions(this.world.dimension.getType());
		return filter((occurrence) -> {
			for (ChunkPos pos : chunks) {
				int xMin = pos.getXStart();
				int zMin = pos.getZStart();
				int xMax = pos.getXEnd();
				int zMax = pos.getZEnd();
				if (occurrence.position.x >= xMin && occurrence.position.z >= zMin && occurrence.position.x <= xMax
						&& occurrence.position.z <= zMax) {
					return true;
				}
			}
			return false;
		});
	}

	public Set<Occurrence> getVisible(LivingEntity e) {
		return filter((occ) -> {
			return occ.canOccurrenceBeSeen(e);
		});
	}

	public Set<Occurrence> filter(Predicate<? super Occurrence> pred) {
		return this.occurrences.stream().filter(pred).collect(Collectors.toSet());
	}

	public Set<Occurrence> getAll() {
		return new HashSet<>(occurrences);
	}

	public void clearAllOccurrences() {
		this.occurrences.clear();
	}

	@Override
	public void read(CompoundNBT nbt) {
		occurrences = Sets.newHashSet(GMNBT.createList((ListNBT) nbt.get("Ocs"),
				(e) -> OccurrenceType.deserialize(world, GMNBT.makeDynamic(e))));
	}

	@SubscribeEvent
	public void tick(WorldTickEvent event) {
		if (!event.world.isRemote && OccurrenceData.get((ServerWorld) event.world) == this) {
			for (Occurrence oc : new HashSet<>(this.occurrences)) {
				oc.tick(event, event.world.getGameTime(), event.world.getDayTime());
				if (oc.shouldEnd()) {
					occurrences.remove(oc);
				}
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {

		compound.put("Ocs", GMNBT.makeList(occurrences, (e) -> e.serialize(NBTDynamicOps.INSTANCE)));
		return compound;
	}

	public static OccurrenceData get(ServerWorld world) {
		DimensionSavedDataManager dimdat = world.getSavedData();
		return dimdat.getOrCreate(() -> {
			return new OccurrenceData().setWorld(world);
		}, NAME);
	}

}
