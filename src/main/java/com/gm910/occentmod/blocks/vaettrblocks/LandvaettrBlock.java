package com.gm910.occentmod.blocks.vaettrblocks;

import java.lang.reflect.Field;
import java.util.List;

import com.gm910.occentmod.blocks.AreaVaettrTileEntity;
import com.gm910.occentmod.blocks.ModBlock;
import com.gm910.occentmod.init.BlockInit;
import com.gm910.occentmod.init.TileInit;
import com.gm910.occentmod.vaettr.Vaettr.VaettrType;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;

public class LandvaettrBlock extends ModBlock {

	public LandvaettrBlock() {
		super(Block.Properties.create(Material.WOOD, MaterialColor.GOLD).lightValue(15));
		this.addTileEntity(() -> TileInit.LANDVAETTR.get(), null);
	}
	
	
	public static class Landvaettr extends AreaVaettrTileEntity {
		
		public Landvaettr() {
			super(TileInit.LANDVAETTR.get(), BlockInit.LANDVAETTR.get(), VaettrType.landvaettr, 50, 16*2, 16*2, 
					(world2, thi, posm) -> {
						Biome thisBiome = world2.getBiome(thi.getPos());
						List<Biome.Category> btypes = Lists.newArrayList(Biome.Category.PLAINS, Biome.Category.FOREST, Biome.Category.DESERT, Biome.Category.EXTREME_HILLS, Biome.Category.MESA, Biome.Category.MUSHROOM, Biome.Category.SAVANNA, Biome.Category.TAIGA, Biome.Category.BEACH);
						
						if (!btypes.contains(thisBiome.getCategory())) {
							return false;
						}
						return world2.getBiome(posm).getRegistryName().equals(thisBiome.getRegistryName());
					});
		}
		

		@Override
		public void tickServer(ServerWorld world) {
			super.tickServer(world);
			
				
			if (!getAttackTargets().isEmpty() && world.rand.nextInt(99) < 10) {
				for (LivingEntity e : getAttackTargets()) {
					double xoff = world.rand.nextDouble() * 6 - 3;
					double zoff = world.rand.nextDouble() * 6 - 3;
					double xhit = e.getPosX() + xoff;
					double zhit = e.getPosZ() + zoff;
					double yhit = e.getPosY() + world.rand.nextInt(2) - 1;
					world.createExplosion(null, xhit, yhit, zhit, 1, Explosion.Mode.NONE);
				}
			}
			if (getLifetime() % 200 == 0) {
				for (BlockPos pos : positions) {
					for (int y = 255; y >= minimum; y--) {
						BlockPos posm = new BlockPos(pos.getX(), y, pos.getZ());
						if (world.getBlockState(posm).getMaterial() == Material.FIRE) {
							PotionEntity pe = new PotionEntity(world, pos.getX(), y+ 2, pos.getZ());
							ItemStack potion = new ItemStack(Items.SPLASH_POTION);
							PotionUtils.addPotionToItemStack(potion, Potions.WATER);
							pe.setItem(potion);
							pe.setVelocity(0, -10, 0);
							world.addEntity(pe);
						}
						
						List<Entity> ens = world.getEntitiesWithinAABBExcludingEntity(null, (new AxisAlignedBB(posm)).grow(5));
						for (Entity e : ens) {
							if (e instanceof MobEntity) {
								MobEntity mb = (MobEntity)e;
								if ((!(e instanceof CreatureEntity) || (e instanceof MonsterEntity)) && !this.getAttackTargetsRaw().contains(mb.getUniqueID())) {
									this.getAttackTargetsRaw().add(mb.getUniqueID());
								}
							}
						}
					}
				}
			}
				
		}
		
		public void tickClient(ClientWorld world) {
			super.tickClient(world);
			for (BlockPos pos : positions) {
		    		int gy = 256;
		    		for (; gy >= minimum; gy--) {
		    			Material mat = world.getBlockState(new BlockPos(pos.getX(), gy - 1, pos.getZ())).getMaterial();
		    			if (mat == Material.CLAY || mat == Material.SNOW_BLOCK || mat == Material.SAND || mat == Material.ROCK || mat == Material.PACKED_ICE || mat == Material.EARTH || mat.isLiquid()) {
		    				break;
		    			}
		    		}
		    		
		    		if (world.rand.nextInt(100) < 5)
			    		world.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(), 
			    				gy + 1+ world.rand.nextDouble() - world.rand.nextDouble(), 
			    				pos.getZ() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(), 0.0, 0.0, 0.0);
		    }
		}
	}

}
