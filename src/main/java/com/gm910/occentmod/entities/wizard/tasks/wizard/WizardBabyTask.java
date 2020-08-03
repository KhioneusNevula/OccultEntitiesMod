package com.gm910.occentmod.entities.wizard.tasks.wizard;

import java.util.Optional;

import com.gm910.occentmod.entities.wizard.WizardEntity;
import com.gm910.occentmod.init.EntityInit;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class WizardBabyTask extends Task<WizardEntity> {
	private long field_220483_a;

	public WizardBabyTask() {
		super(ImmutableMap.of(WizardEntity.BREED_TARGET.get(), MemoryModuleStatus.VALUE_PRESENT,
				MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT), 350, 350);
	}

	protected boolean shouldExecute(ServerWorld worldIn, WizardEntity owner) {
		return this.canFindBreedTarget(owner);
	}

	protected boolean shouldContinueExecuting(ServerWorld worldIn, WizardEntity entityIn, long gameTimeIn) {
		return gameTimeIn <= this.field_220483_a && this.canFindBreedTarget(entityIn);
	}

	protected void startExecuting(ServerWorld worldIn, WizardEntity entityIn, long gameTimeIn) {
		WizardEntity WizardEntity = this.getBreedTarget(entityIn);
		BrainUtil.lookApproachEachOther(entityIn, WizardEntity);
		worldIn.setEntityState(WizardEntity, (byte) 18);
		worldIn.setEntityState(entityIn, (byte) 18);
		int i = 275 + entityIn.getRNG().nextInt(50);
		this.field_220483_a = gameTimeIn + (long) i;
	}

	protected void updateTask(ServerWorld worldIn, WizardEntity owner, long gameTime) {
		WizardEntity WizardEntity = this.getBreedTarget(owner);
		if (!(owner.getDistanceSq(WizardEntity) > 5.0D)) {
			BrainUtil.lookApproachEachOther(owner, WizardEntity);
			if (gameTime >= this.field_220483_a) {
				// owner.func_223346_ep();
				// WizardEntity.func_223346_ep();
				this.func_223521_a(worldIn, owner, WizardEntity);
			} else if (owner.getRNG().nextInt(35) == 0) {
				worldIn.setEntityState(WizardEntity, (byte) 12);
				worldIn.setEntityState(owner, (byte) 12);
			}

		}
	}

	private void func_223521_a(ServerWorld p_223521_1_, WizardEntity p_223521_2_, WizardEntity p_223521_3_) {
		Optional<BlockPos> optional = this.func_220479_b(p_223521_1_, p_223521_2_);
		if (!optional.isPresent()) {
			p_223521_1_.setEntityState(p_223521_3_, (byte) 13);
			p_223521_1_.setEntityState(p_223521_2_, (byte) 13);
		} else {
			Optional<WizardEntity> optional1 = this.func_220480_a(p_223521_2_, p_223521_3_);
			if (optional1.isPresent()) {
				this.func_220477_a(p_223521_1_, optional1.get(), optional.get());
			} else {
				p_223521_1_.getPointOfInterestManager().release(optional.get());
				DebugPacketSender.func_218801_c(p_223521_1_, optional.get());
			}
		}

	}

	protected void resetTask(ServerWorld worldIn, WizardEntity entityIn, long gameTimeIn) {
		entityIn.getBrain().removeMemory(WizardEntity.BREED_TARGET.get());
	}

	private WizardEntity getBreedTarget(WizardEntity p_220482_1_) {
		return p_220482_1_.getBrain().getMemory(WizardEntity.BREED_TARGET.get()).get();
	}

	private boolean canFindBreedTarget(WizardEntity p_220478_1_) {
		Brain<WizardEntity> brain = p_220478_1_.getBrain();
		if (!brain.getMemory(WizardEntity.BREED_TARGET.get()).isPresent()) {
			return false;
		} else {
			WizardEntity wizardentity = this.getBreedTarget(p_220478_1_);
			return BrainUtil.isCorrectVisibleType(brain, WizardEntity.BREED_TARGET.get(), EntityInit.WIZARD.get())
					&& p_220478_1_.canBreed() && wizardentity.canBreed();
		}
	}

	private Optional<BlockPos> func_220479_b(ServerWorld p_220479_1_, WizardEntity p_220479_2_) {
		return p_220479_1_.getPointOfInterestManager().take(PointOfInterestType.HOME.getPredicate(), (p_220481_2_) -> {
			return this.func_223520_a(p_220479_2_, p_220481_2_);
		}, new BlockPos(p_220479_2_), 48);
	}

	private boolean func_223520_a(WizardEntity p_223520_1_, BlockPos p_223520_2_) {
		Path path = p_223520_1_.getNavigator().getPathToPos(p_223520_2_, PointOfInterestType.HOME.getValidRange());
		return path != null && path.reachesTarget();
	}

	private Optional<WizardEntity> func_220480_a(WizardEntity p_220480_1_, WizardEntity p_220480_2_) {
		WizardEntity WizardEntity = p_220480_1_.createChild(p_220480_2_);
		if (WizardEntity == null) {
			return Optional.empty();
		} else {
			p_220480_1_.setGrowingAge(6000);
			p_220480_2_.setGrowingAge(6000);
			WizardEntity.setGrowingAge(-24000);
			WizardEntity.setLocationAndAngles(p_220480_1_.getPosX(), p_220480_1_.getPosY(), p_220480_1_.getPosZ(), 0.0F,
					0.0F);
			p_220480_1_.world.addEntity(WizardEntity);
			p_220480_1_.world.setEntityState(WizardEntity, (byte) 12);
			return Optional.of(WizardEntity);
		}
	}

	private void func_220477_a(ServerWorld p_220477_1_, WizardEntity p_220477_2_, BlockPos p_220477_3_) {
		GlobalPos globalpos = GlobalPos.of(p_220477_1_.getDimension().getType(), p_220477_3_);
		p_220477_2_.getBrain().setMemory(MemoryModuleType.HOME, globalpos);
	}
}