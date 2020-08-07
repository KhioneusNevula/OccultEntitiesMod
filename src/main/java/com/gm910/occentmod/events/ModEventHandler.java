package com.gm910.occentmod.events;

import com.gm910.occentmod.api.networking.TaskEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ModEventHandler {

	@SubscribeEvent
	public static void onTask(TaskEvent event) {
		if (event.shouldRunByDefault()) {
			event.run();
		}
	}

}
