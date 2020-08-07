package com.gm910.occentmod.entities.wizard.sensors;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.entities.wizard.WizardEntity;
import com.gm910.occentmod.init.DataInit;
import com.gm910.occentmod.init.EntityInit;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.RegistryObject;

public class WizardBabiesSensor extends Sensor<LivingEntity> {

	public static void forceClinit() {
	}

	public static final RegistryObject<SensorType<WizardBabiesSensor>> TYPE = DataInit
			.registerSensor(WizardEntity.PREFIX + "_wizard_babies", WizardBabiesSensor::new);

	public Set<MemoryModuleType<?>> getUsedMemories() {
		return ImmutableSet.of(WizardEntity.VISIBLE_BABIES.get());
	}

	protected void update(ServerWorld worldIn, LivingEntity entityIn) {
		entityIn.getBrain().setMemory(WizardEntity.VISIBLE_BABIES.get(), this.getCorrectEntities(entityIn));
	}

	private List<LivingEntity> getCorrectEntities(LivingEntity thisEntity) {
		return this.getVisibleMobs(thisEntity).stream().filter(this::testIfWizardBaby).collect(Collectors.toList());
	}

	private boolean testIfWizardBaby(LivingEntity toTest) {
		return toTest.getType() == EntityInit.WIZARD.get() && toTest.isChild();
	}

	private List<LivingEntity> getVisibleMobs(LivingEntity thisEntity) {
		return thisEntity.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS).orElse(Lists.newArrayList());
	}
}