package com.gm910.occentmod.entities;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import com.gm910.occentmod.api.networking.messages.Networking;
import com.gm910.occentmod.api.networking.messages.types.TaskChangeBlock;
import com.gm910.occentmod.api.util.BlockInfo;
import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.init.AIInit;
import com.gm910.occentmod.init.EntityInit;
import com.gm910.occentmod.vaettr.Vaettr;
import com.gm910.occentmod.world.VaettrData;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.RegistryObject;

public class LivingBlockEntity extends FlyingEntity {

	private LivingEntity owner;
	private Vaettr ownerVaettr;

	private Vaettr vaettrAttackTarget;

	private BlockInfo block = null;

	private ServerPos originPos;

	private boolean replaces = true;

	private boolean returningHome = false;

	private World reader = new LivingBlockReader(this);

	public static void forceClinit() {
	}

	public static final RegistryObject<MemoryModuleType<UUID>> LIVING_OWNER_MEMORY = AIInit.MEMORY_MODULES
			.register("lbe_living_owner", () -> {
				return new MemoryModuleType<>(Optional.of((ops) -> {
					return UUID.fromString(ops.asString(UUID.randomUUID() + ""));
				}));
			});
	public static final RegistryObject<MemoryModuleType<UUID>> VAETTR_OWNER_MEMORY = AIInit.MEMORY_MODULES
			.register("lbe_vaettr_owner", () -> {
				return new MemoryModuleType<>(Optional.of((ops) -> {
					return UUID.fromString(ops.asString(UUID.randomUUID() + ""));
				}));
			});

	public static final RegistryObject<MemoryModuleType<BlockInfo>> BLOCK_MEMORY = AIInit.MEMORY_MODULES
			.register("lbe_block", () -> {
				return new MemoryModuleType<>(Optional.of(BlockInfo::fromDynamic));
			});

	public LivingBlockEntity(EntityType<LivingBlockEntity> type, World worldIn) {
		super(type, worldIn);
		this.moveController = new MoveHelperController(this);

	}

	@Override
	protected Brain<?> createBrain(Dynamic<?> dynamicIn) {
		// TODO Auto-generated method stub
		Brain<LivingBlockEntity> brain = new Brain<LivingBlockEntity>(
				ImmutableList.of(LIVING_OWNER_MEMORY.get(), VAETTR_OWNER_MEMORY.get(), BLOCK_MEMORY.get()),
				ImmutableList.of(), dynamicIn);

		return brain;
	}

	public LivingBlockEntity(World world, double x, double y, double z, @Nullable Vaettr owner,
			@Nullable LivingEntity owner1, @Nullable ServerPos origin, @Nullable BlockInfo block) {
		this(EntityInit.LIVING_BLOCK.get(), world);
		// System.out.println("Living block created with owner " + (owner == null ? null
		// : owner.getName()));
		this.setPosition(x, y, z);
		this.ownerVaettr = owner;
		this.owner = owner1;
		this.originPos = origin;
		this.block = block;
		if (origin != null) {
			this.prevPosX = origin.getX();
			this.prevPosY = origin.getY();
			this.prevPosZ = origin.getZ();
		}
	}

	public void setReturningHome(boolean returningHome) {
		this.returningHome = returningHome;
	}

	public boolean isReturningHome() {
		return returningHome;
	}

	public void setOriginPos(ServerPos originPos) {
		this.originPos = originPos;
	}

	public LivingBlockEntity setReplaces(boolean replaces) {
		this.replaces = replaces;
		return this;
	}

	/**
	 * Whether this entity replaces the block it spawned at
	 * 
	 * @return
	 */
	public boolean replaces() {
		return replaces;
	}

	public ServerPos getOriginPos() {
		return originPos;
	}

