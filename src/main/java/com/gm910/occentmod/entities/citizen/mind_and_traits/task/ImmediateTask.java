package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.Map;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public abstract class ImmediateTask<E extends LivingEntity> extends SapientTask<E> {

	public ImmediateTask(Class<E> doerclazz, Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn,
			int durationMinIn, int durationMaxIn) {
		super(doerclazz, requiredMemoryStateIn, durationMinIn, durationMaxIn);
	}

	public ImmediateTask(Class<E> doerClazz, Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn,
			int duration) {
		super(doerClazz, requiredMemoryStateIn, duration);
	}

	public ImmediateTask(Class<E> doerClazz, Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn) {
		super(doerClazz, requiredMemoryStateIn);
	}

	@Override
	public boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
		return true;
	}

}
