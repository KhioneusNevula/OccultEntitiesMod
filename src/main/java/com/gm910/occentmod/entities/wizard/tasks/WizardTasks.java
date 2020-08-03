package com.gm910.occentmod.entities.wizard.tasks;

import com.gm910.occentmod.entities.wizard.WizardEntity;
import com.gm910.occentmod.entities.wizard.WizardJob;
import com.gm910.occentmod.entities.wizard.tasks.wizard.AssignJobTask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.ChangeWizardJobTask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.CreatureWalkToPOITask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.GeneralClearHurtTask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.WizardBabyTask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.WizardDummyShareItemsTask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.WizardGiveHeroGiftsDummyTask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.WizardInteractWithBFFTask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.WizardNearEnemiesTask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.WizardPickupFoodTask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.WizardPrepareBattleTask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.WizardTradeDummyTask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.jobs.WizardDummyShowWaresTask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.jobs.WizardStayNearPointTask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.jobs.WizardWalkTowardSecondaryTask;
import com.gm910.occentmod.entities.wizard.tasks.wizard.jobs.WizardWorkTask;
import com.gm910.occentmod.init.EntityInit;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.BeginRaidTask;
import net.minecraft.entity.ai.brain.task.CongregateTask;
import net.minecraft.entity.ai.brain.task.DummyTask;
import net.minecraft.entity.ai.brain.task.ExpireHidingTask;
import net.minecraft.entity.ai.brain.task.ExpirePOITask;
import net.minecraft.entity.ai.brain.task.FindHidingPlaceTask;
import net.minecraft.entity.ai.brain.task.FindInteractionAndLookTargetTask;
import net.minecraft.entity.ai.brain.task.FindWalkTargetTask;
import net.minecraft.entity.ai.brain.task.FirstShuffledTask;
import net.minecraft.entity.ai.brain.task.FleeTask;
import net.minecraft.entity.ai.brain.task.GatherPOITask;
import net.minecraft.entity.ai.brain.task.HideFromRaidOnBellRingTask;
import net.minecraft.entity.ai.brain.task.InteractWithDoorTask;
import net.minecraft.entity.ai.brain.task.InteractWithEntityTask;
import net.minecraft.entity.ai.brain.task.JumpOnBedTask;
import net.minecraft.entity.ai.brain.task.LookAtEntityTask;
import net.minecraft.entity.ai.brain.task.LookTask;
import net.minecraft.entity.ai.brain.task.SleepAtHomeTask;
import net.minecraft.entity.ai.brain.task.SwimTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.UpdateActivityTask;
import net.minecraft.entity.ai.brain.task.WakeUpTask;
import net.minecraft.entity.ai.brain.task.WalkRandomlyTask;
import net.minecraft.entity.ai.brain.task.WalkToHouseTask;
import net.minecraft.entity.ai.brain.task.WalkToTargetTask;
import net.minecraft.entity.ai.brain.task.WalkToVillagerBabiesTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsLookTargetTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsPosTask;
import net.minecraft.entity.ai.brain.task.WorkTask;
import net.minecraft.village.PointOfInterestType;

