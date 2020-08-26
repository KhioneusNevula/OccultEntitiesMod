package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.deeds.CitizenDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

/**
 * A set of linked tasks intrinsically linked to each other's completion
 * 
 * @author borah
 *
 */
public class PlanTask extends CitizenTask<LivingEntity> {

	private List<? extends CitizenTask<? extends LivingEntity>> tasks = new ArrayList<>();

	private Predicate<? super CitizenEntity> shouldStart;

	private Predicate<? super CitizenEntity> shouldPause;

	private boolean isActive;

	public PlanTask(Predicate<? super LivingEntity> shouldStart, Predicate<? super LivingEntity> shouldPause,
			List<? extends CitizenTask<? extends LivingEntity>> tasks) {
		super(ImmutableMap.of());
		this.shouldStart = shouldStart;
		this.shouldPause = shouldPause;
		this.isActive = false;
	}

	public PlanTask(Dynamic<?> dyn) {
		super(memoryStates(getTasks(dyn.get("tasks").get().get())));
		this.tasks = dyn.get("tasks").asList((e) -> TaskType.deserialize(e));
		this.isActive = dyn.get("active").asBoolean(false);
	}

	public boolean shouldStart(CitizenEntity en) {
		return shouldStart.test(en);
	}

	@Override
	public boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
		return super.shouldExecute(worldIn, owner);
	}

	public boolean shouldPause(CitizenEntity en) {
		return shouldPause.test(en);
	}

	public boolean isActive() {
		return isActive;
	}

	@Override
	public boolean isVisible(LivingEntity en, LivingEntity seer) {
		return false;
	}

	@Override
	public Set<Context> getContexts() {
		return Sets.newHashSet(Context.CORE);
	}

	@Override
	public boolean isIndefinite() {
		return true;
	}

	public static <E extends LivingEntity> List<? extends CitizenTask<? super E>> getTasks(Dynamic<?> dyn) {
		return dyn.asList((d) -> TaskType.deserialize(d));
	}

	public static <E extends LivingEntity> Map<MemoryModuleType<?>, MemoryModuleStatus> memoryStates(
			List<? extends CitizenTask<? super E>> tasques) {
		Map<MemoryModuleType<?>, MemoryModuleStatus> stats = new HashMap<>();
		for (CitizenTask<? super E> tasque : tasques) {
			stats.putAll(tasque.getDelegateMemoryMap());
		}
		return stats;
	}

	@Override
	public TaskType<LivingEntity, PlanTask> getType() {
		return TaskType.PLAN;
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {
		T da = ops.createList(this.tasks.stream().map((e) -> e.serialize(ops)));

		T a = ops.createBoolean(this.isActive);

		return ops.createMap(ImmutableMap.of(ops.createString("tasks"), da, ops.createString("active"), a));
	}

	@Override
	public CitizenDeed getDeed(CitizenIdentity doer) {
		return null;
	}

}
