package com.gm910.occentmod.entities.citizen.mind_and_traits.personality.hobbies;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.gm910.occentmod.entities.citizen.CitizenEntity;

import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.ResourceLocation;

public class HobbyType<T extends Task<? super CitizenEntity>> {

	private static final Map<ResourceLocation, HobbyType<?>> TYPES = new HashMap<>();

	public final ResourceLocation regName;

	public final Function<CitizenEntity, T> supplier;

	public HobbyType(ResourceLocation regName, Function<CitizenEntity, T> tasque) {
		this.regName = regName;
		this.supplier = tasque;
		TYPES.put(regName, this);
	}

	public static HobbyType<?> get(ResourceLocation rl) {
		return TYPES.get(rl);
	}

	public static Collection<HobbyType<?>> getHobbyTypes() {
		return TYPES.values();
	}

}
