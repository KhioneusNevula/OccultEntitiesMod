package com.gm910.occentmod.blocks;

import java.util.ArrayList;
import java.util.List;

import com.gm910.occentmod.api.networking.messages.Networking;
import com.gm910.occentmod.api.networking.messages.types.TaskChangeBlock;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.api.util.Translate;
import com.gm910.occentmod.vaettr.Vaettr.VaettrType;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class AreaVaettrTileEntity extends VaettrTileEntity {

		protected int minimum;
		
		protected List<BlockPos> positions;
		
		protected int checkX;
		protected int checkZ;
		protected TriPredicate<ServerWorld, AreaVaettrTileEntity, BlockPos> checkPos;
		
		public AreaVaettrTileEntity(TileEntityType<?> tiletype, Block blocktype, VaettrType type, int minimum, int checkX, int checkZ, TriPredicate<ServerWorld, AreaVaettrTileEntity, BlockPos> checkPos) {
			super(tiletype, blocktype, type);
			this.minimum = minimum;
			positions = new ArrayList<>();
			this.checkX = checkX;
			this.checkZ = checkZ;
			this.checkPos = checkPos;
			
		}
		
		@SubscribeEvent
		public void blockBreak(BlockEvent.BreakEvent event) {
			
			if (containsPosition(event.getPos()) && event.getWorld().getDimension().getType().equals(this.world.getDimension().getType()) 
					&& !getAttackTargets().contains(event.getPlayer())
					&& !event.getWorld().getBlockState(event.getPos()).getMaterial().isReplaceable()) {
				if (!event.getWorld().isRemote()) {
					getAttackTargetsRaw().add(event.getPlayer().getUniqueID());
					event.getPlayer().sendMessage(Translate.make("angered." + super.getType().getRegistryName().getPath(), vaettr.getName()));
				}
				
				for (int i = 0; i < world.rand.nextInt(100); i++) {
					world.addParticle(ParticleTypes.FLASH, pos.getX() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(), 
	    				pos.getY() + world.rand.nextDouble() - world.rand.nextDouble(), 
	    				pos.getZ() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(), 
	    				world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5, 
	    				world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5,
	    				world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5);
					world.addParticle(ParticleTypes.FLASH, event.getPos().getX() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(), 
		    				event.getPos().getY() + world.rand.nextDouble() - world.rand.nextDouble(), 
		    				event.getPos().getZ() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(), 
		    				world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5, 
		    				world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5,
		    				world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5);
				}
				
			}
		}
		
		
		public boolean containsPosition(BlockPos pos) {
			for (int y = minimum; y <= 256; y++) {
				for (BlockPos pos2 : positions) {
					if (pos.equals(new BlockPos(pos2.getX(), y, pos2.getZ()))) {
						return true;
					}
				}
			}
			return false;
		}
		
		public void tickClient(ClientWorld world) {
			
		}
		
		public void tickServer(ServerWorld world) {
			
			minimum = this.pos.getY() - 20;
			
			
			for (LivingEntity e : this.getAttackTargets()) {
				if (!this.containsPosition(e.getPosition()) || !e.isAlive()) {
					getAttackTargetsRaw().remove(e.getUniqueID());
				}
			}
			
			if (this.getLifetime() %50 ==0 ) {
				for (TileEntity te : world.loadedTileEntityList) {
					if (te instanceof VaettrTileEntity && te != this && this.containsPosition(te.getPos())) {
						System.out.println("Intrusive " + this.getClass().getSimpleName() + " " + this.getName());
						
						world.setBlockState(te.getPos(), Blocks.FIRE.getDefaultState());
					}
				}
			}
			
			if (getLifetime() < 3 || getLifetime() % 1000 == 0) {
				
				
				positions.clear();
				
				
				addPositionsToVaettr();
				

				if (positions.isEmpty()) {
					System.out.println("Empty " + this.getClass().getSimpleName() + " " + this.getName());
					world.setBlockState(pos, Blocks.FIRE.getDefaultState());
				}
				
				//System.out.println(positions);
				Networking.sendToAll(new TaskChangeBlock(world.getBlockState(pos), new ServerPos(this), this));
				
			}
		}
		
		public void addPositionsToVaettr() {
			for (int xd = -checkX; xd <= checkX; xd++) {
				for (int zd = -checkZ; zd <= checkZ; zd++) {
					BlockPos posm = new BlockPos(pos.getX() + xd, minimum, pos.getZ() + zd);
					if (checkPos.test((ServerWorld)world, this, posm)) {
						if (!positions.contains(posm)) {
							positions.add(posm);
						}
					}
				}
			}
		}
		

		@Override
		public CompoundNBT write(CompoundNBT nbt1) {
			CompoundNBT nbt = super.write(nbt1);
			
			nbt.put("Positions", GMNBT.makePosList(positions));
			
			return nbt;
		}
		
		@Override
		public void read(CompoundNBT nbt) {
			
			super.read(nbt);
			
			positions = GMNBT.createPosList(nbt.getList("Positions", NBT.TAG_COMPOUND));
		}
		
		public int getMinimum() {
			return minimum;
		}
		
		public List<BlockPos> getPositions() {
			return positions;
		}
		
	}

