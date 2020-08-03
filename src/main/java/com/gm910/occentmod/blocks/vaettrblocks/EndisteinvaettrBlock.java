package com.gm910.occentmod.blocks.vaettrblocks;

import com.gm910.occentmod.blocks.AreaVaettrTileEntity;
import com.gm910.occentmod.blocks.ModBlock;
import com.gm910.occentmod.init.BlockInit;
import com.gm910.occentmod.init.TileInit;
import com.gm910.occentmod.vaettr.Vaettr.VaettrType;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class EndisteinvaettrBlock extends ModBlock {
	
	public EndisteinvaettrBlock() {
		super(Block.Properties.create(Material.WOOD, MaterialColor.GOLD));
		this.addTileEntity(() -> TileInit.ENDISTEINVAETTR.get(), null);
	}
	
	public static class Endisteinvaettr extends AreaVaettrTileEntity {
		
		
		public Endisteinvaettr() {
			super(TileInit.ENDISTEINVAETTR.get(), BlockInit.ENDISTEINVAETTR.get(), VaettrType.endisteinvaettr, 2, 16, 16, 
					(world2, thi, posm) -> {
						boolean flag = false;
						for (int y = 255; y >= 0; y--) {
							if (!world2.getBlockState(new BlockPos(posm.getX(), y, posm.getZ())).getMaterial().isReplaceable()) {
								flag = true;
								break;
							}
						}
						return flag && world2.getDimension().getType() == DimensionType.THE_END;// && world2.getBiome(posm) != Biomes.THE_END;
					});
		}
		
		@Override
		public void addPositionsToVaettr() {
			
			super.addPositionsToVaettr();
			/*for (int xd = -checkX; xd <= checkX; xd++) {
				for (int zd = -checkZ; zd <= checkZ; zd++) {
					
					BlockPos posm = new BlockPos(pos.getX() + xd, minimum, pos.getZ() + zd);
					if (checkPos.test((ServerWorld)world, this, posm)) {
						if (!positions.contains(posm)) {
							positions.add(posm);
						}
					}
				}
			}*/
		}
		
		@Override
		public void tickServer(ServerWorld world) {
			// TODO Auto-generated method stub
			super.tickServer(world);
			
			if (!getAttackTargets().isEmpty() && world.rand.nextInt(99) < 10) {
				for (LivingEntity e : getAttackTargets()) {
					
					for (BlockPos pos : positions) {
						if (pos.withinDistance(new BlockPos(e.getPosition().getX(), pos.getY(), e.getPosition().getZ()), 5) && world.rand.nextInt(20) < 2) {
							for (int y = 0; y <= e.getPosition().getY(); y++) {
								
								BlockPos del = new BlockPos(pos.getX(), y, pos.getZ());
								if (world.getBlockState(del).getMaterial() == Material.ROCK) { 
								
									world.addEntity(new FallingBlockEntity(world, del.getX(), y, del.getZ(), world.getBlockState(del)));
									
								}
								
							}
						}
					}
					
				}
			}
		}
		
		@Override
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
		    		world.addParticle(ParticleTypes.END_ROD, pos.getX() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(), 
		    				gy + 1+ world.rand.nextDouble() - world.rand.nextDouble(), 
		    				pos.getZ() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(), 0.0, 0.0, 0.0);
	    }
		}
	}
}
