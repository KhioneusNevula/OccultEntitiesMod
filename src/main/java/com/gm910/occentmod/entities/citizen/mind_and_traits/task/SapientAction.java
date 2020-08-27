package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.Set;

import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public interface SapientAction<Doer extends LivingEntity> {

	public static <Doer extends LivingEntity, T extends SapientTask<?> & SapientAction<Doer>> void startExecuting(
			T this_, ServerWorld worldIn, Doer entityIn, long gameTimeIn) {
		Set<? extends LivingEntity> en = this_.startActionOn(worldIn, entityIn, gameTimeIn);
		for (LivingEntity e : en) {
			if (!SapientInfo.getLazy(e).isPresent())
				continue;
			SapientInfo.getLazy(e).orElse(null).getAutonomy().react(this_, entityIn);
		}
	}

	public static <Doer extends LivingEntity, T extends SapientTask<?> & SapientAction<Doer>> void updateTask(T this_,
			ServerWorld worldIn, Doer owner, long gameTime) {
		Set<? extends LivingEntity> en = this_.updateActionOn(worldIn, owner, gameTime);
		for (LivingEntity e : en) {
			if (!SapientInfo.getLazy(e).isPresent())
				continue;
			SapientInfo.getLazy(e).orElse(null).getAutonomy().react(this_, owner);
		}
	}

	/**
	 * Return an empty set if no one is affected
	 * 
	 * @param world
	 * @param entity
	 * @param gameTime
	 * @return
	 */
	public Set<? extends LivingEntity> startActionOn(ServerWorld world, Doer entity, long gameTime);

	/**
	 * Return an empty set if no one is affected
	 * 
	 * @param world
	 * @param entity
	 * @param gameTime
	 * @return
	 */
	public Set<? extends LivingEntity> updateActionOn(ServerWorld world, Doer entity, long gameTime);

	/**
	 * Return empty if the given citizen entity would not react
	 * 
	 * @return
	 */
	public Set<SapientTask<? extends LivingEntity>> getPotentialReactions();

}
