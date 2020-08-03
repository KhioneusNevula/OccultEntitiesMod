package com.gm910.occentmod.entities.wizard.tasks.wizard;

import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.PanicTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

public class GeneralClearHurtTask extends Task<LivingEntity> {
	public GeneralClearHurtTask() {
		super(ImmutableMap.of());
	}

	protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
		boolean flag = PanicTask.hasBeenHurt(entityIn) || PanicTask.hostileNearby(entityIn) || func_220394_a(entityIn);
		if (!flag) {
			entityIn.getBrain().removeMemory(MemoryModuleType.HURT_BY);
			entityIn.getBrain().removeMemory(MemoryModuleType.HURT_BY_ENTITY);
			entityIn.getBrain().updateActivity(worldIn.getDayTime(), worldIn.getGameTime());
		}

	}

	private static boolean func_220394_a(LivingEntity p_220394_0_) {
		return p_220394_0_.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).filter((p_223523_1_) -> {
			return p_223523_1_.getDistanceSq(p_220394_0_) <= 36.0D;
		}).isPresent();
	}
}