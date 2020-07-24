package com.gm910.occentmod.blocks.vaettrblocks;

import java.lang.reflect.Field;
import java.util.List;

import com.gm910.occentmod.blocks.AreaVaettrTileEntity;
import com.gm910.occentmod.blocks.ModBlock;
import com.gm910.occentmod.init.BlockInit;
import com.gm910.occentmod.init.TileInit;
import com.gm910.occentmod.vaettr.Vaettr.VaettrType;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;

public class StormvaettrBlock extends ModBlock {

	public StormvaettrBlock() {
		super(Block.Properties.create(Material.WOOD, MaterialColor.BLACK).lightValue(15));
		this.addTileEntity(() -> TileInit.STORMVAETTR.get(), null);
	}
	
	
	public static class Stormvaettr extends AreaVaettrTileEntity {
		
		public Stormvaettr() {
			super(TileInit.STORMVAETTR.get(), BlockInit.STORMVAETTR.get(), VaettrType.stormvaettr, 50, 16*2, 16*2, 
					(world2, thi, posm) -> {
						Biome thisBiome = world2.getBiome(thi.getPos());
						return world2.getBiome(posm).getRegistryName().equals(thisBiome.getRegistryName());
					});
			
		}
		

		@Override
		public void tickServer(ServerWorld world) {
			super.tickServer(world);
				
			world.getWorldInfo().setThunderTime(0);
			world.getWorldInfo().setThundering(true);
				
			if (!getAttackTargets().isEmpty() && world.rand.nextInt(99) < 30) {
				for (LivingEntity e : getAttackTargets()) {
					double xoff = world.rand.nextDouble() * 6 - 3;
					double zoff = world.rand.nextDouble() * 6 - 3;
					double xhit = e.getPosX() + xoff;
					double zhit = e.getPosZ() + zoff;
					double yhit = e.getPosY();
					int prob = world.rand.nextInt(50);
					if (prob < 5) {
						world.addEntity(new DragonFireballEntity(world, xhit, yhit + 50, zhit, 0, -5, 0));
					} else if (prob < 25) {
						PotionEntity pe = new PotionEntity(world, pos.getX(), yhit+50, pos.getZ());
						ItemStack potion = new ItemStack(Items.SPLASH_POTION);
						Field[] pots = Potions.class.getDeclaredFields();
						Potion toCheck = Potions.WATER;
						boolean flag = false;
						int count = pots.length;
						while (!flag) {
							if (count <= 0) break;
							Field po = pots[world.rand.nextInt(pots.length)];
							try {
								toCheck = (Potion) po.get(null);
							} catch (IllegalArgumentException | IllegalAccessException ex) { 
								
							}
							
							for (EffectInstance effect : toCheck.getEffects()) {
								if (!effect.getPotion().isBeneficial()) {
									flag = true;
									break;
								}
							}
							
							count--;
						}
						
						if (!flag) toCheck = Potions.WATER;
						
						PotionUtils.addPotionToItemStack(potion, toCheck);
						pe.setItem(potion);
						pe.setVelocity(0, -10, 0);
						world.addEntity(pe);
					} else {

						world.addLightningBolt(new LightningBoltEntity(world, xhit, yhit, zhit, true));
					}
					
				}
			}
			if (getLifetime() % 200 == 0) {
				for (BlockPos pos : positions) {
					for (int y = 255; y >= minimum; y--) {
						BlockPos posm = new BlockPos(pos.getX(), y, pos.getZ());
						
						if (world.getBlockState(posm).getMaterial() == Material.FIRE || world.getBlockState(posm).getBlock() == Blocks.CAMPFIRE) {
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
								} else {
									if (mb.isBurning()) {
										PotionEntity pe = new PotionEntity(world, mb.getPosX(), mb.getPosY()+ 2, mb.getPosZ());
										ItemStack potion = new ItemStack(Items.SPLASH_POTION);
										PotionUtils.addPotionToItemStack(potion, Potions.WATER);
										pe.setItem(potion);
										pe.setVelocity(0, -10, 0);
										world.addEntity(pe);
									}
								}
							} else if (e instanceof AreaEffectCloudEntity) {
								((AreaEffectCloudEntity)e).remove();
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
		    		
		    		
		    		if (world.rand.nextInt(100) < 20)
			    		world.addParticle(ParticleTypes.RAIN, pos.getX() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(), 
			    				gy + 1+ world.rand.nextDouble() - world.rand.nextDouble(), 
			    				pos.getZ() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(), 0, 0, 0);
		    		
		    }
		}
		
		
	}

}
