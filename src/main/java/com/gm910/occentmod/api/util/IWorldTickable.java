package com.gm910.occentmod.api.util;

import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.LogicalSide;

public interface IWorldTickable {

	public void tick(WorldTickEvent event, long gameTime, long dayTime);

	/**
	 * Return null for both sides
	 * 
	 * @return
	 */
	public default LogicalSide getSide() {
		return null;
	}

	public default boolean canTickWhileUnloaded() {
		return false;
	}

	public default boolean canTick() {
		return this instanceof IWorldTickable;
	}

}
