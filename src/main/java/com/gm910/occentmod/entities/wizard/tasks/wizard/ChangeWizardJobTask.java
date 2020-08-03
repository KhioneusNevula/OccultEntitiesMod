package com.gm910.occentmod.entities.wizard.tasks.wizard;

import com.gm910.occentmod.entities.wizard.WizardEntity;
import com.gm910.occentmod.entities.wizard.WizardJob;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

public class ChangeWizardJobTask extends Task<WizardEntity> {
	public ChangeWizardJobTask() {
		super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_ABSENT));
	}

	protected boolean shouldExecute(ServerWorld worldIn, WizardEntity owner) {
		return owner.getJob() != WizardJob.JOBLESS;
	}

	protected void startExecuting(ServerWorld worldIn, WizardEntity entityIn, long gameTimeIn) {
		entityIn.setJob(WizardJob.JOBLESS);
		entityIn.resetBrain(worldIn);
	}
}