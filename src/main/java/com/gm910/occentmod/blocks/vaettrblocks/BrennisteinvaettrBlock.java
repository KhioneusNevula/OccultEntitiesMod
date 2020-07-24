package com.gm910.occentmod.blocks.vaettrblocks;

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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class BrennisteinvaettrBlock extends ModBlock {
	
	public BrennisteinvaettrBlock() {
		super(Block.Properties.create(Material.WOOD, MaterialColor.GOLD));
		this.addTileEntity(() -> TileInit.BRENNISTEINVAETTR.get(), null);
	}
	
	public static class Brennisteinvaettr extends AreaVaettrTileEntity {
		
		
		public Brennisteinvaettr() {
			super(TileInit.BRENNISTEINVAETTR.get(), BlockInit.BRENNISTEINVAETTR.get(), VaettrType.brennisteinvaettr, 20, 16, 16, 
					(world2, thi, posm) -> {
						return world2.getDimension().getType() == DimensionType.THE_NETHER;// && posm.withinDistance(thi.getPos(), 20);
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
							for (int y = e.getPosition().getY(); y <= 255; y++) {
								
								BlockPos del = new BlockPos(pos.getX(), y, pos.getZ());
								if (world.getBlockState(del).getBlock() == Blocks.NETHERRACK ||
										world.getBlockState(del).getBlock() == Blocks.SOUL_SAND ||
										world.getBlockState(del).getBlock() == Blocks.NETHER_WART_BLOCK ||
										world.getBlockState(del).getMaterial().isLiquid()) { 
								
									this.world.addEntity(new FallingBlockEntity(world, pos.getX(), y, pos.getZ(), world.getBlockState(del)));
									break;
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
			
		}
	}
}
