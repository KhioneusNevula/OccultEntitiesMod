package com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.deeds.MurderDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.deeds.NeedFulfilledDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.events.DamageOccurrence;
import com.gm910.occentmod.util.GMFiles;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class OccurrenceType<T extends Occurrence> {

	private ResourceLocation name;
	private static final Map<ResourceLocation, OccurrenceType<?>> DEEDS = new HashMap<>();

	public static final OccurrenceType<MurderDeed> MURDER = new OccurrenceType<>(GMFiles.rl("murder"),
			(e) -> new MurderDeed());

	public static final OccurrenceType<NeedFulfilledDeed> NEED_FULFILLED = new OccurrenceType<>(
			GMFiles.rl("need_fulfilled"), (e) -> new NeedFulfilledDeed());

	public static final OccurrenceType<DamageOccurrence> DAMAGE = new OccurrenceType<>(GMFiles.rl("damage"),
			(e) -> new DamageOccurrence(e));

	public static final OccurrenceType<DamageOccurrence> ATTACK = new OccurrenceType<>(GMFiles.rl("attack"),
			(e) -> new DamageOccurrence(e));

	private Function<ServerWorld, T> supplier;

	/**
	 * 
	 * @param name
	 * @param func returns a blank citizen deed to deserialize
	 */
	public OccurrenceType(ResourceLocation name, Function<ServerWorld, T> func) {
		this.name = name;
		this.supplier = func;
		DEEDS.put(name, this);
	}

	public ResourceLocation getName() {
		return name;
	}

	public static Occurrence deserialize(ServerWorld world, Dynamic<?> dyn) {
		OccurrenceType<?> type = get(new ResourceLocation(dyn.get("rl").asString("")));
		return type.deserializeDat(world, dyn);
	}

	public T deserializeDat(ServerWorld world, Dynamic<?> dyn) {
		T deed = supplier.apply(world);
		deed.$readData(dyn);
		return deed;
	}

	public T makeBlankForDeserialization(ServerWorld world) {
		return supplier.apply(world);
	}

	public static OccurrenceType<?> get(ResourceLocation name) {
		return DEEDS.get(name);
	}

	public static Collection<OccurrenceType<?>> getTypes() {
		return DEEDS.values();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " named " + this.name;
	}

}
