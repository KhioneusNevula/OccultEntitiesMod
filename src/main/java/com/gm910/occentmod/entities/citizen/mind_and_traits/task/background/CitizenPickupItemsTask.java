package com.gm910.occentmod.entities.citizen.mind_and_traits.task.background;

import java.util.List;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class CitizenPickupItemsTask extends Task<CitizenEntity> {
	private List<ItemEntity> field_225452_a = Lists.newArrayList();

	public CitizenPickupItemsTask() {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
	}

	protected boolean shouldExecute(ServerWorld worldIn, CitizenEntity owner) {
		this.field_225452_a = worldIn.getEntitiesWithinAABB(ItemEntity.class,
				owner.getBoundingBox().grow(4.0D, 2.0D, 4.0D));
		return !this.field_225452_a.isEmpty();
	}

	protected void startExecuting(ServerWorld worldIn, CitizenEntity entityIn, long gameTimeIn) {
		ItemEntity itementity = this.field_225452_a.get(worldIn.rand.nextInt(this.field_225452_a.size()));
		Vec3d vec3d = itementity.getPositionVec();
		entityIn.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(new BlockPos(vec3d)));
		entityIn.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, 0.5F, 0));

	}

}