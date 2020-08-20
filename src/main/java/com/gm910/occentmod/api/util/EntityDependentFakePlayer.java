package com.gm910.occentmod.api.util;

import java.util.Collection;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.Dynamic;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.stats.Stat;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.ITeleporter;

public class EntityDependentFakePlayer extends PlayerEntity {
	GameType gameType = GameType.SURVIVAL;

	private LivingEntity owner;

	public EntityDependentFakePlayer(World worldIn, GameProfile gameProfileIn, @Nullable LivingEntity owner) {
		super(worldIn, gameProfileIn);
		this.owner = owner;
	}

	public LivingEntity getOwner() {
		return owner;
	}

	public void setOwner(LivingEntity owner) {
		this.owner = owner;
	}

	@Override
	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	@Override
	public boolean blockActionRestricted(World p_223729_1_, BlockPos p_223729_2_, GameType p_223729_3_) {
		return super.blockActionRestricted(p_223729_1_, p_223729_2_, gameType);
	}

	@Override
	public boolean isSpectator() {
		return false;
	}

	@Override
	public boolean isCreative() {
		return gameType.isCreative();
	}

	@Override
	public void addExhaustion(float exhaustion) {
		super.addExhaustion(exhaustion);
	}

	@Override
	public void addExperienceLevel(int levels) {
		super.addExperienceLevel(levels);
	}

	@Override
	public boolean addItemStackToInventory(ItemStack p_191521_1_) {
		return super.addItemStackToInventory(p_191521_1_);
	}

	@Override
	public void addMovementStat(double p_71000_1_, double p_71000_3_, double p_71000_5_) {
		super.addMovementStat(p_71000_1_, p_71000_3_, p_71000_5_);
	}

	@Override
	public void addPassenger(Entity passenger) {
		ModReflect.run(Entity.class, Void.class, "addPassenger", "func_184200_o", owner,
				new Class<?>[] { Entity.class }, new Object[] { passenger });
	}

	@Override
	public boolean addPotionEffect(EffectInstance effectInstanceIn) {
		return owner.addPotionEffect(effectInstanceIn);
	}

	@Override
	public void addScore(int scoreIn) {
		super.addScore(scoreIn);
	}

	@Override
	public boolean addShoulderEntity(CompoundNBT p_192027_1_) {
		return super.addShoulderEntity(p_192027_1_);
	}

	@Override
	public void addStat(ResourceLocation p_195067_1_, int p_195067_2_) {
		super.addStat(p_195067_1_, p_195067_2_);
	}

	@Override
	public void addStat(ResourceLocation stat) {
		super.addStat(stat);
	}

	@Override
	public void addStat(Stat<?> stat) {
		super.addStat(stat);
	}

	@Override
	public void addStat(Stat<?> stat, int amount) {
		super.addStat(stat, amount);
	}

	@Override
	public boolean addTag(String tag) {
		return owner.addTag(tag);
	}

	@Override
	public void addTrackingPlayer(ServerPlayerEntity player) {
		owner.addTrackingPlayer(player);
	}

	@Override
	public void addVelocity(double x, double y, double z) {
		owner.addVelocity(x, y, z);
	}

	@Override
	protected float applyArmorCalculations(DamageSource source, float damage) {
		return ModReflect.run(LivingEntity.class, float.class, "applyArmorCalculations", "func_70655_b", owner,
				new Class[] { DamageSource.class, float.class }, new Object[] { source, damage });
	}

	@Override
	public boolean allowLogging() {
		return owner.allowLogging();
	}

	@Override
	protected void applyEnchantments(LivingEntity entityLivingBaseIn, Entity entityIn) {
		ModReflect.run(Entity.class, Void.class, "applyEnchantments", "func_174815_a", owner,
				new Class[] { LivingEntity.class, Entity.class }, new Object[] { entityLivingBaseIn, entityIn });

	}

	@Override
	public void applyEntityCollision(Entity entityIn) {
		owner.applyEntityCollision(entityIn);
	}

	@Override
	public void applyOrientationToEntity(Entity entityToUpdate) {
		owner.applyOrientationToEntity(entityToUpdate);
	}

