package com.gm910.occentmod.entities.wizard.tasks.wizard.jobs;

import java.util.Objects;
import java.util.Optional;

import com.gm910.occentmod.entities.wizard.WizardEntity;
import com.gm910.occentmod.entities.wizard.WizardJob;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class WizardWorkTask extends Task<WizardEntity> {
	private long time;
	private final int maxDistanceFromSite;

	public WizardWorkTask(int p_i50342_2_) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.JOB_SITE,
				MemoryModuleStatus.VALUE_PRESENT));
		this.maxDistanceFromSite = p_i50342_2_;
	}

	protected boolean shouldExecute(ServerWorld worldIn, WizardEntity owner) {
		Optional<GlobalPos> optional = owner.getBrain().getMemory(MemoryModuleType.JOB_SITE);
		return optional.isPresent() && Objects.equals(worldIn.getDimension().getType(), optional.get().getDimension())
				&& optional.get().getPos().withinDistance(owner.getPositionVec(), (double) this.maxDistanceFromSite);
	}

	protected void startExecuting(ServerWorld worldIn, WizardEntity entityIn, long gameTimeIn) {
		if (gameTimeIn > this.time) {
			Optional<Vec3d> optional = Optional.ofNullable(RandomPositionGenerator.getLandPos(entityIn, 8, 6));
			entityIn.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220564_0_) -> {
				return new WalkTarget(p_220564_0_, 0.4F, 1);
			}));
			this.performWork(worldIn, entityIn, gameTimeIn);
			this.time = gameTimeIn + 180L;
		}

	}

	public void performWork(ServerWorld worldIn, WizardEntity entity, long gameTime) {
		WizardJob job = entity.getJob();
		WizardJobTypes.JOB_STYLES.getOrDefault(job, (world, en, gt) -> {
		}).accept(worldIn, entity, gameTime);
	}

}