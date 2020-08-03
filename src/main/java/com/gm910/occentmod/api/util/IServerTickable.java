package com.gm910.occentmod.api.util;

import net.minecraftforge.event.TickEvent.ServerTickEvent;

public interface IServerTickable {

	public void tick(ServerTickEvent event, long gameTime, long dayTime);
}
