package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.Map;

import com.gm910.occentmod.entities.citizen.CitizenEntity;

import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public abstract class ImmediateTask extends CitizenTask {

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
	public boolean shouldExecute(ServerWorld worldIn, CitizenEntity owner) {
		return true;
	}

}
