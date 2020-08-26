package com.gm910.occentmod.entities.citizen.mind_and_traits.task.needs;

import com.gm910.occentmod.capabilities.citizeninfo.CitizenInfo;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedType;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public interface INeedsTask<R extends LivingEntity, T extends NeedType<?, ?>> {

	public T getNeedType();

	public default boolean shouldContinueExecuting(ServerWorld worldIn, R entityIn, long gameTimeIn) {
		return !CitizenInfo.get(entityIn).orElse(null).getNeeds().getChecker((NeedType<R, T>) getNeedType())
				.areNeedsFulfilled();
	}

}
