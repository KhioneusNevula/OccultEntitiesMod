package com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.util.GMNBT;
import com.google.common.collect.Sets;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class OccurrenceData extends WorldSavedData {

	public static final String NAME = OccultEntities.MODID + "_occurrences";

	private Set<Occurrence> occurrences = new HashSet<>();

	public OccurrenceData(String name) {
		super(name);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public OccurrenceData() {
		this(NAME);
	}

	public void addOccurrence(Occurrence oc) {
		this.occurrences.add(oc);
	}

	public void removeOccurrence(Occurrence oc) {
		this.occurrences.remove(oc);
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
		occurrences = Sets.newHashSet(
				GMNBT.createList((ListNBT) nbt.get("Ocs"), (e) -> OccurrenceType.deserialize(GMNBT.makeDynamic(e))));
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
			return new OccurrenceData();
		}, NAME);
	}

}
