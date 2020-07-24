package com.gm910.occentmod.vaettr;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.blocks.VaettrTileEntity;
import com.gm910.occentmod.util.GMResource;
import com.gm910.occentmod.world.VaettrData;
import com.gm910.occentmod.world.Warper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * MAY ONLY BE CREATED ON SERVERSIDE
 * @author borah
 *
 */
public class Vaettr implements INBTSerializable<CompoundNBT>{
	
	public static enum VaettrType {
		landvaettr(20f, false, true),
		stormvaettr(20f, false, true),
		husvaettr(20f, false, true),
		vatnavaettr (20f, false, true),
		eldvaettr (-1f, true, false),
		brennisteinvaettr (20f, false, true),
		endisteinvaettr (20f, false, true),
		skogvaettr (20f, false, true),
		dyrvaettr (-1f, true, false),
		manvaettr (-1f, true, false),
		daudhvaettr (-1f, true, false),
		draugvaettr (-1f, true, false),
		endimanvaettr (-1f, true, false),
		svinimanvaettr (-1f, true, false),
		seidhvaettr (-1f, true, false),
		vanr (-1f, false, false);
		
		public final float health;
		public final boolean isEntity;
		public final boolean isTile;
		/**
		 * -1 if the health is determined by the entity or infinite
		 */
		private VaettrType(float health, boolean isEntity, boolean isTile) {
			this.health = health;
			this.isEntity = isEntity;
			this.isTile = isTile;
		}
	}

	private List<UUID> livingTargets = new ArrayList<>();
	
	private List<UUID> vaettrTargets = new ArrayList<>();
	
	private LivingEntity selfEntity;
	
	private VaettrTileEntity selfTileEntity;
	
	private String name;
	
	private boolean isDead;
	
	private int lifetime;
	
	private float health;
	
	private VaettrData data;
	
	private VaettrType type;
	
	public Vaettr() {
		
	}
	
	protected void setName() {
		if (name == null) {
			String[] names = GMResource.getNames(this.getType().name());
			name = names[(new Random()).nextInt(names.length)];
		}
	}
	
	public String getName() {
		return name;
	}
	
	public float getMaxHealth() {
		return selfEntity != null ? selfEntity.getMaxHealth() : this.type.health;
	}
	
	public float getHealth() {
		return health;
	}
	
	public void setHealth(float health) {
		this.health = health;
	}
	
	public Vaettr(LivingEntity en, VaettrType type) {
		this.selfEntity = en;
		this.isDead = !en.isAlive();
		this.lifetime = en.ticksExisted;
		if (en instanceof MobEntity && ((MobEntity) en).getAttackTarget() != null) this.livingTargets.add(  ((MobEntity) en).getAttackTarget().getUniqueID() );
		this.type = type;
		this.health = en.getHealth();
	}
	
	public void setData(VaettrData data) {
		this.data = data;
	}
	
	public Vaettr(VaettrTileEntity te, VaettrType type) {
		this.selfTileEntity = te;
		this.isDead = false;
		this.lifetime = 0;
		this.type = type;
		this.health = type.health;
	}
	
	/**
	 * Constructor for vanir
	 * @param name
	 */
	public Vaettr(String name) {
		this.type = VaettrType.vanr;
		this.health = type.health;
		this.isDead = false;
		this.lifetime = 0;
		this.name = name;
	}
	
	public VaettrType getType() {
		return type;
	}
	
	public void setPos(BlockPos pos) {
		if (this.selfEntity != null) {
			this.selfEntity.setPosition(pos.getX(), pos.getY(), pos.getZ());
		}
		if (this.selfTileEntity != null) {
			this.selfTileEntity.setPos(pos);
		}
	}
	
	public void setPos(Vec3d pos) {
		if (this.selfEntity != null) {
			this.selfEntity.setPosition(pos.getX(), pos.getY(), pos.getZ());
		}
		if (this.selfTileEntity != null) {
			this.selfTileEntity.setPos(new BlockPos(pos));
		}
		
	}
	
	public Vec3d getPos() {
		if (this.selfEntity != null) {
			return selfEntity.getPositionVector();
		}
		if (this.selfTileEntity != null) {
			return new Vec3d(selfTileEntity.getPos());
		}
		return Vec3d.ZERO;
	}
	
