package com.gm910.occentmod.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class MinecraftEventHandler {
	
	public static int ticks = 0;
	

	@SubscribeEvent
	public static void living(LivingUpdateEvent event) {
		if (!event.getEntity().world.isRemote) {
			
		}
	}
	
	
	@EventBusSubscriber(value=Dist.CLIENT)
	public static class ClientEventHandler {
		
		
		
		@SubscribeEvent
		public static void clientticking(ClientTickEvent event) {
			ticks++;
		}
		
	}
	
	@EventBusSubscriber(value=Dist.DEDICATED_SERVER)
	public static class DedicatedServerEventHandler {
		
		
		@SubscribeEvent
		public static void tickcounting(ServerTickEvent event) {
			ticks++;
		}
	}
	
	
}
