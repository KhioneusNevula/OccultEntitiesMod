package com.gm910.occentmod.entities.citizen.mind_and_traits.task.needs;

import com.gm910.occentmod.capabilities.citizeninfo.CitizenInfo;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedType;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public interface INeedsTask<T extends NeedType<?>> {

	public T getNeedType();

	public default boolean shouldContinueExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
		return !CitizenInfo.get(entityIn).orElse(null).getNeeds().getChecker(getNeedType()).areNeedsFulfilled();
	}

}
