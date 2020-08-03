package com.gm910.occentmod.entities.wizard.tasks.wizard;

import java.util.UUID;

import com.gm910.occentmod.api.util.serializing.UUIDSerializable;
import com.gm910.occentmod.entities.wizard.WizardEntity;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class WizardInteractWithBFFTask extends Task<WizardEntity> {
	private final int field_220446_a;
	private final float field_220447_b;
	private final int field_220449_d;
	private final MemoryModuleType<? super WizardEntity> field_220452_g;

	public WizardInteractWithBFFTask(int p_i50363_2_, MemoryModuleType<? super WizardEntity> memoryToChange,
			float p_i50363_6_, int p_i50363_7_) {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.WALK_TARGET,
				MemoryModuleStatus.VALUE_ABSENT, memoryToChange, MemoryModuleStatus.VALUE_ABSENT,
				MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT, WizardEntity.BEST_FRIEND.get(),
				MemoryModuleStatus.REGISTERED));
		this.field_220447_b = p_i50363_6_;
		this.field_220449_d = p_i50363_2_ * p_i50363_2_;
		this.field_220446_a = p_i50363_7_;
		this.field_220452_g = memoryToChange;
	}

	public static WizardInteractWithBFFTask create(int p_220445_1_, MemoryModuleType<? super WizardEntity> p_220445_2_,
			float p_220445_3_, int p_220445_4_) {
		return new WizardInteractWithBFFTask(p_220445_1_, p_220445_2_, p_220445_3_, p_220445_4_);
	}

	protected boolean shouldExecute(ServerWorld worldIn, WizardEntity owner) {
		return owner.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS).get().stream().anyMatch((p_220444_1_) -> {
			return p_220444_1_.getUniqueID().equals(owner.getBrain().getMemory(WizardEntity.BEST_FRIEND.get())
					.orElse(new UUIDSerializable(new UUID(0, 0))).value);
		});
	}

	protected void startExecuting(ServerWorld worldIn, WizardEntity entityIn, long gameTimeIn) {
		Brain<?> brain = entityIn.getBrain();
		brain.getMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent((p_220437_3_) -> {
			p_220437_3_.stream().filter((p_220440_1_) -> {
				return p_220440_1_.getUniqueID().equals(entityIn.getBrain().getMemory(WizardEntity.BEST_FRIEND.get())
						.orElse(new UUIDSerializable(new UUID(0, 0))).value);
			}).map((p_220439_0_) -> {
				return (WizardEntity) p_220439_0_;
			}).filter((p_220443_2_) -> {
				return p_220443_2_.getDistanceSq(entityIn) <= (double) this.field_220449_d;
			}).findFirst().ifPresent((p_220438_2_) -> {
				brain.setMemory(this.field_220452_g, p_220438_2_);
				brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_220438_2_));
				brain.setMemory(MemoryModuleType.WALK_TARGET,
						new WalkTarget(new EntityPosWrapper(p_220438_2_), this.field_220447_b, this.field_220446_a));
			});
		});
	}
}