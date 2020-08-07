package com.gm910.occentmod.entities.wizard;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.gm910.occentmod.api.networking.messages.Networking;
import com.gm910.occentmod.api.networking.messages.types.TaskParticles;
import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.api.util.serializing.EnumSerializable;
import com.gm910.occentmod.api.util.serializing.UUIDSerializable;
import com.gm910.occentmod.entities.wizard.sensors.WizardBFFLastSeenSensor;
import com.gm910.occentmod.entities.wizard.sensors.WizardBabiesSensor;
import com.gm910.occentmod.entities.wizard.sensors.WizardHostilesSensor;
import com.gm910.occentmod.entities.wizard.sensors.WizardSecondaryPositionSensor;
import com.gm910.occentmod.entities.wizard.tasks.WizardTasks;
import com.gm910.occentmod.entities.wizard.tasks.seidhvaettr.SeidhTask;
import com.gm910.occentmod.init.DataInit;
import com.gm910.occentmod.init.EntityInit;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.LongSerializable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;

@Deprecated
public class WizardEntity extends AgeableEntity {

	public static final String PREFIX = EntityInit.WIZARD.getId().getPath();

	public static void forceClinit() {
	}

	public static final RegistryObject<MemoryModuleType<List<LivingEntity>>> VISIBLE_BABIES = DataInit
			.registerMemoryModule(PREFIX + "_visible_babies");

	public static final RegistryObject<MemoryModuleType<UUIDSerializable>> BEST_FRIEND = DataInit
			.registerMemoryModule(PREFIX + "_best_friend", Optional.of(UUIDSerializable::new));

	public static final RegistryObject<MemoryModuleType<EnumSerializable<WizardJob>>> PROFESSION = DataInit
			.registerMemoryModule(PREFIX + "_profession", (e) -> new EnumSerializable<>(e, WizardJob.class));

	public static final RegistryObject<MemoryModuleType<LongSerializable>> BFF_LAST_SEEN_TIME = DataInit
			.registerMemoryModule(PREFIX + "_bff_last_seen_time", LongSerializable::deserialize);

	public static final RegistryObject<MemoryModuleType<WizardEntity>> BREED_TARGET = DataInit
			.registerMemoryModule(PREFIX + "_breed_target");

