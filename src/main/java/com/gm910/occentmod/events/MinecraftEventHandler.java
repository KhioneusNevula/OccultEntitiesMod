package com.gm910.occentmod.events;

import java.util.Optional;

import com.gm910.occentmod.capabilities.speciallocs.SpecialLocationManager;
import com.gm910.occentmod.init.AIInit;
import com.google.common.base.Predicates;

import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class MinecraftEventHandler {

	public static long ticksPlayed = 0;

	@SubscribeEvent
	public static void living(LivingUpdateEvent event) {
		if (event.getEntity().world instanceof ServerWorld) {
			ServerWorld world = (ServerWorld) event.getEntity().getEntityWorld();
			PointOfInterestManager poi = world.getPointOfInterestManager();
			Optional<BlockPos> obpp = poi.findClosest((e) -> e == AIInit.VAETTR_POI.get(), Predicates.alwaysTrue(),
					event.getEntity().getPosition(), 30, PointOfInterestManager.Status.ANY);
			if (obpp.isPresent()) {
				BlockPos bpp = obpp.get();
			}
		}
	}

	@SubscribeEvent
	public static void worldtick(WorldTickEvent event) {
		if (event.world.isRemote) {
			return;
		}
		SpecialLocationManager manager = SpecialLocationManager.getForWorld((ServerWorld) event.world);
		manager.tick(event, event.world.getGameTime(), event.world.getDayTime());
	}

	@SubscribeEvent
	public static void liv(LivingUpdateEvent event) {

	}

	@SubscribeEvent
	public static void chunkload(ChunkEvent.Load event) {
		if (!(event.getWorld() instanceof ServerWorld)) {
			return;
		}
		SpecialLocationManager manager = SpecialLocationManager.getForWorld((ServerWorld) event.getWorld());
		manager.chunkload(event);
	}

	@SubscribeEvent
	public static void chunkunload(ChunkEvent.Unload event) {
		if (!(event.getChunk() instanceof Chunk)) {
			return;
		}
	}

	@SubscribeEvent
	public static void chunkdataload(ChunkDataEvent.Load event) {

	}

	@SubscribeEvent
	public static void chunkdataunload(ChunkDataEvent.Unload event) {

	}

	@EventBusSubscriber(value = Dist.CLIENT)
	public static class ClientEventHandler {

		@SubscribeEvent
		public static void clientticking(ClientTickEvent event) {
			ticksPlayed++;
		}

	}

	@EventBusSubscriber(value = Dist.DEDICATED_SERVER)
	public static class DedicatedServerEventHandler {

		@SubscribeEvent
		public static void tickcounting(ServerTickEvent event) {
			ticksPlayed++;

		}

	}

}
