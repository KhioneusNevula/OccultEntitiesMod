package com.gm910.occentmod.entities.wizard.sensors;

import java.util.List;
import java.util.Set;

import com.gm910.occentmod.entities.wizard.WizardEntity;
import com.gm910.occentmod.init.AIInit;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.RegistryObject;

public class WizardSecondaryPositionSensor extends Sensor<WizardEntity> {

	public static void forceClinit() {
	}

	public static final RegistryObject<SensorType<WizardSecondaryPositionSensor>> TYPE = AIInit
			.registerSensor(WizardEntity.PREFIX + "_wizard_secondary_position", WizardSecondaryPositionSensor::new);

	private int xspread;
	private int yspread;
	private int zspread;

	public WizardSecondaryPositionSensor() {
		this(4, 2, 4);
	}

	public WizardSecondaryPositionSensor(int xspread, int yspread, int zspread) {
		super(40);
		this.xspread = Math.abs(xspread);
		this.yspread = Math.abs(yspread);
		this.zspread = Math.abs(zspread);
	}

	protected void update(ServerWorld worldIn, WizardEntity entityIn) {
		DimensionType dimensiontype = worldIn.getDimension().getType();
		BlockPos blockpos = new BlockPos(entityIn);
		List<GlobalPos> list = Lists.newArrayList();
		int i = 4;

		for (int j = -xspread; j <= xspread; ++j) {
			for (int k = -yspread; k <= yspread; ++k) {
				for (int l = -zspread; l <= zspread; ++l) {
					BlockPos blockpos1 = blockpos.add(j, k, l);
					if (entityIn.getJob().testSecondaryPosition(worldIn, entityIn, blockpos1)) {
						list.add(GlobalPos.of(dimensiontype, blockpos1));
					}
				}
			}
		}

		Brain<?> brain = entityIn.getBrain();
		if (!list.isEmpty()) {
			brain.setMemory(MemoryModuleType.SECONDARY_JOB_SITE, list);
		} else {
			brain.removeMemory(MemoryModuleType.SECONDARY_JOB_SITE);
		}

	}

	public Set<MemoryModuleType<?>> getUsedMemories() {
		return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
	}
}