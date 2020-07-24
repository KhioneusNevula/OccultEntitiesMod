package com.gm910.occentmod.entities;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.gm910.occentmod.api.util.BlockInfo;
import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.entities.goals.BlockFollowGoal;
import com.gm910.occentmod.vaettr.Vaettr;
import com.gm910.occentmod.world.VaettrData;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class LivingBlockEntity extends MobEntity {
	
	private LivingEntity owner;
	private Vaettr ownerVaettr;
	
	private Vaettr vaettrAttackTarget;
	
	private BlockInfo block = null;
	
	private ServerPos originPos;
	
	private boolean replaces = true;
	
	public LivingBlockEntity(EntityType<LivingBlockEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	public void setOriginPos(ServerPos originPos) {
		this.originPos = originPos;
	}
	
	public void setReplaces(boolean replaces) {
		this.replaces = replaces;
	}
	
	/**
	 * Whether this entity replaces the block it spawned at
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
		BlockPos homepos = this.getPosition();
		originPos = new ServerPos(homepos, this.dimension);
		if (world.getBlockState(homepos).getMaterial() == Material.AIR) {
			this.dead = true;
			return;
		}
		if (block == null) {
			this.getAttributes().registerAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(block.getState().getBlockHardness(world, homepos));
			
			this.setBlock(new BlockInfo(world, homepos));
		}
		this.noClip = true;
		
	}
	
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		super.tick();
		if (this.dimension != this.originPos.getDimension()) {
			this.remove();
		}
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		// TODO Auto-generated method stub
		return this.getBoundingBox();
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new BlockFollowGoal(this, 2f));
		this.goalSelector.addGoal(1, new ChargeAttackGoal());
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

	   public void setOwner(LivingEntity player) {
	      this.owner = player;
	   }

	   @Nullable
	   public Entity getOwner() {
	      return owner;
	   }
	   
	   public Vaettr getOwnerVaettr() {
		return ownerVaettr;
	}
	   
	   public void setOwnerVaettr(Vaettr ownerVaettr) {
		this.ownerVaettr = ownerVaettr;
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
	      
	      if (this.getOwnerVaettr() == null) {
		         compound.putString("VaettrUUID", "");
		      } else {
		         compound.putString("VaettrUUID", this.getOwnerVaettr().getUniqueId().toString());
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
	      } catch (IllegalArgumentException e) {}
	      try {
	    	  vae = UUID.fromString(vaes);
	      } catch (IllegalArgumentException e) {}
	      
	      if (own != null) {
	    	  this.owner = (LivingEntity) ServerPos.getEntityFromUUID(own, world.getServer());
	      }
	      
	      if (vae != null) {
	    	  this.ownerVaettr = VaettrData.get(world).getVaettr(vae);
	      }
	      
	      this.block = new BlockInfo(compound.getCompound("Block"));
	      
	   }
	
	public class ChargeAttackGoal extends Goal {
	      public ChargeAttackGoal() {
	         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
	      }

	      /**
	       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
	       * method as well.
	       */
	      public boolean shouldExecute() {
	         if ((LivingBlockEntity.this.getAttackTarget() != null || isVaettrTargetValid()) && !LivingBlockEntity.this.getMoveHelper().isUpdating() && LivingBlockEntity.this.rand.nextInt(7) == 0) {
	            return LivingBlockEntity.this.getDistanceSq(LivingBlockEntity.this.getAttackTarget()) > 4.0D;
	         } else {
	            return false;
	         }
	      }
	      
	      public boolean isVaettrTargetValid() {
	    	  return LivingBlockEntity.this.vaettrAttackTarget != null && LivingBlockEntity.this.vaettrAttackTarget.getPos() != Vec3d.ZERO;
	      }

	      /**
	       * Returns whether an in-progress EntityAIBase should continue executing
	       */
	      public boolean shouldContinueExecuting() {
	         return LivingBlockEntity.this.getMoveHelper().isUpdating() /*&& LivingBlockEntity.this.isCharging()*/ && (LivingBlockEntity.this.getAttackTarget() != null && LivingBlockEntity.this.getAttackTarget().isAlive() || isVaettrTargetValid()) ;
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
	         //LivingBlockEntity.this.setCharging(true);
	         LivingBlockEntity.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
	      }

	      /**
	       * Reset the task's internal state. Called when this task is interrupted by another one
	       */
	      public void resetTask() {
	         //LivingBlockEntity.this.setCharging(false);
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
	         
	         if (livingentity != null && LivingBlockEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
	            LivingBlockEntity.this.attackEntityAsMob(livingentity);
	            //LivingBlockEntity.this.setCharging(false);
	         } 
	         else if (vaet != null && vaet.hasEntity() && LivingBlockEntity.this.getBoundingBox().intersects(vaet.getSelfEntity().getBoundingBox())) {
	        	 LivingBlockEntity.this.attackEntityAsMob(vaet.getSelfEntity());
	         } else if (vaet != null && vaet.hasTileEntity() && LivingBlockEntity.this.getBoundingBox().intersects(new AxisAlignedBB(vaet.getBlockPos()))) {
	        	 vaet.setHealth(Math.max(0, vaet.getHealth() - (float) LivingBlockEntity.this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));
	         }
	         else {
	            double d0 = LivingBlockEntity.this.getDistanceSq(livingentity);
	            if (d0 < 9.0D) {
	               LivingBlockEntity.this.moveController.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
	            }
	         }

	      }
	   }
	
	class TargetForVaettr extends TargetGoal {
	     // private final EntityPredicate field_220803_b = (new EntityPredicate()).setLineOfSiteRequired().setUseInvisibilityCheck();

	      public TargetForVaettr(LivingBlockEntity creature) {
	         super(creature, false);
	      }

	      /**
	       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
	       * method as well.
	       */
	      public boolean shouldExecute() {
	         //return LivingBlockEntity.this.getOwner() != null && LivingBlockEntity.this.getOwner() != null && this.isSuitableTarget(LivingBlockEntity.this.getOwner().getAttackTarget(), this.field_220803_b);
	    	  return LivingBlockEntity.this.getOwnerVaettr() != null && (!LivingBlockEntity.this.getOwnerVaettr().getLivingTargets().isEmpty() || !LivingBlockEntity.this.getOwnerVaettr().getVaettrTargets().isEmpty());
	      }

	      /**
	       * Execute a one shot task or start executing a continuous task
	       */
	      public void startExecuting() {
	    	  List<LivingEntity> targs = LivingBlockEntity.this.ownerVaettr.getLivingTargets();
	    	  List<Vaettr> targsV = LivingBlockEntity.this.ownerVaettr.getVaettrTargets();
	    	 int rand1 = LivingBlockEntity.this.rand.nextInt(targs.size());
	    	 int rand2 = LivingBlockEntity.this.rand.nextInt(targsV.size());
	    	 boolean v = LivingBlockEntity.this.rand.nextBoolean();
	         if (!v) {
	        	 LivingBlockEntity.this.setAttackTarget(targs.get(rand1));
	         } else {
	        	 LivingBlockEntity.this.vaettrAttackTarget = targsV.get(rand2);
	         }
	         super.startExecuting();
	      }
	   }
	
}
