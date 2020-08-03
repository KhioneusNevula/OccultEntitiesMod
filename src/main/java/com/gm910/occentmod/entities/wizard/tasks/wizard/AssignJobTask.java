package com.gm910.occentmod.entities.wizard.tasks.wizard;

import com.gm910.occentmod.entities.wizard.WizardEntity;
import com.gm910.occentmod.entities.wizard.WizardJob;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class AssignJobTask extends Task<WizardEntity> {
	public AssignJobTask() {
		super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_PRESENT));
	}

	protected boolean shouldExecute(ServerWorld worldIn, WizardEntity owner) {
		return owner.getJob() == WizardJob.JOBLESS;
	}

	protected void startExecuting(ServerWorld worldIn, WizardEntity entityIn, long gameTimeIn) {
		// System.out.println("WIZARD REASSIGNING JOB");
		GlobalPos globalpos = entityIn.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
		MinecraftServer minecraftserver = worldIn.getServer();
		minecraftserver.getWorld(globalpos.getDimension()).getPointOfInterestManager().getType(globalpos.getPos())
				.ifPresent((poi) -> {
					(Lists.newArrayList(WizardJob.values())).stream().filter((specific) -> {
						return specific.poi == poi;
					}).findFirst().ifPresent((job) -> {
						entityIn.setJob(job);
						entityIn.resetBrain(worldIn);
					});
				});
	}
}