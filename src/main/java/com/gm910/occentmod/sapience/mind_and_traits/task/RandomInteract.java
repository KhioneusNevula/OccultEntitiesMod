package com.gm910.occentmod.sapience.mind_and_traits.task;

import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds.SapientDeed;
import com.gm910.occentmod.sapience.mind_and_traits.personality.PersonalityTrait;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class RandomInteract extends SapientTask<LivingEntity> {

	public RandomInteract() {
		super(LivingEntity.class, ImmutableMap.of());
	}

	@Override
	public boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
		return SapientInfo.get(owner).getPersonality().generateTraitReactionMap().get(PersonalityTrait.INQUISITIVITY)
				.isTernaryHigh();
	}

	@Override
	protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
		for (int x = -10; x <= 10; x++) {
			for (int y = -10; y <= 10; y++) {
				for (int z = -10; z <= 10; z++) {
					Vec3d vpos = entityIn.getPositionVector().add(x, y, z);
					BlockPos pos = new BlockPos(vpos);
					if (!Occurrence.canBeSeenBy(vpos, entityIn)) {
						continue;
					}
					SapientInfo<?> info = SapientInfo.get(entityIn);
					
					info.getKnowledge().setMemoryModule)
				}
			}
		}
	}

	@Override
	public SapientDeed getDeed(SapientIdentity doer) {

		return null;
	}

	@Override
	public TaskType<LivingEntity, ? extends SapientTask<LivingEntity>> getType() {

		return null;
	}

}
