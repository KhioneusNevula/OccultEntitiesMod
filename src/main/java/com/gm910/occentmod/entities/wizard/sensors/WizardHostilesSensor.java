package com.gm910.occentmod.entities.wizard.sensors;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.gm910.occentmod.entities.wizard.WizardEntity;
import com.gm910.occentmod.init.DataInit;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.RegistryObject;

public class WizardHostilesSensor extends Sensor<LivingEntity> {

	public static void forceClinit() {
	}

	public static final RegistryObject<SensorType<WizardHostilesSensor>> TYPE = DataInit
			.registerSensor(WizardEntity.PREFIX + "_wizard_hostiles", WizardHostilesSensor::new);

	private static final ImmutableMap<EntityType<?>, Float> HOSTILES = ImmutableMap.<EntityType<?>, Float>builder()
			.put(EntityType.DROWNED, 8.0F).put(EntityType.EVOKER, 12.0F).put(EntityType.HUSK, 8.0F)
			.put(EntityType.ILLUSIONER, 12.0F).put(EntityType.PILLAGER, 15.0F).put(EntityType.RAVAGER, 12.0F)
			.put(EntityType.VEX, 8.0F).put(EntityType.VINDICATOR, 10.0F).put(EntityType.ZOMBIE, 8.0F)
			.put(EntityType.ZOMBIE_VILLAGER, 8.0F).build();

	public Set<MemoryModuleType<?>> getUsedMemories() {
		return ImmutableSet.of(MemoryModuleType.NEAREST_HOSTILE);
	}

	protected void update(ServerWorld worldIn, LivingEntity entityIn) {
		entityIn.getBrain().setMemory(MemoryModuleType.NEAREST_HOSTILE, this.senseNearestHostile(entityIn));
	}

	private Optional<LivingEntity> senseNearestHostile(LivingEntity wizard) {
		return this.getVisibleMobs(wizard).flatMap((entitylist) -> {
			return entitylist.stream().filter(this::isHostile).filter((hostileEntity) -> {
				return this.isHostileWithinDangerDistance(wizard, hostileEntity);
			}).min((hostile1, hostile2) -> {
				return this.closerHostile(wizard, hostile1, hostile2);
			});
		});
	}

	private Optional<List<LivingEntity>> getVisibleMobs(LivingEntity p_220990_1_) {
		return p_220990_1_.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
	}

	private int closerHostile(LivingEntity wizard, LivingEntity hostile1, LivingEntity hostile2) {
		return MathHelper.floor(hostile1.getDistanceSq(wizard) - hostile2.getDistanceSq(wizard));
	}

	private boolean isHostileWithinDangerDistance(LivingEntity wizard, LivingEntity hostile) {
		float f = HOSTILES.get(hostile.getType());
		return hostile.getDistanceSq(wizard) <= (double) (f * f);
	}

	private boolean isHostile(LivingEntity p_220988_1_) {
		return HOSTILES.containsKey(p_220988_1_.getType());
	}
}