package com.gm910.occentmod.entities.goals;

import java.util.EnumSet;
import java.util.UUID;

import com.gm910.occentmod.entities.LivingBlockEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;

public class BlockFollowGoal extends Goal {
   private static final EntityPredicate ENTITY_PREDICATE = (new EntityPredicate()).setDistance(10.0D).allowInvulnerable().allowFriendlyFire().setSkipAttackChecks().setLineOfSiteRequired();
   protected final LivingBlockEntity creature;
   private final double speed;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double pitch;
   private double yaw;
   private int delayTemptCounter;
   private boolean isRunning;
   public static final String BLOCK_FOLLOW_TAG = "LivingBlockFollow";

   public BlockFollowGoal(LivingBlockEntity creatureIn, double speedIn) {
      this.creature = creatureIn;
      this.speed = speedIn;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      if (!(creatureIn.getNavigator() instanceof GroundPathNavigator) && !(creatureIn.getNavigator() instanceof FlyingPathNavigator)) {
         throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
      }
   }
   
   public Entity getMaster() {
	   return creature.getOwner();
   }
   
   public boolean masterWantsFollow() {
	   return creature.getOwner() == null ? false : creature.getOwner().getPersistentData().getBoolean(BLOCK_FOLLOW_TAG);
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      
      return this.getMaster() != null && masterWantsFollow();
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {

      return this.shouldExecute() && creature.getOwner() != null && creature.getOwner().isAlive();
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.targetX = this.getMaster().getPosX();
      this.targetY = this.getMaster().getPosY();
      this.targetZ = this.getMaster().getPosZ();
      this.isRunning = true;
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.creature.getNavigator().clearPath();
      this.delayTemptCounter = 100;
      this.isRunning = false;
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      this.creature.getLookController().setLookPositionWithEntity(this.getMaster(), (float)(this.creature.getHorizontalFaceSpeed() + 20), (float)this.creature.getVerticalFaceSpeed());
      if (this.creature.getDistanceSq(this.getMaster()) < 6.25D) {
         this.creature.getNavigator().clearPath();
      } else {
         this.creature.getNavigator().tryMoveToEntityLiving(this.getMaster(), this.speed);
      }

   }

   /**
    * @see #isRunning
    */
   public boolean isRunning() {
      return this.isRunning;
   }
}