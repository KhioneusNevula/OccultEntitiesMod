package com.gm910.occentmod.entities.citizen.mind_and_traits.personality.hobbies;

import java.util.Objects;
import java.util.Optional;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.CitizenTask;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class DummyHobbyTask extends CitizenTask {
	private long time;
	private final int maxDistanceFromSite;
	private MemoryModuleType<GlobalPos> memory;

	public DummyHobbyTask(MemoryModuleType<GlobalPos> memory, int p_i50342_2_) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, memory,
				MemoryModuleStatus.VALUE_PRESENT));
		this.maxDistanceFromSite = p_i50342_2_;
		this.memory = memory;
	}

	protected boolean shouldExecute(ServerWorld worldIn, CitizenEntity owner) {
		Optional<GlobalPos> optional = owner.getBrain().getMemory(memory);
		return optional.isPresent() && Objects.equals(worldIn.getDimension().getType(), optional.get().getDimension())
				&& optional.get().getPos().withinDistance(owner.getPositionVec(), (double) this.maxDistanceFromSite);
	}

	protected void startExecuting(ServerWorld worldIn, CitizenEntity entityIn, long gameTimeIn) {
		if (gameTimeIn > this.time) {
			Optional<Vec3d> optional = Optional.ofNullable(RandomPositionGenerator.getLandPos(entityIn, 8, 6));
			entityIn.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220564_0_) -> {
				return new WalkTarget(p_220564_0_, 0.4F, 1);
			}));
			this.time = gameTimeIn + 180L;
		}

	}

}