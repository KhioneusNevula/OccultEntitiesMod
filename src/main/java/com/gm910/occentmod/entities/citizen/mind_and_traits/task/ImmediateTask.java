package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.Map;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public abstract class ImmediateTask<E extends LivingEntity> extends CitizenTask<E> {

	public ImmediateTask(Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn, int durationMinIn,
			int durationMaxIn) {
		super(requiredMemoryStateIn, durationMinIn, durationMaxIn);
	}

	public ImmediateTask(Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn, int duration) {
		super(requiredMemoryStateIn, duration);
	}

	public ImmediateTask(Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn) {
		super(requiredMemoryStateIn);
	}

	@Override
	public boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
		return true;
	}

}