	public final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME,
			MemoryModuleType.JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.MOBS,
			MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER,
			MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET,
			MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.INTERACTABLE_DOORS,
			MemoryModuleType.field_225462_q, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY,
			MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE,
			MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT,
			MemoryModuleType.field_226332_A_, MemoryModuleType.LAST_WORKED_AT_POI, BEST_FRIEND.get(),
			BREED_TARGET.get(), VISIBLE_BABIES.get());

	public final ImmutableSet<SensorType<? extends Sensor<? super WizardEntity>>> SENSOR_TYPES = ImmutableSet
			.<SensorType<? extends Sensor<? super WizardEntity>>>of(SensorType.NEAREST_LIVING_ENTITIES,
					SensorType.NEAREST_PLAYERS, SensorType.INTERACTABLE_DOORS, SensorType.NEAREST_BED,
					SensorType.HURT_BY, WizardBFFLastSeenSensor.TYPE.get(), WizardSecondaryPositionSensor.TYPE.get(),
					WizardBabiesSensor.TYPE.get(), WizardHostilesSensor.TYPE.get());

	public final Map<MemoryModuleType<GlobalPos>, BiPredicate<WizardEntity, PointOfInterestType>> field_213774_bB = ImmutableMap
			.of(MemoryModuleType.HOME, (wiz, poi) -> {
				return poi == PointOfInterestType.HOME;
			}, MemoryModuleType.JOB_SITE, (wiz, poi) -> {
				return wiz.getJob().poi == poi;
			}/*, MemoryModuleType.MEETING_POINT, (wiz, poi) -> {
				return poi == PointOfInterestType.MEETING;
				}*/);

	public WizardEntity(EntityType<? extends WizardEntity> type, World worldIn) {
		super(type, worldIn);
		this.moveController = new FlyingMovementController(this, 50, false);
		((FlyingPathNavigator) this.getNavigator()).setCanOpenDoors(true);
		((FlyingPathNavigator) this.getNavigator()).setCanEnterDoors(true);
		this.getNavigator().setCanSwim(true);
		this.setCanPickUpLoot(true);

		// this.setVillagerData(this.getVillagerData().withType(villagerType).withProfession(VillagerProfession.NONE));
		this.brain = this.createBrain(new Dynamic<>(NBTDynamicOps.INSTANCE, new CompoundNBT()));
	}

	@Override
	public PathNavigator createNavigator(World worldIn) {
		return new FlyingPathNavigator(this, worldIn);
	}

	@Override
	public MovementController getMoveHelper() {
		// TODO Auto-generated method stub
		return super.getMoveHelper();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Brain<WizardEntity> getBrain() {
		return (Brain<WizardEntity>) super.getBrain();
	}

	public WizardEntity getBestFriend() {
		Optional<UUIDSerializable> f = this.brain.getMemory(BEST_FRIEND.get());
		if (f.isPresent()) {
			UUID uu = f.get().value;
			if (!world.isRemote) {
				return (WizardEntity) ServerPos.getEntityFromUUID(uu, world.getServer());
			} else {
				return (WizardEntity) ServerPos.getEntityFromUUID(uu, world, this.getPosition(), 48);
			}
		}
		return null;
	}

	public void setBestFriend(WizardEntity other) {
		this.brain.setMemory(BEST_FRIEND.get(), new UUIDSerializable(other.getUniqueID()));
	}

	public WizardJob getJob() {
		return getJob(this.brain);
	}

	public WizardJob getJob(Brain<?> brain) {
		Optional<EnumSerializable<WizardJob>> jobop = brain.getMemory(PROFESSION.get());
		if (jobop == null) {
			return WizardJob.JOBLESS;
		}
		if (jobop.isPresent()) {
			return jobop.get().value;
		}
		return WizardJob.JOBLESS;
	}

	public void setJob(WizardJob job) {
		this.brain.setMemory(PROFESSION.get(), new EnumSerializable<>(job));
	}

	@Override
	public Brain<?> createBrain(Dynamic<?> dynamicIn) {
		ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME,
				MemoryModuleType.JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.MOBS,
				MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_PLAYERS,
				MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET,
				MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH,
				MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.field_225462_q, MemoryModuleType.NEAREST_BED,
				MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE,
				MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
				MemoryModuleType.LAST_SLEPT, MemoryModuleType.field_226332_A_, MemoryModuleType.LAST_WORKED_AT_POI,
				BEST_FRIEND.get(), BREED_TARGET.get(), VISIBLE_BABIES.get());

		ImmutableSet<SensorType<? extends Sensor<? super WizardEntity>>> SENSOR_TYPES = ImmutableSet
				.<SensorType<? extends Sensor<? super WizardEntity>>>of(SensorType.NEAREST_LIVING_ENTITIES,
						SensorType.NEAREST_PLAYERS, SensorType.INTERACTABLE_DOORS, SensorType.NEAREST_BED,
						SensorType.HURT_BY, WizardBFFLastSeenSensor.TYPE.get(),
						WizardSecondaryPositionSensor.TYPE.get(), WizardBabiesSensor.TYPE.get(),
						WizardHostilesSensor.TYPE.get());

		Brain<WizardEntity> brain = new Brain<WizardEntity>(MEMORY_TYPES, SENSOR_TYPES, dynamicIn);
		this.initBrain(brain);
		return brain;
	}

	public void resetBrain(ServerWorld serverWorldIn) {
		Brain<WizardEntity> brain = this.getBrain();
		brain.stopAllTasks(serverWorldIn, this);
		this.brain = brain.copy();
		this.initBrain(this.getBrain());
	}

	private void initBrain(Brain<WizardEntity> brain) {

		brain.setMemory(PROFESSION.get(), new EnumSerializable<>(WizardJob.JOBLESS));

		float f = (float) this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
		if (this.isChild()) {
			brain.setSchedule(Schedule.VILLAGER_BABY);

			brain.registerActivity(Activity.PLAY, WizardTasks.play(f));
		} else {
			brain.setSchedule(Schedule.VILLAGER_DEFAULT);

			brain.registerActivity(Activity.WORK, WizardTasks.work(getJob(brain), f),
					ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_PRESENT)));
		}

		brain.registerActivity(Activity.CORE, WizardTasks.core(this.getJob(brain), f));
		brain.registerActivity(Activity.MEET, WizardTasks.meet(getJob(brain), f),
				ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryModuleStatus.VALUE_PRESENT)));
		brain.registerActivity(Activity.REST, WizardTasks.rest(getJob(brain), f));
		brain.registerActivity(Activity.IDLE, WizardTasks.idle(getJob(brain), f));
		brain.registerActivity(WizardActivities.BATTLE.get(), WizardTasks.battle(getJob(brain), f));
		brain.registerActivity(Activity.HIDE, WizardTasks.hide(getJob(brain), f));
		brain.setDefaultActivities(ImmutableSet.of(Activity.CORE));
		brain.setFallbackActivity(Activity.IDLE);
		brain.switchTo(Activity.IDLE);
		brain.updateActivity(this.world.getDayTime(), this.world.getGameTime());
	}

	/**
	 * This is called when Entity's growing age timer reaches 0 (negative values are
	 * considered as a child, positive as an adult)
	 */
	@Override
	public void onGrowingAdult() {
		super.onGrowingAdult();
		if (this.world instanceof ServerWorld) {
			this.resetBrain((ServerWorld) this.world);
		}

	}

	@Override
	public void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
		this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
	}

	@Override
	public void updateAITasks() {
		this.world.getProfiler().startSection("brain");
		this.getBrain().tick((ServerWorld) this.world, this);
		this.world.getProfiler().endSection();

		super.updateAITasks();
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick() {
		super.tick();
		if (this.ticksExisted % 400 == 0) {
			System.out.println("WizardTask: " + this.brain.getRunningTasks().collect(Collectors.toList()));
			System.out.println("WizardJob: " + this.getJob());
			// System.out.println("WizardSchedule: " +
			// this.brain.getSchedule().getRegistryName());
			// System.out.println("POIS: " +
			// ForgeRegistries.POI_TYPES.getEntries().stream().map((e) -> e.getValue())
			// .collect(Collectors.toList()));
			// System.out.println("WizardVisibleBabies: " +
			// this.brain.getMemory(VISIBLE_BABIES.get()).orElse(null) + " ");
			System.out.println("WizardJobsite: " + this.brain.getMemory(MemoryModuleType.JOB_SITE).orElse(null) + " ");
			if (this.ticksExisted % 800 == 0)
				this.brain.getMemory(MemoryModuleType.JOB_SITE).ifPresent((gpos) -> {
					BlockPos pos = gpos.getPos();

					for (int i = 0; i < world.rand.nextInt(100); i++) {
						Networking.sendToAll(new TaskParticles(ParticleTypes.DRAGON_BREATH,
								pos.getX() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(),
								pos.getY() + world.rand.nextDouble() * 3,
								pos.getZ() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(),
								world.dimension.getType(), world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5,
								world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5,
								world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5, false, false, false));
					}
				});
		}
	}

	public boolean wantsItem(ItemStack itemstack) {
		// TODO
		return false;
	}

	@Override
	public boolean processInteract(PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		boolean flag = itemstack.getItem() == Items.NAME_TAG;
		if (flag) {
			itemstack.interactWithEntity(player, this, hand);
			return true;
		} else {
			return super.processInteract(player, hand);
		}
	}

	@Override
	public void registerData() {
		super.registerData();
		// this.dataManager.register(VILLAGER_DATA, new
		// VillagerData(IVillagerType.PLAINS, VillagerProfession.NONE, 1));
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
	}

	/**
	 * (abstract) public helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);

		if (this.world instanceof ServerWorld) {
			this.resetBrain((ServerWorld) this.world);
		}

	}

	@Override
	public boolean canDespawn(double distanceToClosestPlayer) {
		return false;
	}

	public void notifySeidhvaettir(SeidhTask task) {
		// TODO
	}

	@Override
	@Nullable
	public SoundEvent getAmbientSound() {
		if (this.isSleeping()) {
			return null;
		} else {
			return SoundEvents.ENTITY_VILLAGER_AMBIENT;
		}
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_VILLAGER_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_VILLAGER_DEATH;
	}

	/**
	 * Hint to AI tasks that we were attacked by the passed EntityLivingBase and
	 * should retaliate. Is not guaranteed to change our actual active target (for
	 * example if we are currently busy attacking someone else)
	 */
	@Override
	public void setRevengeTarget(@Nullable LivingEntity livingBase) {
		if (livingBase != null && this.world instanceof ServerWorld) {
			if (this.isAlive() && livingBase instanceof PlayerEntity) {
				this.world.setEntityState(this, (byte) 13);
			}
		}

		super.setRevengeTarget(livingBase);
	}

	/**
	 * Called when the mob's health reaches 0.
	 */
	@Override
	public void onDeath(DamageSource cause) {
		LOGGER.info("Villager {} died, message: '{}'", this, cause.getDeathMessage(this).getString());
		Entity entity = cause.getTrueSource();

		this.releasePOIS(MemoryModuleType.HOME);
		this.releasePOIS(MemoryModuleType.JOB_SITE);
		// this.releasePOIS(MemoryModuleType.MEETING_POINT);
		super.onDeath(cause);
	}

	public void releasePOIS(MemoryModuleType<GlobalPos> p_213742_1_) {
		if (this.world instanceof ServerWorld) {
			MinecraftServer minecraftserver = ((ServerWorld) this.world).getServer();
			this.brain.getMemory(p_213742_1_).ifPresent((p_213752_3_) -> {
				ServerWorld serverworld = minecraftserver.getWorld(p_213752_3_.getDimension());
				PointOfInterestManager pointofinterestmanager = serverworld.getPointOfInterestManager();
				Optional<PointOfInterestType> optional = pointofinterestmanager.getType(p_213752_3_.getPos());
				BiPredicate<WizardEntity, PointOfInterestType> bipredicate = field_213774_bB.get(p_213742_1_);
				if (optional.isPresent() && bipredicate.test(this, optional.get())) {
					pointofinterestmanager.release(p_213752_3_.getPos());
					DebugPacketSender.func_218801_c(serverworld, p_213752_3_.getPos());
				}

			});
		}
	}

	/**
	 * Handler for {@link World#setEntityState}
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleStatusUpdate(byte id) {
		/*if (id == 12) {
			this.spawnParticles(ParticleTypes.HEART);
		} else if (id == 13) {
			this.spawnParticles(ParticleTypes.ANGRY_VILLAGER);
		} else if (id == 14) {
			this.spawnParticles(ParticleTypes.HAPPY_VILLAGER);
		} else if (id == 42) {
			this.spawnParticles(ParticleTypes.SPLASH);
		} else {
			super.handleStatusUpdate(id);
		}*/
		super.handleStatusUpdate(id);

	}

	public boolean canBreed() {
		return false;
	}

	@Override
	@Nullable
	public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
			@Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		if (reason == SpawnReason.BREEDING) {
			// this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.NONE));
		}

		if (reason == SpawnReason.COMMAND || reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.SPAWNER
				|| reason == SpawnReason.DISPENSER) {
			// this.setVillagerData(this.getVillagerData().withType(IVillagerType.byBiome(worldIn.getBiome(new
			// BlockPos(this)))));
		}

		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	public WizardEntity createChild(AgeableEntity ageable) {
		double d0 = this.rand.nextDouble();
		/*IVillagerType ivillagertype;
		if (d0 < 0.5D) {
			ivillagertype = IVillagerType.byBiome(this.world.getBiome(new BlockPos(this)));
		} else if (d0 < 0.75D) {
			ivillagertype = this.getVillagerData().getType();
		} else {
			ivillagertype = ((WizardEntity) ageable).getVillagerData().getType();
		}*/

		WizardEntity WizardEntity = new WizardEntity(EntityInit.WIZARD.get(), this.world);
		WizardEntity.onInitialSpawn(this.world, this.world.getDifficultyForLocation(new BlockPos(WizardEntity)),
				SpawnReason.BREEDING, (ILivingEntityData) null, (CompoundNBT) null);
		return WizardEntity;
	}

}