	@Override
	public void onAddedToWorld() {
		super.onAddedToWorld();

		if (originPos == null) {
			BlockPos homepos = this.getPosition();
			if (world.getBlockState(homepos).getRenderType() != BlockRenderType.MODEL) {
				homepos = homepos.down();
				if (world.getBlockState(homepos).getRenderType() != BlockRenderType.MODEL) {
					this.remove();
					System.out.println("LivingBlock dead because it was spawned in air");
					return;
				}
			}
			originPos = new ServerPos(homepos, this.dimension);

			this.setPosition(homepos.getX(), homepos.getY() + (double) ((1.0F - this.getHeight()) / 2.0F),
					homepos.getZ());
		}
		if (block == null) {

			this.setBlock(new BlockInfo(world, originPos));

			this.getAttributes().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH)
					.setBaseValue(block.getState().getBlockHardness(world, originPos));
			if (replaces) {
				this.world.setBlockState(originPos, Blocks.AIR.getDefaultState());
			}
		}
		this.setNoGravity(true);
	}

	@Override
	public boolean canBreatheUnderwater() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canRenderOnFire() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MovementController getMoveHelper() {
		// TODO Auto-generated method stub
		return super.getMoveHelper();
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		super.tick();

		if (!world.isRemote) {
			if (this.dimension != this.originPos.getDimension()) {
				this.remove();
			}

			if ((owner == null || !owner.isAlive()) && (ownerVaettr == null || ownerVaettr.isDead())) {
				// System.out.println("Living block owner dead on client? " + world.isRemote +
				// ": living: " + (owner == null ? null : owner.getClass().getSimpleName()) + "
				// vaet: " + (ownerVaettr == null ? ownerVaettr :
				// ownerVaettr.getClass().getSimpleName() + " " + ownerVaettr.getName()));
				// this.remove();
				returningHome = true;
			}
			if ((this.getAttackTarget() == null || !this.getAttackTarget().isAlive())
					&& (this.vaettrAttackTarget == null || this.vaettrAttackTarget.isDead())) {
				// System.out.println("Living block owner has no targets");
				returningHome = true;
			}

		}
	}

	/**
	 * @Override public void onRemovedFromWorld() { super.onRemovedFromWorld(); if
	 *           (!world.isRemote && this.block != null) { if
	 *           (originPos.getWorld(world.getServer()).getBlockState(originPos).getMaterial().isReplaceable())
	 *           { originPos.getWorld(world.getServer()).setBlockState(originPos,
	 *           this.block.getState()); if (block.getTile() != null) {
	 *           originPos.getWorld(world.getServer()).setTileEntity(originPos,
	 *           this.block.getTile()); } if (this.isBurning() &&
	 *           originPos.getWorld(world.getServer()).getBlockState(originPos.up()).getMaterial().isReplaceable())
	 *           { originPos.getWorld(world.getServer()).setBlockState(originPos,
	 *           Blocks.FIRE.getDefaultState()); } Networking.sendToAll(new
	 *           TaskChangeBlock(originPos.getWorld(world.getServer()).getBlockState(originPos),
	 *           originPos,
	 *           originPos.getWorld(world.getServer()).getTileEntity(originPos),
	 *           1)); } } }
	 **/

	@Override
	protected void dropLoot(DamageSource damageSourceIn, boolean p_213354_2_) {
		super.dropLoot(damageSourceIn, p_213354_2_);
		if (this.block != null) {
			this.block.getState().getBlock();
			Block.spawnDrops(this.block.getState(), world, this.getPosition(), this.block.getTile(),
					damageSourceIn.getTrueSource(),
					damageSourceIn.getTrueSource() instanceof LivingEntity
							? ((LivingEntity) damageSourceIn.getTrueSource()).getActiveItemStack()
							: ItemStack.EMPTY);
		}
	}

	@Override
	protected void dropExperience() {
		// TODO Auto-generated method stub
		super.dropExperience();
		if (block != null) {
			this.block.getState().getBlock().dropXpOnBlockBreak(this.reader, this.originPos,
					this.block.getState().getExpDrop(reader, originPos, 0, 0));
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		if (this.getBlock() == null)
			return null;
		BlockState b = this.getBlock().getState();
		boolean blocksMovement = ModReflect.getField(Block.class, boolean.class, "blocksMovement", "field_196274_w",
				b.getBlock());

		return blocksMovement ? getBoundingBox() : null;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new ReturnHomeGoal());
		// this.goalSelector.addGoal(1, new BlockFollowGoal(this, 2f));
		this.goalSelector.addGoal(2, new ChargeAttackGoal());
		this.targetSelector.addGoal(0, new TargetForVaettr(this));

	}

	public void setBlock(BlockInfo block) {
		this.block = block;
	}

	public void setBlock(World world, BlockPos pos) {
		this.block = new BlockInfo(world, pos);
	}

	public BlockInfo getBlock() {
		return block;
	}

	public LivingBlockEntity setOwner(LivingEntity player) {
		this.owner = player;
		return this;
	}

	@Nullable
	public Entity getOwner() {
		return owner;
	}

	@Nullable
	public Vaettr getOwnerVaettr() {
		return ownerVaettr;
	}

	public LivingBlockEntity setOwnerVaettr(Vaettr ownerVaettr) {
		this.ownerVaettr = ownerVaettr;
		return this;
	}

	@Override
	protected void registerAttributes() {
		// TODO Auto-generated method stub
		super.registerAttributes();
		this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);

	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		if (this.getOwner() == null) {
			compound.putString("OwnerUUID", "");
		} else {
			compound.putString("OwnerUUID", this.getOwner().getUniqueID().toString());
		}

		if (this.getOwnerVaettr() == null || this.getOwnerVaettr().isDead()) {
			compound.putString("VaettrUUID", "");
		} else {
			if (!world.isRemote) {
				compound.putString("VaettrUUID", this.getOwnerVaettr().getUniqueId().toString());
			} else {
				compound.put("VaettrData", this.getOwnerVaettr().serializeNBT());
			}
		}
		compound.put("Block", this.block.serializeNBT());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		String owns = compound.getString("OwnerUUID");
		String vaes = compound.getString("VaettrUUID");
		UUID own = null;
		UUID vae = null;
		try {
			own = UUID.fromString(owns);
		} catch (IllegalArgumentException e) {
		}
		try {
			vae = UUID.fromString(vaes);
		} catch (IllegalArgumentException e) {
		}

		if (own != null && !world.isRemote) {
			this.owner = (LivingEntity) ServerPos.getEntityFromUUID(own, world.getServer());
		}

		if (vae != null && !world.isRemote) {
			this.ownerVaettr = VaettrData.get(world).getVaettr(vae);
		}
		if (this.world.isRemote) {
			this.ownerVaettr = new Vaettr(compound.getCompound("VaettrData"));
		}

		this.block = new BlockInfo(compound.getCompound("Block"));

	}

	static class MoveHelperController extends MovementController {
		private final LivingBlockEntity parentEntity;
		private int courseChangeCooldown;

		public MoveHelperController(LivingBlockEntity ghast) {
			super(ghast);
			this.parentEntity = ghast;
		}

		public void tick() {
			if (this.action == MovementController.Action.MOVE_TO) {
				if (this.courseChangeCooldown-- <= 0) {
					this.courseChangeCooldown += this.parentEntity.getRNG().nextInt(5) + 2;
					Vec3d vec3d = new Vec3d(this.posX - this.parentEntity.getPosX(),
							this.posY - this.parentEntity.getPosY(), this.posZ - this.parentEntity.getPosZ());
					double d0 = vec3d.length();
					vec3d = vec3d.normalize();
					if (this.func_220673_a(vec3d, MathHelper.ceil(d0))) {
						this.parentEntity.setMotion(this.parentEntity.getMotion().add(vec3d.scale(0.1D)));
					} else {
						this.action = MovementController.Action.WAIT;
					}
				}

			}
		}

		private boolean func_220673_a(Vec3d p_220673_1_, int p_220673_2_) {
			AxisAlignedBB axisalignedbb = this.parentEntity.getBoundingBox();

			for (int i = 1; i < p_220673_2_; ++i) {
				axisalignedbb = axisalignedbb.offset(p_220673_1_);
				if (!this.parentEntity.world.hasNoCollisions(this.parentEntity, axisalignedbb)) {
					return false;
				}
			}

			return true;
		}
	}

	public class ChargeAttackGoal extends Goal {
		public ChargeAttackGoal() {
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean shouldExecute() {
			if ((LivingBlockEntity.this.getAttackTarget() != null && getAttackTarget().isAlive()
					|| isVaettrTargetValid() && !vaettrAttackTarget.isDead())
					&& !LivingBlockEntity.this.getMoveHelper().isUpdating()
					&& LivingBlockEntity.this.rand.nextInt(7) == 0) {
				return LivingBlockEntity.this.getDistanceSq(LivingBlockEntity.this.getAttackTarget()) > 1.0D;
			} else {
				return false;
			}
		}

		public boolean isVaettrTargetValid() {
			return LivingBlockEntity.this.vaettrAttackTarget != null
					&& LivingBlockEntity.this.vaettrAttackTarget.getPos() != Vec3d.ZERO;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return LivingBlockEntity.this.getMoveHelper().isUpdating()
					/*&& LivingBlockEntity.this.isCharging()*/ && (LivingBlockEntity.this.getAttackTarget() != null
							&& LivingBlockEntity.this.getAttackTarget().isAlive() || isVaettrTargetValid());
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			LivingEntity livingentity = LivingBlockEntity.this.getAttackTarget();
			Vaettr vaet = LivingBlockEntity.this.vaettrAttackTarget;
			Vec3d vec3d = null;
			if (livingentity != null) {
				vec3d = livingentity.getEyePosition(1.0F);
			} else {
				vec3d = vaet.getPos();
			}
			LivingBlockEntity.this.moveController.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
			// LivingBlockEntity.this.setCharging(true);
			LivingBlockEntity.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by
		 * another one
		 */
		public void resetTask() {
			// LivingBlockEntity.this.setCharging(false);
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			LivingEntity livingentity = LivingBlockEntity.this.getAttackTarget();
			Vaettr vaet = LivingBlockEntity.this.vaettrAttackTarget;
			Vec3d vec3d = null;
			if (livingentity != null) {
				vec3d = livingentity.getEyePosition(1.0F);
			} else {
				vec3d = vaet.getPos();
			}

			if (livingentity != null
					&& LivingBlockEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
				LivingBlockEntity.this.attackEntityAsMob(livingentity);
				// LivingBlockEntity.this.setCharging(false);
			} else if (vaet != null && vaet.hasEntity()
					&& LivingBlockEntity.this.getBoundingBox().intersects(vaet.getSelfEntity().getBoundingBox())) {
				LivingBlockEntity.this.attackEntityAsMob(vaet.getSelfEntity());
			} else if (vaet != null && vaet.hasTileEntity()
					&& LivingBlockEntity.this.getBoundingBox().intersects(new AxisAlignedBB(vaet.getBlockPos()))) {
				vaet.setHealth(Math.max(0, vaet.getHealth() - (float) LivingBlockEntity.this
						.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));
			} else {
				double d0 = LivingBlockEntity.this.getDistanceSq(livingentity);
				if (d0 < 9.0D) {
					LivingBlockEntity.this.moveController.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
				}
			}

		}
	}

	class TargetForVaettr extends TargetGoal {
		// private final EntityPredicate field_220803_b = (new
		// EntityPredicate()).setLineOfSiteRequired().setUseInvisibilityCheck();

		public TargetForVaettr(LivingBlockEntity creature) {
			super(creature, false);
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean shouldExecute() {
			// return LivingBlockEntity.this.getOwner() != null &&
			// LivingBlockEntity.this.getOwner() != null &&
			// this.isSuitableTarget(LivingBlockEntity.this.getOwner().getAttackTarget(),
			// this.field_220803_b);
			return LivingBlockEntity.this.getOwnerVaettr() != null
					&& (!LivingBlockEntity.this.getOwnerVaettr().getLivingTargets().isEmpty()
							|| !LivingBlockEntity.this.getOwnerVaettr().getVaettrTargets().isEmpty());
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			List<LivingEntity> targs = LivingBlockEntity.this.ownerVaettr.getLivingTargets();
			List<Vaettr> targsV = LivingBlockEntity.this.ownerVaettr.getVaettrTargets();
			boolean flag = rand.nextBoolean();
			boolean living = false;
			boolean vaettr = false;
			if (targs.isEmpty()) {
				if (targsV.isEmpty()) {
					return;
				} else {
					vaettr = true;
				}
			} else {
				if (targsV.isEmpty()) {
					living = true;
				} else {
					if (flag) {
						living = true;
					} else {
						vaettr = true;
					}
				}
			}
			if (living) {

				int rand1 = LivingBlockEntity.this.rand.nextInt(targs.size());
				LivingBlockEntity.this.setAttackTarget(targs.get(rand1));
			}
			if (vaettr) {
				int rand2 = LivingBlockEntity.this.rand.nextInt(targsV.size());
				LivingBlockEntity.this.vaettrAttackTarget = targsV.get(rand2);
			}
			super.startExecuting();
		}
	}

	public class ReturnHomeGoal extends Goal {

		public ReturnHomeGoal() {
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.TARGET));
		}

		@Override
		public boolean shouldExecute() {
			// TODO Auto-generated method stub
			return returningHome && block != null && world.getBlockState(originPos).getMaterial().isReplaceable();
		}

		@Override
		public void startExecuting() {

			getMoveHelper().setMoveTo(originPos.getX(), originPos.getY(), originPos.getZ(), 2);
		}

		@Override
		public void tick() {
			// getMoveHelper().setMoveTo(originPos.getX(), originPos.getY(),
			// originPos.getZ(), 20);
			move(MoverType.SELF, new Vec3d(originPos));
			if (getPosition().distanceSq(originPos.getPos()) < 4) {
				returningHome = false;
				block.place(world, originPos);
				if (isBurning() && originPos.getWorld(world.getServer()).getBlockState(originPos.up()).getMaterial()
						.isReplaceable()) {
					originPos.getWorld(world.getServer()).setBlockState(originPos, Blocks.FIRE.getDefaultState());
				}
				Networking.sendToAll(new TaskChangeBlock(originPos.getWorld(world.getServer()).getBlockState(originPos),
						originPos, originPos.getWorld(world.getServer()).getTileEntity(originPos), 1));

				remove();
			}
		}

	}

	public class LivingBlockReader extends World {

		protected LivingBlockReader(LivingBlockEntity e) {
			super(e.world.getWorldInfo(), e.world.dimension.getType(), (m, f) -> e.world.getChunkProvider(),
					e.world.getProfiler(), e.world.isRemote());
		}

		@Override
		public WorldLightManager getLightManager() {
			// TODO Auto-generated method stub
			return LivingBlockEntity.this.world.getLightManager();
		}

		@Override
		public TileEntity getTileEntity(BlockPos pos) {
			// TODO Auto-generated method stub
			return pos.equals(LivingBlockEntity.this.originPos) || pos.equals(LivingBlockEntity.this.getPosition())
					? LivingBlockEntity.this.block.getTile()
					: null;
		}

		@Override
		public BlockState getBlockState(BlockPos pos) {
			// TODO Auto-generated method stub
			return pos.equals(LivingBlockEntity.this.originPos) || pos.equals(LivingBlockEntity.this.getPosition())
					? LivingBlockEntity.this.block.getState()
					: Blocks.AIR.getDefaultState();
		}

		@Override
		public IFluidState getFluidState(BlockPos pos) {
			// TODO Auto-generated method stub
			return pos.equals(LivingBlockEntity.this.originPos) || pos.equals(LivingBlockEntity.this.getPosition())
					? LivingBlockEntity.this.block.getState().getFluidState()
					: Fluids.EMPTY.getDefaultState();
		}

		@Override
		public WorldBorder getWorldBorder() {
			// TODO Auto-generated method stub
			return new WorldBorder();
		}

		@Override
		public IChunk getChunk(int x, int z, ChunkStatus requiredStatus, boolean nonnull) {
			// TODO Auto-generated method stub
			return nonnull ? null : world.getChunk(x, z, requiredStatus, nonnull);
		}

		@Override
		public boolean chunkExists(int chunkX, int chunkZ) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int getHeight(Type heightmapType, int x, int z) {
			// TODO Auto-generated method stub
			return world.getHeight();
		}

		@Override
		public int getSkylightSubtracted() {
			// TODO Auto-generated method stub
			return world.getSkylightSubtracted();
		}

		@Override
		public BiomeManager getBiomeManager() {
			// TODO Auto-generated method stub
			return world.getBiomeManager();
		}

		@Override
		public Biome getNoiseBiomeRaw(int x, int y, int z) {
			// TODO Auto-generated method stub
			return world.getNoiseBiomeRaw(x, y, z);
		}

		@Override
		public boolean isRemote() {
			// TODO Auto-generated method stub
			return world.isRemote;
		}

		@Override
		public int getSeaLevel() {
			// TODO Auto-generated method stub
			return world.getSeaLevel();
		}

		@Override
		public Dimension getDimension() {
			// TODO Auto-generated method stub
			return world.dimension;
		}

		@Override
		public ITickList<Block> getPendingBlockTicks() {
			// TODO Auto-generated method stub
			return world.getPendingBlockTicks();
		}

		@Override
		public ITickList<Fluid> getPendingFluidTicks() {
			// TODO Auto-generated method stub
			return world.getPendingFluidTicks();
		}

		@Override
		public void playEvent(PlayerEntity player, int type, BlockPos pos, int data) {
			if (pos.equals(originPos) || pos.equals(getPosition())) {
				world.playEvent(type, pos, data);
			}
		}

		@Override
		public List<? extends PlayerEntity> getPlayers() {
			// TODO Auto-generated method stub
			return world.getPlayers();
		}

		@Override
		public void notifyBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
			if (pos.equals(originPos) || pos.equals(getPosition())) {
				world.notifyBlockUpdate(pos, oldState, newState, flags);
			}
		}

		@Override
		public void playSound(PlayerEntity player, double x, double y, double z, SoundEvent soundIn,
				SoundCategory category, float volume, float pitch) {
			world.playSound(player, x, y, z, soundIn, category, volume, pitch);
		}

		@Override
		public void playMovingSound(PlayerEntity playerIn, Entity entityIn, SoundEvent eventIn,
				SoundCategory categoryIn, float volume, float pitch) {
			world.playMovingSound(playerIn, entityIn, eventIn, categoryIn, volume, pitch);
		}

		@Override
		public Entity getEntityByID(int id) {
			// TODO Auto-generated method stub
			return world.getEntityByID(id);
		}

		@Override
		public MapData getMapData(String mapName) {
			// TODO Auto-generated method stub
			return world.getMapData(mapName);
		}

		@Override
		public void registerMapData(MapData mapDataIn) {
			world.registerMapData(mapDataIn);
		}

		@Override
		public int getNextMapId() {
			// TODO Auto-generated method stub
			return world.getNextMapId();
		}

		@Override
		public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
			if (pos.equals(originPos) || pos.equals(getPosition())) {
				world.sendBlockBreakProgress(breakerId, pos, progress);
			}
		}

		@Override
		public Scoreboard getScoreboard() {
			return world.getScoreboard();
		}

		@Override
		public RecipeManager getRecipeManager() {
			// TODO Auto-generated method stub
			return world.getRecipeManager();
		}

		@Override
		public NetworkTagManager getTags() {
			// TODO Auto-generated method stub
			return world.getTags();
		}

	}
}
