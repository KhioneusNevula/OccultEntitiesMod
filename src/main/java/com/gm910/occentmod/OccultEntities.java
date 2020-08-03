package com.gm910.occentmod;

import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gm910.occentmod.api.networking.messages.ModChannels;
import com.gm910.occentmod.api.networking.messages.Networking.TaskMessage;
import com.gm910.occentmod.capabilities.CapabilityProvider;
import com.gm910.occentmod.init.AIInit;
import com.gm910.occentmod.init.BiomeInit;
import com.gm910.occentmod.init.BlockInit;
import com.gm910.occentmod.init.DimensionInit;
import com.gm910.occentmod.init.EntityInit;
import com.gm910.occentmod.init.ItemInit;
import com.gm910.occentmod.init.StructureInit;
import com.gm910.occentmod.init.TileInit;
import com.gm910.occentmod.keys.ModKeys;
import com.gm910.occentmod.world.DimensionData;
import com.gm910.occentmod.world.VaettrData;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(OccultEntities.MODID)
public class OccultEntities {

	public static final String MODID = "occentmod";

	public static final String NAME = "Occult Entities Mod";
	public static final String VERSION = "1.0";

	private static final Logger LOGGER = LogManager.getLogger();

	public static OccultEntities instance;

	public OccultEntities() {

		instance = this;

		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		// Register the enqueueIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		// Register the processIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		// Register the doClientStuff method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
		ModKeys.firstinit();

		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		BlockInit.BLOCKS.register(modBus);

		ItemInit.ITEMS.register(modBus);

		TileInit.TILE_TYPES.register(modBus);

		AIInit.registerToEventBus(modBus);

		EntityInit.ENTITY_TYPES.register(modBus);

		BiomeInit.BIOMES.register(modBus);

		DimensionInit.WORLD_MAKERS.register(modBus);

	}

	@SuppressWarnings("deprecation")
	private void setup(final FMLCommonSetupEvent event) {
		LOGGER.info("HELLO FROM PREINIT");
		CapabilityProvider.preInit();
		DeferredWorkQueue.runLater(StructureInit::registerStructures);

		DeferredWorkQueue.runLater(() -> EntityInit.registerSittableEntities());
	}

	private void doClientStuff(final FMLClientSetupEvent event) {

		LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
		ModKeys.clientinit();
		ModChannels.INSTANCE.registerMessage(ModChannels.id++, TaskMessage.class, TaskMessage::encode,
				TaskMessage::fromBuffer, TaskMessage::handle);
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		// some example code to dispatch IMC to another mod
		InterModComms.sendTo("occentmod", "helloworld", () -> {
			LOGGER.info("Hello world from the MDK");
			return "Hello world";
		});
	}

	private void processIMC(final InterModProcessEvent event) {
		// some example code to receive and process InterModComms from other mods
		LOGGER.info("Got IMC {}",
				event.getIMCStream().map(m -> m.getMessageSupplier().get()).collect(Collectors.toList()));
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {

	}

	@SubscribeEvent
	public void onServerStarted(FMLServerStartedEvent event) {

		DimensionData dat = DimensionData.get(event.getServer());
		dat.storeInitialDimensions();
		dat.registerStoredDimensions();
		VaettrData.get(event.getServer());
	}

	@SubscribeEvent
	public void onServerStopping(FMLServerStoppingEvent event) {

		DimensionData dat = DimensionData.get(event.getServer());
		dat.unregisterStoredDimensions();
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		/*
				@SubscribeEvent
				public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
					// register a new block here
				}
		*/
	}

	@EventBusSubscriber(bus = EventBusSubscriber.Bus.FORGE)
	public static class ForgeEvents {

		@SubscribeEvent
		public static void onDimensionRegister(RegisterDimensionsEvent event) {
			DimensionData.registerInitialDimensionsStatic();
		}

	}
}
