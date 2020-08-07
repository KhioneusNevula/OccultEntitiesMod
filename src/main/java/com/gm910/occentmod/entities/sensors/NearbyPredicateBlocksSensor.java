package com.gm910.occentmod.entities.sensors;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class NearbyPredicateBlocksSensor extends Sensor<LivingEntity> {
	
	private BiPredicate<ServerWorld, BlockPos> pred;
	private MemoryModuleType<List<GlobalPos>> memory;
	
	public NearbyPredicateBlocksSensor(BiPredicate<ServerWorld, BlockPos> blockPred, MemoryModuleType<List<GlobalPos>> mem) {
		this.pred = blockPred;
		this.memory = mem;
	}
	
	public boolean test(ServerWorld world, BlockPos pos) {
		return pred.test(world, pos);
	}
	
   protected void update(ServerWorld worldIn, LivingEntity entityIn) {
      DimensionType dimensiontype = worldIn.getDimension().getType();
      BlockPos blockpos = new BlockPos(entityIn);
      List<GlobalPos> list = Lists.newArrayList();

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            for(int k = -1; k <= 1; ++k) {
               BlockPos blockpos1 = blockpos.add(i, j, k);
               if (test(worldIn, blockpos1)) {
                  list.add(GlobalPos.of(dimensiontype, blockpos1));
               }
            }
         }
      }

      Brain<?> brain = entityIn.getBrain();
      if (!list.isEmpty()) {
         brain.setMemory(memory, list);
      } else {
         brain.removeMemory(memory);
      }

   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(memory);
   }
}