	@Override
	public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
		return owner.applyPlayerInteraction(player, vec, hand);
	}

	@Override
	protected float applyPotionDamageCalculations(DamageSource source, float damage) {
		return ModReflect.run(LivingEntity.class, float.class, "applyPotionDamageCalculations", "func_70672_c", owner,
				new Class[] { DamageSource.class, float.class }, new Object[] { source, damage });
	}

	@Override
	public boolean areEyesInFluid(Tag<Fluid> p_213290_1_, boolean checkChunkLoaded) {
		return owner.areEyesInFluid(p_213290_1_, checkChunkLoaded);
	}

	@Override
	public boolean areEyesInFluid(Tag<Fluid> tagIn) {
		return owner.areEyesInFluid(tagIn);
	}

	@Override
	public boolean attackable() {
		return owner.attackable();
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return owner.attackEntityAsMob(entityIn);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return owner.attackEntityFrom(source, amount);
	}

	@Override
	public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
		super.attackTargetEntityWithCurrentItem(targetEntity);
	}

	@Override
	public boolean attemptTeleport(double p_213373_1_, double p_213373_3_, double p_213373_5_, boolean p_213373_7_) {
		return owner.attemptTeleport(p_213373_1_, p_213373_3_, p_213373_5_, p_213373_7_);
	}

	@Override
	public void awardKillScore(Entity p_191956_1_, int p_191956_2_, DamageSource p_191956_3_) {
		owner.awardKillScore(p_191956_1_, p_191956_2_, p_191956_3_);
	}

	@Override
	public void baseTick() {
		owner.baseTick();
	}

	@Override
	protected void blockUsingShield(LivingEntity entityIn) {
		super.blockUsingShield(entityIn);
	}

	@Override
	protected int calculateFallDamage(float e, float f) {
		return ModReflect.run(LivingEntity.class, int.class, "calculateFallDamage", "func_225508_e_", owner,
				new Class[] { float.class, float.class }, new Object[] { e, f });
	}

	@Override
	public boolean canAttack(EntityType<?> typeIn) {
		return owner.canAttack(typeIn);
	}

	@Override
	public boolean canAttack(LivingEntity livingentityIn, EntityPredicate predicateIn) {
		return owner.canAttack(livingentityIn, predicateIn);
	}

	@Override
	public boolean canAttack(LivingEntity target) {
		return owner.canAttack(target);
	}

	@Override
	public boolean canAttackPlayer(PlayerEntity other) {
		return super.canAttackPlayer(other);
	}

	@Override
	public boolean canBeAttackedWithItem() {
		return owner.canBeAttackedWithItem();
	}

	@Override
	public boolean canBeCollidedWith() {
		return owner.canBeCollidedWith();
	}

	@Override
	public boolean canBeHitWithPotion() {
		return owner.canBeHitWithPotion();
	}

	@Override
	public boolean canBePushed() {
		return owner.canBePushed();
	}

	@Override
	protected boolean canBeRidden(Entity entityIn) {
		return ModReflect.run(Entity.class, boolean.class, "canBeRidden", "func_184228_n", owner,
				new Class[] { Entity.class }, new Object[] { entityIn });
	}

	@Override
	public boolean canBeRiddenInWater() {
		return owner.canBeRiddenInWater();
	}

	@Override
	public boolean canBeRiddenInWater(Entity rider) {
		return owner.canBeRiddenInWater(rider);
	}

	@Override
	public boolean canBreatheUnderwater() {
		return owner.canBreatheUnderwater();
	}

	@Override
	protected boolean canDropLoot() {
		return ModReflect.run(LivingEntity.class, boolean.class, "canDropLoot", "func_146066_aG", owner);
	}

	@Override
	public boolean canEat(boolean ignoreHunger) {
		return super.canEat(ignoreHunger);
	}

	@Override
	public boolean canEntityBeSeen(Entity entityIn) {
		return owner.canEntityBeSeen(entityIn);
	}

	@Override
	public boolean canExplosionDestroyBlock(Explosion explosionIn, IBlockReader worldIn, BlockPos pos,
			BlockState blockStateIn, float p_174816_5_) {
		return owner.canExplosionDestroyBlock(explosionIn, worldIn, pos, blockStateIn, p_174816_5_);
	}

	@Override
	protected boolean canFitPassenger(Entity passenger) {
		return ModReflect.run(Entity.class, boolean.class, "applyPotionDamageCalculations", "func_184219_q", owner,
				new Class[] { Entity.class }, new Object[] { passenger });
	}

	@Override
	public boolean canHarvestBlock(BlockState state) {
		return super.canHarvestBlock(state);
	}

	@Override
	public boolean canPassengerSteer() {
		return owner.canPassengerSteer();
	}

	@Override
	public boolean canPickUpItem(ItemStack itemstackIn) {
		return owner.canPickUpItem(itemstackIn);
	}

	@Override
	public boolean canPlayerEdit(BlockPos pos, Direction facing, ItemStack stack) {
		return super.canPlayerEdit(pos, facing, stack);
	}

	@Override
	public boolean canRenderOnFire() {
		return owner.canRenderOnFire();
	}

	@Override
	public boolean canRiderInteract() {
		return owner.canRiderInteract();
	}

	@Override
	public boolean canSwim() {
		return owner.canSwim();
	}

	@Override
	public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
		return owner.canTrample(state, pos, fallDistance);
	}

	@Override
	protected boolean canTriggerWalking() {
		return super.canTriggerWalking();
	}

	@Override
	public boolean canUpdate() {
		return owner.canUpdate();
	}

	@Override
	public boolean canUseCommandBlock() {
		return super.canUseCommandBlock();
	}

	@Override
	public void canUpdate(boolean value) {
		owner.canUpdate(value);
	}

	@Override
	public Collection<ItemEntity> captureDrops() {
		return owner.captureDrops();
	}

	@Override
	public Entity changeDimension(DimensionType destination) {
		return owner.changeDimension(destination);
	}

	@Override
	public Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
		return owner.captureDrops(value);
	}

	@Override
	public Entity changeDimension(DimensionType destination, ITeleporter teleporter) {
		return owner.changeDimension(destination, teleporter);
	}

	@Override
	public void checkDespawn() {
		owner.checkDespawn();
	}

	@Override
	public boolean clearActivePotions() {
		return owner.clearActivePotions();
	}

	@Override
	protected void collideWithEntity(Entity entityIn) {

		ModReflect.run(LivingEntity.class, Void.class, "collideWithEntity", "func_82167_n", owner,
				new Class[] { Entity.class }, new Object[] { entityIn });
	}

	@Override
	public void clearBedPosition() {
		owner.clearBedPosition();
	}

	@Override
	protected void collideWithNearbyEntities() {

		ModReflect.run(LivingEntity.class, Void.class, "collideWithNearbyEntities", "func_85033_bc", owner);
	}

	@Override
	public void closeScreen() {
		super.closeScreen();
	}

	@Override
	protected void constructKnockBackVector(LivingEntity entityIn) {

		ModReflect.run(LivingEntity.class, Void.class, "constructKnockBackVector", "func_213371_e", owner,
				new Class[] { LivingEntity.class }, new Object[] { entityIn });
	}

	@Override
	public void copyDataFromOld(Entity entityIn) {
		owner.copyDataFromOld(entityIn);
	}

	@Override
	public void copyLocationAndAnglesFrom(Entity entityIn) {
		owner.copyLocationAndAnglesFrom(entityIn);
	}

	@Override
	protected Brain<?> createBrain(Dynamic<?> dynamicIn) {
		return super.createBrain(dynamicIn);
	}

	@Override
	protected CooldownTracker createCooldownTracker() {
		return super.createCooldownTracker();
	}

	@Override
	protected void createRunningParticles() {

		ModReflect.run(Entity.class, Void.class, "createRunningParticles", "func_174808_Z", owner);
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return super.createSpawnPacket();
	}

	@Override
	protected void createWitherRose(LivingEntity e) {

		ModReflect.run(LivingEntity.class, Void.class, "createWitherRose", "func_226298_f_", owner,
				new Class[] { LivingEntity.class }, new Object[] { e });
	}

	@Override
	public boolean curePotionEffects(ItemStack curativeItem) {
		return owner.curePotionEffects(curativeItem);
	}

	@Override
	protected void damageArmor(float damage) {
		super.damageArmor(damage);
	}

	@Override
	protected void damageEntity(DamageSource damageSrc, float damageAmount) {
		super.damageEntity(damageSrc, damageAmount);
	}

	@Override
	protected void damageShield(float damage) {
		super.damageShield(damage);
	}

	@Override
	protected void dealFireDamage(int amount) {

		ModReflect.run(Entity.class, Void.class, "dealFireDamage", "func_70081_e", owner, new Class[] { int.class },
				new Object[] { amount });
	}

	@Override
	protected int decreaseAirSupply(int air) {
		return ModReflect.run(Entity.class, int.class, "decreaseAirSupply", "func_70682_h", owner,
				new Class[] { int.class }, new Object[] { air });
	}

}