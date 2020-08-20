package com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.deeds.MurderDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.needs.NeedFulfilledDeed;
import com.gm910.occentmod.util.GMFiles;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.ResourceLocation;

public class OccurrenceType<T extends Occurrence> {

	private ResourceLocation name;
	private static final Map<ResourceLocation, OccurrenceType<?>> DEEDS = new HashMap<>();

	public static final OccurrenceType<MurderDeed> MURDER = new OccurrenceType<>(GMFiles.rl("murder"),
			() -> new MurderDeed());

	public static final OccurrenceType<NeedFulfilledDeed> NEED_FULFILLED = new OccurrenceType<>(
			GMFiles.rl("need_fulfilled"), () -> new NeedFulfilledDeed());

	private Supplier<T> supplier;

	/**
	 * 
	 * @param name
	 * @param func returns a blank citizen deed to deserialize
	 */
	public OccurrenceType(ResourceLocation name, Supplier<T> func) {
		this.name = name;
		this.supplier = func;
		DEEDS.put(name, this);
	}

	public ResourceLocation getName() {
		return name;
	}

	public static Occurrence deserialize(Dynamic<?> dyn) {
		OccurrenceType<?> type = get(new ResourceLocation(dyn.get("rl").asString("")));
		return type.deserializeDat(dyn);
	}

	public T deserializeDat(Dynamic<?> dyn) {
		T deed = supplier.get();
		deed.$readData(dyn);
		return deed;
	}

	public T makeNew() {
		return supplier.get();
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
