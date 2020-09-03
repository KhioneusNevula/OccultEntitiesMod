package com.gm910.occentmod.sapience.mind_and_traits.task.motion;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.init.GMDeserialize;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds.SapientDeed;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.gm910.occentmod.sapience.mind_and_traits.task.ImmediateTask;
import com.gm910.occentmod.sapience.mind_and_traits.task.Necessity;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientTask;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientWalkTarget;
import com.gm910.occentmod.sapience.mind_and_traits.task.TaskType;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class ExploreAndObserveTask extends ImmediateTask<MobEntity> {

	private final float speed;

	public ExploreAndObserveTask(float speed) {
		super(MobEntity.class, ImmutableMap.of());
		this.speed = speed;
		this.addContext(Context.MOVE);
	}

	@Override
	public boolean shouldExecute(ServerWorld worldIn, MobEntity owner) {
		return !SapientInfo.get(owner).getKnowledge().getValueModule(GMDeserialize.SAPIENT_WALK_TARGET).isPresent();
	}

	protected void startExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn) {
		BlockPos blockpos = new BlockPos(entityIn);
		List<BlockPos> list = BlockPos.getAllInBox(blockpos.add(-1, -1, -1), blockpos.add(1, 1, 1))
				.map(BlockPos::toImmutable).collect(Collectors.toList());
		Collections.shuffle(list);
		Optional<BlockPos> optional = list.stream().filter((pose) -> {
			return !worldIn.canSeeSky(pose);
		}).filter((pos) -> {
			return worldIn.isTopSolid(pos, entityIn);
		}).filter((en) -> {
			return worldIn.hasNoCollisions(entityIn);
		}).findFirst();
		optional.ifPresent((pose) -> {
			SapientInfo.get(entityIn).getKnowledge().setValueModule(GMDeserialize.SAPIENT_WALK_TARGET,
					new SapientWalkTarget(pose, this.speed, 0, Necessity.UNNECESSARY));
		});
	}

	@Override
	public SapientDeed getDeed(SapientIdentity doer) {

		return null;
	}

	@Override
	public TaskType<MobEntity, ? extends SapientTask<MobEntity>> getType() {
		return TaskType.EXPLORE_AND_OBSERVE;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return ops.createFloat(this.speed);
	}

}
