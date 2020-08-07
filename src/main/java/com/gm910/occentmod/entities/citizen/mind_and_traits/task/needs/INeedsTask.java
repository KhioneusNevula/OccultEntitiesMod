package com.gm910.occentmod.entities.citizen.mind_and_traits.task.needs;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedType;

import net.minecraft.world.server.ServerWorld;

public interface INeedsTask<T extends NeedType<?>> {

	public T getNeedType();

	public default boolean shouldContinueExecuting(ServerWorld worldIn, CitizenEntity entityIn, long gameTimeIn) {
		return !entityIn.getNeeds().getChecker(getNeedType()).areNeedsFulfilled();
	}
}
