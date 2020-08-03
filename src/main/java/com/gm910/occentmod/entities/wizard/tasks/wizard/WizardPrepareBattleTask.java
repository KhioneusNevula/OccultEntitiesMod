package com.gm910.occentmod.entities.wizard.tasks.wizard;

import com.gm910.occentmod.entities.wizard.WizardActivities;
import com.gm910.occentmod.entities.wizard.WizardEntity;
import com.gm910.occentmod.entities.wizard.tasks.seidhvaettr.SeidhAttackTask;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

public class WizardPrepareBattleTask extends Task<WizardEntity> {
	public WizardPrepareBattleTask() {
		super(ImmutableMap.of());
	}

	protected boolean shouldContinueExecuting(ServerWorld worldIn, WizardEntity entityIn, long gameTimeIn) {
		return hasBeenHurt(entityIn) || hostileNearby(entityIn);
	}

	protected void startExecuting(ServerWorld worldIn, WizardEntity entityIn, long gameTimeIn) {
		if (hasBeenHurt(entityIn) || hostileNearby(entityIn)) {
			Brain<?> brain = entityIn.getBrain();
			if (!brain.hasActivity(WizardActivities.BATTLE.get())) {
				brain.removeMemory(MemoryModuleType.PATH);
				brain.removeMemory(MemoryModuleType.WALK_TARGET);
				brain.removeMemory(MemoryModuleType.LOOK_TARGET);
				brain.removeMemory(MemoryModuleType.BREED_TARGET);
				brain.removeMemory(MemoryModuleType.INTERACTION_TARGET);
			}

			brain.switchTo(WizardActivities.BATTLE.get());
		}

	}

	protected void updateTask(ServerWorld worldIn, WizardEntity owner, long gameTime) {
		if (gameTime % 100L == 0L && owner.getJob().canBattle) {
			owner.notifySeidhvaettir(new SeidhAttackTask(owner.getJob()));
		}

	}

	public static boolean hostileNearby(LivingEntity p_220513_0_) {
		return p_220513_0_.getBrain().hasMemory(MemoryModuleType.NEAREST_HOSTILE);
	}

	public static boolean hasBeenHurt(LivingEntity p_220512_0_) {
		return p_220512_0_.getBrain().hasMemory(MemoryModuleType.HURT_BY);
	}
}