	public BlockPos getBlockPos() {
		if (this.selfEntity != null) {
			return selfEntity.getPosition();
		}
		if (this.selfTileEntity != null) {
			return selfTileEntity.getPos();
		}
		return BlockPos.ZERO;
		
	}
	
	public boolean isVanr() {
		return !hasTileEntity() && !hasEntity();
	}
	
	public ServerPos getServerPos() {
		return new ServerPos(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), getWorld().dimension.getType().getId());
	}
	
	public ServerWorld getWorld() {
		if (this.selfEntity != null) {
			return (ServerWorld) selfEntity.getEntityWorld();
		}
		if (this.selfTileEntity != null) {
			return (ServerWorld) selfTileEntity.getWorld();
		}
		return null;
	}
	
	public void setWorld(ServerWorld world) {
		if (this.selfEntity != null) {
			if (world != selfEntity.world) {
				Warper.teleportEntity(selfEntity, world.dimension.getType());
			}
		}
		if (this.selfTileEntity != null) {
			if (world != selfTileEntity.getWorld()) {
				selfTileEntity.getWorld().removeTileEntity(selfTileEntity.getPos());
				world.addTileEntity(selfTileEntity);
			}
		}
		
	}
	
	public VaettrData getData() {
		return this.data;
	}
	
	public List<LivingEntity> getLivingTargets() {
		List<LivingEntity> ens = new ArrayList<>();
		for (UUID uu : livingTargets) {
			ens.add( (LivingEntity) ServerPos.getEntityFromUUID(uu, data.getServer()));
		}
		return ens;
	}
	
	public List<UUID> getLivingTargetIds() {
		return livingTargets;
	}
	
	public List<UUID> getVaettrTargetIds() {
		return vaettrTargets;
	}
	
	public List<Vaettr> getVaettrTargets() {
		List<Vaettr> ens = new ArrayList<>();
		for (UUID uu : livingTargets) {
			ens.add(data.getVaettr(uu));
		}
		return ens;
	}
	
	public void setDead(boolean isDead) {
		this.isDead = isDead;
		
	}
	
	public void onDeath() {
		if (selfEntity != null) {
			selfEntity.remove();
		}
		if (selfTileEntity != null) {
			selfTileEntity.onDeath();
		}
	}
	
	public UUID getUniqueId() {
		return this.data.getForVaettr(this);
	}
	
	public boolean isDead() {
		return selfEntity != null ? !selfEntity.isAlive() : isDead;
	}
	
	public int getLifetime() {
		return selfEntity != null ? selfEntity.ticksExisted : lifetime;
	}
	
	public Entity getSelfEntity() {
		return selfEntity;
	}
	
	public TileEntity getSelfTileEntity() {
		return selfTileEntity;
	}
	
	public boolean hasEntity() {
		return this.selfEntity != null;
	}
	
	public boolean hasTileEntity() {
		return this.selfTileEntity != null;
	}
	
	public void tick() {
		if (this.selfEntity != null) {
			this.isDead = !selfEntity.isAlive();
			this.health = selfEntity.getHealth();
			this.lifetime = selfEntity.ticksExisted;
			if (this.selfEntity instanceof MobEntity) {
				UUID targ = ((MobEntity) this.selfEntity).getAttackTarget().getUniqueID();
				if (!this.livingTargets.contains(targ)) {
					this.livingTargets.add(targ);
				}
			}
			
		} else {
			this.lifetime ++;
		}
		
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putBoolean("Dead", isDead);
		nbt.putInt("Lifetime", lifetime);
		nbt.put("LivingTargets", GMNBT.makeUUIDList(this.livingTargets));
		nbt.put("VaettrTargets", GMNBT.makeUUIDList(this.vaettrTargets));
		nbt.putString("Name", name);
		nbt.putFloat("Health", health);
		return nbt;
	}


	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		isDead = nbt.getBoolean("Dead");
		lifetime = nbt.getInt("Lifetime");
		name = nbt.getString("Name");
		health = nbt.getFloat("Health");
		this.livingTargets = GMNBT.createUUIDList((ListNBT) nbt.get("LivingTargets"));
		this.vaettrTargets = GMNBT.createUUIDList((ListNBT) nbt.get("VaettrTargets"));
	}
	

}
