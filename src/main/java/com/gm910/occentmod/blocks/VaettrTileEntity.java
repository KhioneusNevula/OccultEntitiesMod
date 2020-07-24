package com.gm910.occentmod.blocks;

import java.util.List;
import java.util.UUID;

import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.vaettr.IHasVaettr;
import com.gm910.occentmod.vaettr.Vaettr;
import com.gm910.occentmod.vaettr.Vaettr.VaettrType;
import com.gm910.occentmod.world.VaettrData;

import net.minecraft.block.Block;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;


public class VaettrTileEntity extends TileEntity implements ITickableTileEntity, IHasVaettr {

		protected UUID vaettrId;
		protected Vaettr vaettr;
		
		protected Block blocktype;
		
		public VaettrTileEntity(TileEntityType<?> tiletype, Block blocktype, VaettrType type) {
			super(tiletype);
			MinecraftForge.EVENT_BUS.register(this);
			this.blocktype = blocktype;
			vaettr = new Vaettr(this, type);
		}
		
		
		public void onDeath() {
			System.out.println("Death of " + this.getClass().getSimpleName() + " named " + vaettr.getName() + " at " + pos + " in dimension " + this.world.dimension.getType());
			
		}
		
		public List<LivingEntity> getAttackTargets() { 
			return this.vaettr.getLivingTargets();
		}
		
		public List<UUID> getAttackTargetsRaw() { 
			return this.vaettr.getLivingTargetIds();
		}

		@Override
		public void tick() {
			if (vaettr.getLifetime() < 3 || vaettr.getLifetime() % 500 == 0) {
		    	
	    	}
			
			
			if (!this.world.isRemote) {

				
				ServerWorld world = (ServerWorld) this.world;
				
				tickServer(world);
				
				
		    } else {
		    	
		    	tickClient((ClientWorld)world);
		    }
		}
		
		public void tickClient(ClientWorld world) {
			
		}
		
		public void tickServer(ServerWorld world) {
			
		}
		
		public UUID getVaettrId() {
			return vaettr == null ? vaettrId : vaettr.getUniqueId();
		}
		
		@Override
		public Vaettr getVaettr() {
		// TODO Auto-generated method stub
			return vaettr;
		}
		
		public String getName() {
			return vaettr.getName();
		}
		
		@Override
		public void setVaettr(Vaettr vaet) {
		// TODO Auto-generated method stub
			this.vaettr = vaet;
			this.vaettrId = vaet.getUniqueId();
		}
		
		@Override
		public CompoundNBT write(CompoundNBT nbt1) {
			CompoundNBT nbt = super.write(nbt1);
			nbt.putUniqueId("Vaettr", this.vaettr.getUniqueId());
			
			return nbt;
		}
		
		@Override
		public void read(CompoundNBT nbt) {
			
			super.read(nbt);
			this.vaettrId = nbt.getUniqueId("Vaettr");
		}
		
		@Override
		public void remove() {
			super.remove();
			this.onDeath();
		}
		
		@Override
		public void onLoad() {
			
			super.onLoad();
			VaettrData.get(this.world.getServer()).addVaettr(this.vaettr);
		}
		
		@Override
		public CompoundNBT getUpdateTag() {
			System.out.println("Sending updates");
			return this.write(super.getUpdateTag());
		}
		
		public int getLifetime() {
			return vaettr.getLifetime();
		}
		
	}

