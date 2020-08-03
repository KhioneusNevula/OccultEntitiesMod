package com.gm910.occentmod.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.gm910.occentmod.api.networking.messages.Networking;
import com.gm910.occentmod.api.networking.messages.types.TaskParticles;
import com.gm910.occentmod.init.BlockInit;
import com.gm910.occentmod.vaettr.IHasVaettr;
import com.gm910.occentmod.vaettr.Vaettr;
import com.gm910.occentmod.vaettr.Vaettr.VaettrType;
import com.gm910.occentmod.world.VaettrData;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

public class VaettrTileEntity extends TileEntity implements ITickableTileEntity, IHasVaettr {

	protected UUID vaettrId;
	protected Vaettr vaettr;

	protected Thread serverTick;
	protected Thread clientTick;

	protected Block blocktype;

	public static final List<Block> getVaettrBlocks() {
		return Lists.newArrayList(BlockInit.BRENNISTEINVAETTR.get(), BlockInit.ENDISTEINVAETTR.get(),
				BlockInit.LANDVAETTR.get(), BlockInit.STORMVAETTR.get());
	}

	public VaettrTileEntity(TileEntityType<?> tiletype, Block blocktype, VaettrType type) {
		super(tiletype);
		MinecraftForge.EVENT_BUS.register(this);
		this.blocktype = blocktype;
		vaettr = new Vaettr(this, type);
		serverTick = new Thread(() -> tickServer((ServerWorld) world));
		clientTick = new Thread(() -> tickClient((ClientWorld) world));
	}

	public void onDeath() {
		System.out.println("Death of " + vaettr + " at " + pos + " in dimension " + this.world.dimension.getType());

		this.vaettr = null;
		world.setBlockState(pos, Blocks.FIRE.getDefaultState());
		int count = world.rand.nextInt(20) + 10;
		for (int i = 0; i < count; i++) {
			SmallFireballEntity fireball = new SmallFireballEntity(world, pos.getX() + 0.5, pos.getY() + 0.5,
					pos.getZ() + 0.5, world.rand.nextDouble() * 6 - 3, world.rand.nextDouble() * 6 - 3,
					world.rand.nextDouble() * 6 - 3);
			fireball.noClip = true;
			fireball.setNoGravity(true);
			world.addEntity(fireball);
		}
	}

	public List<LivingEntity> getAttackTargets() {
		return this.vaettr == null ? new ArrayList<>() : vaettr.getLivingTargets();
	}

	public List<UUID> getAttackTargetsRaw() {
		return this.vaettr == null ? new ArrayList<>() : vaettr.getLivingTargetIds();
	}

	public List<LivingEntity> getWorshipers() {
		return this.vaettr == null ? new ArrayList<>() : vaettr.getWorshipers();
	}

	public List<UUID> getWorshipersRaw() {
		return this.vaettr == null ? new ArrayList<>() : vaettr.getWorshiperIds();
	}

	@Override
	public void tick() {

		if (!this.world.isRemote) {

			if (vaettr == null || vaettr.isDead()) {
				System.out.println("Vaettr is " + vaettr + " dead ");
				this.onDeath();
				return;
			}

			ServerWorld world = (ServerWorld) this.world;
			tickServer(world);
			// pois.getInSquare(AIInit.VAETTR_POI.get().getPredicate(), pos, 10,
			// PointOfInterestManager.Status.IS_OCCUPIED).findAny().get();
			// serverTick.start();
			for (int i = 0; i < world.rand.nextInt(100); i++) {
				for (int ya = 0; ya <= 10; ya++)
					Networking.sendToAll(new TaskParticles(ParticleTypes.PORTAL,
							pos.getX() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(),
							pos.getY() + ya + world.rand.nextDouble() - world.rand.nextDouble(),
							pos.getZ() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(),
							world.dimension.getType(), world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5,
							world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5,
							world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5, false, false, false));
			}

		} else {
			tickClient((ClientWorld) world);
			// clientTick.start();
			/*if (vaettrId == null && vaettr != null) {
				//vaettr.tick();
				vaettr.setDead(true);
			}*/

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
		return vaettr != null ? vaettr.getName() : "";
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
		if (vaettr != null) {
			nbt.putUniqueId("Vaettr", this.vaettr.getUniqueId());
			nbt.put("VaettrData", vaettr.serializeNBT());
		}
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt) {

		super.read(nbt);
		if (nbt.contains("Vaettr")) {
			this.vaettrId = nbt.getUniqueId("Vaettr");
		}
		if (nbt.contains("VaettrData")) {
			this.vaettr = new Vaettr(nbt.getCompound("VaettrData"));

		}
	}

	@Override
	public void remove() {
		super.remove();
		System.out.println("Vaettr tile entity being removed " + this.vaettr + " " + this.getClass().getSimpleName());
		if (this.vaettr != null) {
			this.vaettr.setDead(true);
			if (this.world != null && this.world.isRemote) {
				this.vaettr = null;
			}
		}
	}

	@Override
	public void onLoad() {

		super.onLoad();
		if (!world.isRemote) {
			if (vaettrId != null) {
				vaettr = VaettrData.get(this.world.getServer()).getVaettr(vaettrId);
			} else {
				VaettrData.get(this.world.getServer()).addVaettr(this.vaettr);
			}
		} else {
			if (vaettrId != null) {
				vaettrId = null;
			}
		}
	}

	@Override
	public CompoundNBT getUpdateTag() {
		System.out.println("Sending updates");
		return this.write(super.getUpdateTag());
	}

	public int getLifetime() {
		return vaettr == null ? 0 : vaettr.getLifetime();
	}

}
