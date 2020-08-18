package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;

/**
 * A set of linked tasks intrinsically linked to each other's completion
 * 
 * @author borah
 *
 */
public class PlanTask extends CitizenTask {

	private List<? extends CitizenTask> tasks = new ArrayList<>();

	private Predicate<? super CitizenEntity> shouldStart;

	private Predicate<? super CitizenEntity> shouldPause;

	public PlanTask(Predicate<? super CitizenEntity> shouldStart, Predicate<? super CitizenEntity> shouldPause,
			List<? extends CitizenTask> tasks) {
		super(ImmutableMap.of());
		this.shouldStart = shouldStart;
		this.shouldPause = shouldPause;
	}

	public PlanTask(Dynamic<?> dyn) {
		super(memoryStates(getTasks(dyn.get("tasks").get().get())));
		this.tasks = dyn.get("tasks").asList((e) -> TaskType.deserialize(e));
	}

	public boolean shouldStart(CitizenEntity en) {
		return shouldStart.test(en);
	}

	public boolean shouldPause(CitizenEntity en) {
		return shouldPause.test(en);
	}

	public static List<? extends CitizenTask> getTasks(Dynamic<?> dyn) {
		return dyn.asList((d) -> TaskType.deserialize(d));
	}

	public static Map<MemoryModuleType<?>, MemoryModuleStatus> memoryStates(List<? extends CitizenTask> tasques) {
		Map<MemoryModuleType<?>, MemoryModuleStatus> stats = new HashMap<>();
		for (CitizenTask tasque : tasques) {
			stats.putAll(tasque.getDelegateMemoryMap());
		}
		return stats;
	}

	@Override
	public TaskType<?> getType() {
		return TaskType.PLAN;
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {
		T da = ops.createList(this.tasks.stream().map((e) -> e.serialize(ops)));

		return ops.createMap(ImmutableMap.of(ops.createString("tasks"), da));
	}

}
