package com.gm910.occentmod;

import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gm910.occentmod.api.networking.messages.ModChannels;
import com.gm910.occentmod.api.networking.messages.Networking.TaskMessage;
import com.gm910.occentmod.blocks.worldcontroller.SmallerUnitTESR;
import com.gm910.occentmod.capabilities.GMCaps;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Race;
import com.gm910.occentmod.init.BiomeInit;
import com.gm910.occentmod.init.BlockInit;
import com.gm910.occentmod.init.DataInit;
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
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
		System.out.println("Setup method added to event bus");
		// Register the enqueueIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		System.out.println("Enqueue imc method added to event bus");
		// Register the processIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		System.out.println("Process imc method added to event bus");
		// Register the doClientStuff method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		System.out.println("Do client stuff method added to event bus");

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
		System.out.println("Mod added to event bus");
		ModKeys.firstinit();
		System.out.println("Mod keys first init completed");

		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		Race.registerRaceGeneTypes();

		BlockInit.BLOCKS.register(modBus);
		System.out.println("BlocksRegistered");

		ItemInit.ITEMS.register(modBus);
		System.out.println("ITems registered");

		TileInit.TILE_TYPES.register(modBus);
		System.out.println("TileTypes registered");

		EntityInit.ENTITY_TYPES.register(modBus);
		System.out.println("EntityTypes registered");

		DataInit.registerToEventBus(modBus);
		System.out.println("DataInit registered");

		BiomeInit.BIOMES.register(modBus);
		System.out.println("Biomes registered");

		DimensionInit.WORLD_MAKERS.register(modBus);
		System.out.println("World-makers registered");

	}

	@SuppressWarnings("deprecation")
	private void setup(final FMLCommonSetupEvent event) {
		System.out.println("HELLO FROM PREINIT");
		GMCaps.preInit();
		DeferredWorkQueue.runLater(StructureInit::registerStructures);

		DeferredWorkQueue.runLater(() -> EntityInit.registerSittableEntities());
	}

	private void doClientStuff(final FMLClientSetupEvent event) {

		LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
		ModKeys.clientinit();
		ModChannels.INSTANCE.registerMessage(ModChannels.id++, TaskMessage.class, TaskMessage::encode,
				TaskMessage::fromBuffer, TaskMessage::handle);

		ClientRegistry.bindTileEntityRenderer(TileInit.WORLD_CONTROLLER.get(), SmallerUnitTESR::new);
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
			System.out.println("Register dimensions event");
			DimensionData.registerInitialDimensionsStatic();
		}

	}
}
