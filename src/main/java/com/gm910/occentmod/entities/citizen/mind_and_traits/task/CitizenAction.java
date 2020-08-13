package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.Set;

import com.gm910.occentmod.entities.citizen.CitizenEntity;

import net.minecraft.world.server.ServerWorld;

public interface CitizenAction {

	public default void startExecuting(ServerWorld worldIn, CitizenEntity entityIn, long gameTimeIn) {
		Set<CitizenEntity> en = startActionOn(worldIn, entityIn, gameTimeIn);
		for (CitizenEntity e : en) {
			e.react(this);
		}
	}

	public default void updateTask(ServerWorld worldIn, CitizenEntity owner, long gameTime) {
		Set<CitizenEntity> en = updateActionOn(worldIn, owner, gameTime);
		for (CitizenEntity e : en) {
			e.react(this);
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
	public Set<CitizenEntity> startActionOn(ServerWorld world, CitizenEntity entity, long gameTime);

	/**
	 * Return an empty set if no one is affected
	 * 
	 * @param world
	 * @param entity
	 * @param gameTime
	 * @return
	 */
	public Set<CitizenEntity> updateActionOn(ServerWorld world, CitizenEntity entity, long gameTime);

	/**
	 * Return empty if the given citizen entity would not react
	 * 
	 * @return
	 */
	public Set<ImmediateTask> getPotentialReactions();

}
