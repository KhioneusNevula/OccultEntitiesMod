package com.gm910.occentmod.entities.wizard.sensors;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.gm910.occentmod.entities.wizard.WizardEntity;
import com.gm910.occentmod.init.DataInit;
import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.util.LongSerializable;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.RegistryObject;

public class WizardBFFLastSeenSensor extends Sensor<WizardEntity> {

	public static void forceClinit() {
	}

	public static final RegistryObject<SensorType<WizardBFFLastSeenSensor>> TYPE = DataInit
			.registerSensor(WizardEntity.PREFIX + "_wizard_bff_last_seen", WizardBFFLastSeenSensor::new);

	public WizardBFFLastSeenSensor() {
		this(200);
	}

	public WizardBFFLastSeenSensor(int p_i51525_1_) {
		super(p_i51525_1_);
	}

	protected void update(ServerWorld worldIn, WizardEntity entityIn) {
		findFriend(worldIn.getGameTime(), entityIn);
	}

	public Set<MemoryModuleType<?>> getUsedMemories() {
		return ImmutableSet.of(MemoryModuleType.MOBS, WizardEntity.BEST_FRIEND.get());
	}

	public static void findFriend(long gameTime, WizardEntity wizard) {
		Brain<WizardEntity> brain = wizard.getBrain();
		if (brain.hasMemory(WizardEntity.BEST_FRIEND.get())) {
			if (brain.getMemory(WizardEntity.BEST_FRIEND.get()).get().value == null) {
				return;
			} else {
				if (wizard.getBestFriend() == null) {
					return;
				}
			}
		} else {
			return;
		}
		Optional<List<LivingEntity>> optional = brain.getMemory(MemoryModuleType.MOBS);
		if (optional.isPresent()) {
			boolean flag = optional.get().stream().anyMatch((entity) -> {
				return wizard.getBestFriend().isEntityEqual(entity);
			});
			if (flag) {
				brain.setMemory(WizardEntity.BFF_LAST_SEEN_TIME.get(), LongSerializable.of(gameTime));
			}

		}
	}
}
