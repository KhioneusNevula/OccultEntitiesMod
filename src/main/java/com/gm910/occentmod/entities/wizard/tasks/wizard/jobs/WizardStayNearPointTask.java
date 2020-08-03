package com.gm910.occentmod.entities.wizard.tasks.wizard.jobs;

import java.util.Optional;

import com.gm910.occentmod.entities.wizard.WizardEntity;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class WizardStayNearPointTask extends Task<WizardEntity> {
	private final MemoryModuleType<GlobalPos> point;
	private final float float1;
	private final int int1;
	private final int int2;
	private final int int3;

	public WizardStayNearPointTask(MemoryModuleType<GlobalPos> point, float p_i51501_2_, int p_i51501_3_,
			int p_i51501_4_, int p_i51501_5_) {
		super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleStatus.REGISTERED,
				MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, point,
				MemoryModuleStatus.VALUE_PRESENT));
		this.point = point;
		this.float1 = p_i51501_2_;
		this.int1 = p_i51501_3_;
		this.int2 = p_i51501_4_;
		this.int3 = p_i51501_5_;
	}

	private void releasePOIS(WizardEntity p_225457_1_, long gametime) {
		Brain<?> brain = p_225457_1_.getBrain();
		p_225457_1_.releasePOIS(this.point);
		brain.removeMemory(this.point);
		brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, gametime);
	}

	protected void startExecuting(ServerWorld worldIn, WizardEntity entityIn, long gameTimeIn) {
		Brain<?> brain = entityIn.getBrain();
		brain.getMemory(this.point).ifPresent((gpos) -> {
			if (this.checkTimeSinceReachingWalkTarget(worldIn, entityIn)) {
				this.releasePOIS(entityIn, gameTimeIn);
			} else if (this.desiredPointTooFar(worldIn, entityIn, gpos)) {
				Vec3d vec3d = null;
				int i = 0;

				for (int j = 1000; i < 1000 && (vec3d == null || this.desiredPointTooFar(worldIn, entityIn,
						GlobalPos.of(entityIn.dimension, new BlockPos(vec3d)))); ++i) {
					vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(entityIn, 15, 7,
							new Vec3d(gpos.getPos()));
				}

				if (i == 1000) {
					this.releasePOIS(entityIn, gameTimeIn);
					return;
				}

				brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.float1, this.int1));
			} else if (!this.desiredPointWithinRange(worldIn, entityIn, gpos)) {
				brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(gpos.getPos(), this.float1, this.int1));
			}

		});
	}

	private boolean checkTimeSinceReachingWalkTarget(ServerWorld p_223017_1_, WizardEntity p_223017_2_) {
		Optional<Long> optional = p_223017_2_.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
		if (optional.isPresent()) {
			return p_223017_1_.getGameTime() - optional.get() > (long) this.int3;
		} else {
			return false;
		}
	}

	private boolean desiredPointTooFar(ServerWorld p_220546_1_, WizardEntity p_220546_2_, GlobalPos p_220546_3_) {
		return p_220546_3_.getDimension() != p_220546_1_.getDimension().getType()
				|| p_220546_3_.getPos().manhattanDistance(new BlockPos(p_220546_2_)) > this.int2;
	}

	private boolean desiredPointWithinRange(ServerWorld p_220547_1_, WizardEntity p_220547_2_, GlobalPos p_220547_3_) {
		return p_220547_3_.getDimension() == p_220547_1_.getDimension().getType()
				&& p_220547_3_.getPos().manhattanDistance(new BlockPos(p_220547_2_)) <= this.int1;
	}
}