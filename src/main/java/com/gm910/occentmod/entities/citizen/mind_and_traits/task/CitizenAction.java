package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.Set;

import com.gm910.occentmod.capabilities.citizeninfo.CitizenInfo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public interface CitizenAction<Doer extends LivingEntity> {

	public static <Doer extends LivingEntity, T extends CitizenTask<?> & CitizenAction<Doer>> void startExecuting(
			T this_, ServerWorld worldIn, Doer entityIn, long gameTimeIn) {
		Set<? extends LivingEntity> en = this_.startActionOn(worldIn, entityIn, gameTimeIn);
		for (LivingEntity e : en) {
			if (!CitizenInfo.get(e).isPresent())
				continue;
			CitizenInfo.get(e).orElse(null).getAutonomy().react(this_, entityIn);
		}
	}

	public static <Doer extends LivingEntity, T extends CitizenTask<?> & CitizenAction<Doer>> void updateTask(T this_,
			ServerWorld worldIn, Doer owner, long gameTime) {
		Set<? extends LivingEntity> en = this_.updateActionOn(worldIn, owner, gameTime);
		for (LivingEntity e : en) {
			if (!CitizenInfo.get(e).isPresent())
				continue;
			CitizenInfo.get(e).orElse(null).getAutonomy().react(this_, owner);
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
	public Set<CitizenTask<? extends LivingEntity>> getPotentialReactions();

}
