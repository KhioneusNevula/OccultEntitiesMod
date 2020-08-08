package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.util.ResourceLocation;

public class PersistentTaskType<I extends CitizenTask & IPersistentTask> {

	private static final Map<ResourceLocation, PersistentTaskType<?>> TYPES = new HashMap<>();

	Function<Dynamic<?>, I> deserializer;
	ResourceLocation rl;

	public PersistentTaskType(ResourceLocation rl, Function<Dynamic<?>, I> deserializer) {
		this.rl = rl;
		this.deserializer = deserializer;
		TYPES.put(rl, this);
	}

	public ResourceLocation getResourceLocation() {
		return rl;
	}

	public I deserialize(Dynamic<?> dyn) {
		return deserializer.apply(dyn);
	}

	public static <I extends CitizenTask & IPersistentTask> PersistentTaskType<I> get(ResourceLocation rl) {
		return (PersistentTaskType<I>) TYPES.get(rl);
	}

	public static Collection<PersistentTaskType<?>> getValues() {
		return TYPES.values();
	}
}