package com.gm910.occentmod.entities.citizen;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.gm910.occentmod.entities.sensors.VisibleEntitySensor;
import com.gm910.occentmod.init.DataInit;
import com.gm910.occentmod.init.EntityInit;

import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraftforge.fml.RegistryObject;

public class CitizenMemoryAndSensors {

	public static void reg() {
	}

	public static final Set<RegistryObject<MemoryModuleType<?>>> MEMORY_MODULES = new HashSet<>();
	public static final Set<RegistryObject<SensorType<? extends Sensor<?>>>> SENSORS = new HashSet<>();

	public static final RegistryObject<MemoryModuleType<Collection<CitizenEntity>>> VISIBLE_CITIZENS = registerMem(
			DataInit.registerMemoryModule("visible_citizens"));

	public static final RegistryObject<SensorType<VisibleEntitySensor<CitizenEntity>>> NEAREST_CITIZENS = registerSens(
			DataInit.registerSensor("nearest_citizens", () -> {
				return new VisibleEntitySensor<CitizenEntity>(CitizenEntity.class, VISIBLE_CITIZENS.get(),
						(e) -> e.getType() == EntityInit.CITIZEN.get(), 20);
			}));

	public static <E> RegistryObject<MemoryModuleType<E>> registerMem(RegistryObject<MemoryModuleType<E>> r) {
		MEMORY_MODULES.add((RegistryObject) r);
		return r;
	}

	public static <E extends Sensor<?>> RegistryObject<SensorType<E>> registerSens(RegistryObject<SensorType<E>> r) {
		SENSORS.add((RegistryObject) r);
		return r;
	}

}
