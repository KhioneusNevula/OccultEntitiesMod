package com.gm910.occentmod.entities.wizard.tasks.wizard;

import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.server.ServerWorld;

public class CreatureWalkToPOITask extends Task<CreatureEntity> {
	private final float field_225445_a;
	private final int field_225446_b;

	public CreatureWalkToPOITask(float p_i51557_1_, int p_i51557_2_) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
		this.field_225445_a = p_i51557_1_;
		this.field_225446_b = p_i51557_2_;
	}

	protected boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner) {
		return !worldIn.isVillage(new BlockPos(owner));
	}

	protected void startExecuting(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn) {
		PointOfInterestManager pointofinterestmanager = worldIn.getPointOfInterestManager();
		int i = pointofinterestmanager.sectionsToVillage(SectionPos.from(new BlockPos(entityIn)));
		Vec3d vec3d = null;

		for (int j = 0; j < 5; ++j) {
			Vec3d vec3d1 = RandomPositionGenerator.func_221024_a(entityIn, 15, 7, (p_225444_1_) -> {
				return (double) (-worldIn.sectionsToVillage(SectionPos.from(p_225444_1_)));
			});
			if (vec3d1 != null) {
				int k = pointofinterestmanager.sectionsToVillage(SectionPos.from(new BlockPos(vec3d1)));
				if (k < i) {
					vec3d = vec3d1;
					break;
				}

				if (k == i) {
					vec3d = vec3d1;
				}
			}
		}

		if (vec3d != null) {
			entityIn.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
					new WalkTarget(vec3d, this.field_225445_a, this.field_225446_b));
		}

	}
}