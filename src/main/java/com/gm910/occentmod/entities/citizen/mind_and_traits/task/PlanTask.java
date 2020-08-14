package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.ResourceLocation;

/**
 * A set of linked tasks intrinsically linked to each other's completion
 * 
 * @author borah
 *
 */
public class PlanTask extends CitizenTask implements IPersistentTask {

	private List<? extends CitizenTask> tasks = new ArrayList<>();

	public PlanTask() {
		super(ImmutableMap.of());
	}

	public PlanTask(Dynamic<?> dyn) {
		super(ImmutableMap.of());

	}

	public static List<? extends CitizenTask> getTasks(Dynamic<?> dyn) {
		return dyn.asList((d) -> PersistentTaskType.get(new ResourceLocation(d.get("rl").asString(""))).deserialize(d));
	}

	@Override
	public PersistentTaskType<?> getType() {
		return PersistentTaskType.PLAN;
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {

		return null;
	}

	public static enum PlanType {
		SUBSEQUENT, SIMULTANEOUS
	}
}