public class WizardTasks {
	public static ImmutableList<Pair<Integer, ? extends Task<? super WizardEntity>>> core(WizardJob profession,
			float p_220638_1_) {
		return ImmutableList.of(Pair.of(0, new SwimTask(0.4F, 0.8F)), Pair.of(0, new InteractWithDoorTask()),
				Pair.of(0, new LookTask(45, 90)), Pair.of(0, new WizardPrepareBattleTask()),
				Pair.of(0, new WakeUpTask()), Pair.of(0, new HideFromRaidOnBellRingTask()),
				Pair.of(0, new BeginRaidTask()), Pair.of(1, new WalkToTargetTask(200)),
				Pair.of(2, new WizardTradeDummyTask(p_220638_1_)), Pair.of(5, new WizardPickupFoodTask()),
				Pair.of(10, new GatherPOITask(profession.poi, MemoryModuleType.JOB_SITE, true)),
				Pair.of(10, new GatherPOITask(PointOfInterestType.HOME, MemoryModuleType.HOME, false)),
				// Pair.of(10, new GatherPOITask(PointOfInterestType.MEETING,
				// MemoryModuleType.MEETING_POINT, true)),
				Pair.of(10, new AssignJobTask()), Pair.of(10, new ChangeWizardJobTask()));
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super WizardEntity>>> work(WizardJob profession,
			float p_220639_1_) {
		return ImmutableList.of(lookAtPlayerOrWizard(),
				Pair.of(5,
						new FirstShuffledTask<>(ImmutableList.of(Pair.of(new WizardWorkTask(4), 2),
								Pair.of(new WalkTowardsPosTask(MemoryModuleType.JOB_SITE, 1, 10), 5),
								Pair.of(new WizardWalkTowardSecondaryTask(MemoryModuleType.SECONDARY_JOB_SITE, 0.4F, 1,
										6, MemoryModuleType.JOB_SITE), 5)))),
				Pair.of(10, new WizardDummyShowWaresTask(400, 1600)),
				Pair.of(10, new FindInteractionAndLookTargetTask(EntityType.PLAYER, 4)),
				Pair.of(2, new WizardStayNearPointTask(MemoryModuleType.JOB_SITE, p_220639_1_, 9, 100, 1200)),
				Pair.of(3, new WizardGiveHeroGiftsDummyTask(100)),
				Pair.of(3, new ExpirePOITask(profession.poi, MemoryModuleType.JOB_SITE)),
				Pair.of(99, new UpdateActivityTask()));
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super WizardEntity>>> play(float walkingSpeed) {
		return ImmutableList.of(Pair.of(0, new WalkToTargetTask(100)), lookAtMany(),
				Pair.of(5, new WalkToVillagerBabiesTask()),
				Pair.of(5, new FirstShuffledTask<>(
						ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleStatus.VALUE_ABSENT),
						ImmutableList.of(
								Pair.of(InteractWithEntityTask.func_220445_a(EntityType.VILLAGER, 8,
										MemoryModuleType.INTERACTION_TARGET, walkingSpeed, 2), 2),
								Pair.of(InteractWithEntityTask.func_220445_a(EntityType.CAT, 8,
										MemoryModuleType.INTERACTION_TARGET, walkingSpeed, 2), 1),
								Pair.of(new FindWalkTargetTask(walkingSpeed), 1),
								Pair.of(new WalkTowardsLookTargetTask(walkingSpeed, 2), 1),
								Pair.of(new JumpOnBedTask(walkingSpeed), 2), Pair.of(new DummyTask(20, 40), 2)))),
				Pair.of(99, new UpdateActivityTask()));
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super WizardEntity>>> rest(WizardJob profession,
			float walkingSpeed) {
		return ImmutableList.of(
				Pair.of(2, new WizardStayNearPointTask(MemoryModuleType.HOME, walkingSpeed, 1, 150, 1200)),
				Pair.of(3, new ExpirePOITask(PointOfInterestType.HOME, MemoryModuleType.HOME)),
				Pair.of(3, new SleepAtHomeTask()),
				Pair.of(5,
						new FirstShuffledTask<>(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleStatus.VALUE_ABSENT),
								ImmutableList.of(Pair.of(new WalkToHouseTask(walkingSpeed), 1),
										Pair.of(new WalkRandomlyTask(walkingSpeed), 4),
										Pair.of(new CreatureWalkToPOITask(walkingSpeed, 4), 2),
										Pair.of(new DummyTask(20, 40), 2)))),
				lookAtPlayerOrWizard(), Pair.of(99, new UpdateActivityTask()));
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super WizardEntity>>> meet(WizardJob profession,
			float p_220637_1_) {
		return ImmutableList.of(Pair.of(2,
				new FirstShuffledTask<>(ImmutableList.of(Pair.of(new WorkTask(MemoryModuleType.MEETING_POINT, 40), 2),
						Pair.of(new CongregateTask(), 2)))),
				Pair.of(10, new WizardDummyShowWaresTask(400, 1600)),
				Pair.of(10, new FindInteractionAndLookTargetTask(EntityType.PLAYER, 4)),
				Pair.of(2, new WizardStayNearPointTask(MemoryModuleType.MEETING_POINT, p_220637_1_, 6, 100, 200)),
				Pair.of(3, new WizardGiveHeroGiftsDummyTask(100)),
				Pair.of(3, new ExpirePOITask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT)),
				Pair.of(3,
						new MultiTask<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
								MultiTask.Ordering.ORDERED, MultiTask.RunType.RUN_ONE,
								ImmutableList.of(Pair.of(new WizardDummyShareItemsTask(), 1)))),
				lookAtMany(), Pair.of(99, new UpdateActivityTask()));
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super WizardEntity>>> idle(WizardJob profession,
			float p_220641_1_) {
		return ImmutableList.of(
				Pair.of(2, new FirstShuffledTask<>(ImmutableList.of(
						Pair.of(InteractWithEntityTask.func_220445_a(EntityType.VILLAGER, 8,
								MemoryModuleType.INTERACTION_TARGET, p_220641_1_, 2), 3),
						Pair.of(new WizardInteractWithBFFTask(8, WizardEntity.BREED_TARGET.get(), p_220641_1_, 2), 2),
						Pair.of(new InteractWithEntityTask<>(EntityInit.WIZARD.get(), 8, WizardEntity::canBreed,
								WizardEntity::canBreed, WizardEntity.BREED_TARGET.get(), p_220641_1_, 2), 1),
						Pair.of(InteractWithEntityTask.func_220445_a(EntityType.CAT, 8,
								MemoryModuleType.INTERACTION_TARGET, p_220641_1_, 2), 1),
						Pair.of(new FindWalkTargetTask(p_220641_1_), 1),
						Pair.of(new WalkTowardsLookTargetTask(p_220641_1_, 2), 1),
						Pair.of(new JumpOnBedTask(p_220641_1_), 1), Pair.of(new DummyTask(30, 60), 1)))),
				Pair.of(3, new WizardGiveHeroGiftsDummyTask(100)),
				Pair.of(3, new FindInteractionAndLookTargetTask(EntityType.PLAYER, 4)),
				Pair.of(3, new WizardDummyShowWaresTask(400, 1600)),
				Pair.of(3,
						new MultiTask<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
								MultiTask.Ordering.ORDERED, MultiTask.RunType.RUN_ONE,
								ImmutableList.of(Pair.of(new WizardDummyShareItemsTask(), 1)))),
				Pair.of(3,
						new MultiTask<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.BREED_TARGET),
								MultiTask.Ordering.ORDERED, MultiTask.RunType.RUN_ONE,
								ImmutableList.of(Pair.of(new WizardBabyTask(), 1)))),
				lookAtMany(), Pair.of(99, new UpdateActivityTask()));
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super WizardEntity>>> battle(WizardJob profession,
			float p_220636_1_) {
		float f = p_220636_1_ * 1.5F;
		return ImmutableList.of(Pair.of(0, new GeneralClearHurtTask()),
				Pair.of(1, new WizardNearEnemiesTask(MemoryModuleType.NEAREST_HOSTILE, f)),
				Pair.of(1, new FleeTask(MemoryModuleType.HURT_BY_ENTITY, f)),
				Pair.of(3, new FindWalkTargetTask(f, 2, 2)), lookAtPlayerOrWizard());
	}

	/*public static ImmutableList<Pair<Integer, ? extends Task<? super WizardEntity>>> preRaid(WizardJob profession,
			float p_220642_1_) {
		return ImmutableList.of(Pair.of(0, new RingBellTask()), Pair.of(0,
				new FirstShuffledTask<>(ImmutableList.of(Pair
						.of(new StayNearPointTask(MemoryModuleType.MEETING_POINT, p_220642_1_ * 1.5F, 2, 150, 200), 6),
						Pair.of(new FindWalkTargetTask(p_220642_1_ * 1.5F), 2)))),
				lookAtPlayerOrWizard(), Pair.of(99, new ForgetRaidTask()));
	}
	
	public static ImmutableList<Pair<Integer, ? extends Task<? super WizardEntity>>> raid(WizardJob profession,
			float p_220640_1_) {
		return ImmutableList.of(
				Pair.of(0,
						new FirstShuffledTask<>(ImmutableList.of(Pair.of(new GoOutsideAfterRaidTask(p_220640_1_), 5),
								Pair.of(new FindWalkTargetAfterRaidVictoryTask(p_220640_1_ * 1.1F), 2)))),
				Pair.of(0, new CelebrateRaidVictoryTask(600, 600)),
				Pair.of(2, new FindHidingPlaceDuringRaidTask(24, p_220640_1_ * 1.4F)), lookAtPlayerOrWizard(),
				Pair.of(99, new ForgetRaidTask()));
	}*/

	public static ImmutableList<Pair<Integer, ? extends Task<? super WizardEntity>>> hide(WizardJob profession,
			float p_220644_1_) {
		int i = 2;
		return ImmutableList.of(Pair.of(0, new ExpireHidingTask(15, 2)),
				Pair.of(1, new FindHidingPlaceTask(32, p_220644_1_ * 1.25F, 2)), lookAtPlayerOrWizard());
	}

	private static Pair<Integer, Task<LivingEntity>> lookAtMany() {
		return Pair.of(5,
				new FirstShuffledTask<>(ImmutableList.of(Pair.of(new LookAtEntityTask(EntityType.CAT, 8.0F), 8),
						Pair.of(new LookAtEntityTask(EntityType.VILLAGER, 8.0F), 2),
						Pair.of(new LookAtEntityTask(EntityType.PLAYER, 8.0F), 2),
						Pair.of(new LookAtEntityTask(EntityClassification.CREATURE, 8.0F), 1),
						Pair.of(new LookAtEntityTask(EntityClassification.WATER_CREATURE, 8.0F), 1),
						Pair.of(new LookAtEntityTask(EntityClassification.MONSTER, 8.0F), 1),
						Pair.of(new DummyTask(30, 60), 2))));
	}

	private static Pair<Integer, Task<LivingEntity>> lookAtPlayerOrWizard() {
		return Pair.of(5,
				new FirstShuffledTask<>(ImmutableList.of(
						Pair.of(new LookAtEntityTask(EntityInit.WIZARD.get(), 8.0F), 2),
						Pair.of(new LookAtEntityTask(EntityType.PLAYER, 8.0F), 2), Pair.of(new DummyTask(30, 60), 8))));
	}
}
