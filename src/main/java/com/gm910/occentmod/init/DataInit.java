package com.gm910.occentmod.init;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.blocks.VaettrTileEntity;
import com.gm910.occentmod.entities.LivingBlockEntity;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.CitizenMemoryAndSensors;
import com.gm910.occentmod.sapience.mind_and_traits.task.CitizenSchedule;
import com.gm910.occentmod.sapience.mind_and_traits.work.CitizenPOIS;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.schedule.ScheduleBuilder;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.Item;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class DataInit {
	private DataInit() {
	}

	public static final DeferredRegister<PointOfInterestType> POIS = new DeferredRegister<>(ForgeRegistries.POI_TYPES,
			OccultEntities.MODID);
	public static final DeferredRegister<VillagerProfession> PROFESSIONS = new DeferredRegister<>(
			ForgeRegistries.PROFESSIONS, OccultEntities.MODID);
	public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULES = new DeferredRegister<>(
			ForgeRegistries.MEMORY_MODULE_TYPES, OccultEntities.MODID);
	public static final DeferredRegister<Activity> ACTIVITIES = new DeferredRegister<>(ForgeRegistries.ACTIVITIES,
			OccultEntities.MODID);
	public static final DeferredRegister<SensorType<?>> SENSORS = new DeferredRegister<>(ForgeRegistries.SENSOR_TYPES,
			OccultEntities.MODID);
	public static final DeferredRegister<Schedule> SCHEDULES = new DeferredRegister<>(ForgeRegistries.SCHEDULES,
			OccultEntities.MODID);
	public static final DeferredRegister<DataSerializerEntry> SERIALIZERS = new DeferredRegister<>(
			ForgeRegistries.DATA_SERIALIZERS, OccultEntities.MODID);

	// POI's
	public static final RegistryObject<PointOfInterestType> VAETTR_POI = registerPOIFromBlocks("vaettr",
			() -> ImmutableSet.copyOf(VaettrTileEntity.getVaettrBlocks()), 10, 2);

	public static final RegistryObject<PointOfInterestType> THRONE_POI = registerPOIFromBlocks("throne",
			() -> ImmutableSet.of(BlockInit.THRONE.get()), 1, 0);

	// Villager Professions
	public static final RegistryObject<VillagerProfession> WORSHIPER_PROF = registerProfession("worshiper",
			() -> VAETTR_POI.get(), () -> ImmutableSet.of(), () -> ImmutableSet.of(Blocks.FIRE),
			() -> SoundEvents.ENTITY_VILLAGER_WORK_CLERIC);

	public static final RegistryObject<DataSerializerEntry> CITIZEN_GENETICS_SERIALIZER = registerSerializer("genetics",
			() -> {
				return new GeneticsDataSerializer<CitizenEntity>();
			});

	public static final RegistryObject<DataSerializerEntry> SAPIENT_IDENTITY_SERIALIZER = registerSerializer(
			"dynamic_sapient_identity", () -> {
				return new SapientIdentitySerializer();
			});

	public static RegistryObject<VillagerProfession> registerProfession(String name, Supplier<PointOfInterestType> poi,
			@Nullable Supplier<SoundEvent> sound) {
		return registerProfession(name, () -> poi.get(), () -> ImmutableSet.of(), () -> ImmutableSet.of(),
				() -> sound.get());
	}

	public static RegistryObject<DataSerializerEntry> registerSerializer(String name,
			Supplier<IDataSerializer<?>> sup) {
		return SERIALIZERS.register(name, () -> new DataSerializerEntry(sup.get()));
	}

	public static RegistryObject<VillagerProfession> registerProfession(String name, Supplier<PointOfInterestType> poi,
			Supplier<ImmutableSet<Item>> items, Supplier<ImmutableSet<Block>> blocks,
			@Nullable Supplier<SoundEvent> soundEvent) {
		return PROFESSIONS.register(name,
				() -> ModReflect.construct(VillagerProfession.class,
						new Class[] { String.class, PointOfInterestType.class, ImmutableSet.class, ImmutableSet.class,
								SoundEvent.class },
						new Object[] { name, poi.get(), items.get(), blocks.get(), soundEvent.get() }));
	}

	public static RegistryObject<PointOfInterestType> registerPOIFromStates(String name,
			Supplier<Set<BlockState>> allStates, int maxUsers, int minStopDistance) {
		return POIS.register(name, () -> {
			PointOfInterestType poit = ModReflect.construct(PointOfInterestType.class,
					new Class[] { String.class, Set.class, int.class, int.class }, new Object[] { name,
							(Set<BlockState>) ImmutableSet.copyOf(allStates.get()), maxUsers, minStopDistance });
			if (poit == null)
				throw new RuntimeException("Unable to construct point of interest");
			return ModReflect.run(PointOfInterestType.class, PointOfInterestType.class, "registerBlockStates",
					"func_221052_a", null, poit);
		});
	}

	public static RegistryObject<PointOfInterestType> registerPOIFromBlocks(String name, Supplier<Set<Block>> allBlocks,
			int maxUsers, int minStopDistance) {

		return registerPOIFromStates(name, () -> {
			List<BlockState> states = new ArrayList<>();
			allBlocks.get().forEach((bl) -> {
				states.addAll(ImmutableSet.copyOf(bl.getStateContainer().getValidStates()));
			});
			return (Set<BlockState>) ImmutableSet.copyOf(states);
		}, maxUsers, minStopDistance);
	}

	public static RegistryObject<PointOfInterestType> registerPOIFromBlocks(String name, Supplier<Set<Block>> allBlocks,
			int maxUsers, Predicate<PointOfInterestType> pred, int minStopDistance) {

		return registerPOIFromPredicateAndStates(name, () -> {
			List<BlockState> states = new ArrayList<>();
			allBlocks.get().forEach((bl) -> {
				states.addAll(ImmutableSet.copyOf(bl.getStateContainer().getValidStates()));
			});
			return (Set<BlockState>) ImmutableSet.copyOf(states);
		}, pred, maxUsers, minStopDistance);
	}

	public static RegistryObject<PointOfInterestType> registerPOIFromPredicateAndStates(String name,
			Supplier<Set<BlockState>> allStates, Predicate<PointOfInterestType> pred, int maxUsers,
			int minStopDistance) {
		return POIS.register(name, () -> {
			PointOfInterestType poit = ModReflect.construct(PointOfInterestType.class,
					new Class[] { String.class, Set.class, int.class, Predicate.class, int.class }, new Object[] { name,
							(Set<BlockState>) ImmutableSet.copyOf(allStates.get()), maxUsers, pred, minStopDistance });
			if (poit == null)
				throw new RuntimeException("Unable to construct point of interest");

			return ModReflect.run(PointOfInterestType.class, PointOfInterestType.class, "registerBlockStates",
					"func_221052_a", null, poit);
		});
	}

	public static <U extends IDynamicSerializable> RegistryObject<MemoryModuleType<U>> registerMemoryModule(String key,
			Optional<Function<Dynamic<?>, U>> p_220937_1_) {
		return MEMORY_MODULES.register(key, () -> new MemoryModuleType<U>(p_220937_1_));
	}

	public static <U extends IDynamicSerializable> RegistryObject<MemoryModuleType<U>> registerMemoryModule(String key,
			Function<Dynamic<?>, U> p_220937_1_) {
		return MEMORY_MODULES.register(key, () -> new MemoryModuleType<U>(Optional.of(p_220937_1_)));
	}

	public static <U> RegistryObject<MemoryModuleType<U>> registerMemoryModule(String key) {
		return MEMORY_MODULES.register(key, () -> new MemoryModuleType<U>(Optional.empty()));
	}

	public static <U extends Sensor<?>> RegistryObject<SensorType<U>> registerSensor(String key,
			Supplier<U> p_220996_1_) {
		return SENSORS.register(key, () -> new SensorType<>(p_220996_1_));
	}

	public static ScheduleBuilder registerSchedule(String key) {
		RegistryObject<Schedule> schedule = SCHEDULES.register(key, () -> new Schedule());
		while (!schedule.isPresent()) {
		}
		return new ScheduleBuilder(schedule.get());
	}

	public static RegistryObject<Activity> registerActivity(String key) {
		return ACTIVITIES.register(key, () -> new Activity(key));
	}

	public static void forceClinits() {

		// WizardEntity.forceClinit();
		LivingBlockEntity.forceClinit();
		// WizardActivities.forceClinit();
		// WizardPOIS.forceClinit();
		// WizardBabiesSensor.forceClinit();
		// WizardBFFLastSeenSensor.forceClinit();
		// WizardHostilesSensor.forceClinit();
		// WizardSecondaryPositionSensor.forceClinit();
		CitizenSchedule.forceClinit();
		CitizenPOIS.reg();
		CitizenMemoryAndSensors.reg();
		// Genetics.forceClinit();
	}

	public static void registerToEventBus(IEventBus bus) {
		forceClinits();
		POIS.register(bus);
		PROFESSIONS.register(bus);
		MEMORY_MODULES.register(bus);
		ACTIVITIES.register(bus);
		SENSORS.register(bus);
		SCHEDULES.register(bus);
		SERIALIZERS.register(bus);
	}
}
