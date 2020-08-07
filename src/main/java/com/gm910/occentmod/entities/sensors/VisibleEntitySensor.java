package com.gm910.occentmod.entities.sensors;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.world.server.ServerWorld;

public class VisibleEntitySensor extends Sensor<LivingEntity> {
	private final EntityPredicate field_220982_b;

	private MemoryModuleType<Collection<LivingEntity>> usedMemory;
	private Predicate<LivingEntity> tester;
	private double distance;

	public VisibleEntitySensor(MemoryModuleType<Collection<LivingEntity>> usedMemory, Predicate<LivingEntity> tester,
			double distance) {
		this.usedMemory = usedMemory;
		this.tester = tester;
		this.distance = distance;
		this.field_220982_b = (new EntityPredicate()).setDistance(distance).allowFriendlyFire().setSkipAttackChecks()
				.setLineOfSiteRequired();
	}

	public Set<MemoryModuleType<?>> getUsedMemories() {
		return ImmutableSet.of(usedMemory);
	}

	protected void update(ServerWorld worldIn, LivingEntity entityIn) {
		List<LivingEntity> list = worldIn.getEntitiesWithinAABB(LivingEntity.class,
				entityIn.getBoundingBox().grow(distance, distance, distance), (p_220980_1_) -> {
					return p_220980_1_ != entityIn && p_220980_1_.isAlive();
				});
		list.sort(Comparator.comparingDouble(entityIn::getDistanceSq));
		Brain<?> brain = entityIn.getBrain();
		brain.setMemory(MemoryModuleType.MOBS, list);
		brain.setMemory(MemoryModuleType.VISIBLE_MOBS, list.stream().filter((p_220981_1_) -> {
			return field_220982_b.canTarget(entityIn, p_220981_1_);
		}).filter(entityIn::canEntityBeSeen).filter(tester).collect(Collectors.toList()));
	}

}