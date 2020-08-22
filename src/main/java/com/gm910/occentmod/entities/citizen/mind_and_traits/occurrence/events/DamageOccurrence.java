package com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.events;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.damage.MagicDamage;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceEffect;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceType;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class DamageOccurrence extends Occurrence {

	private long damageTime;

	private LivingEntity damaged;

	private DamageSource source;

	private Entity immediateDamager;

	private Entity trueDamager;

	private float damageAmount;

	private String damageType;

	private ServerWorld world;

	public DamageOccurrence(LivingEntity damaged, long time, DamageSource source, float amount) {
		super(OccurrenceType.DAMAGE, damaged.getPositionVector(), damaged.dimension.getId());
		if (!damaged.isServerWorld()) {
			throw new IllegalArgumentException("Client side entity " + damaged);
		}
		this.damageTime = time;
		this.damaged = damaged;
		this.setSource(source);
		this.damageAmount = amount;
		this.world = (ServerWorld) damaged.world;
	}

	public DamageOccurrence(ServerWorld world) {
		super(OccurrenceType.DAMAGE, null, 0);
		this.world = world;
	}

	public BlockPos getPlace() {
		return new BlockPos(this.getPosition());
	}

	public float getDamageAmount() {
		return damageAmount;
	}

	public LivingEntity getDamaged() {
		return damaged;
	}

	public long getDamageTime() {
		return damageTime;
	}

	public String getDamageType() {
		return damageType;
	}

	public Entity getImmediateDamager() {
		return immediateDamager;
	}

	public DamageSource getSource() {
		return source;
	}

	public Entity getTrueDamager() {
		return trueDamager;
	}

	public void setDamageAmount(float damageAmount) {
		this.damageAmount = damageAmount;
	}

	public void setDamaged(LivingEntity damaged) {
		this.damaged = damaged;
	}

	public void setDamageTime(long damageTime) {
		this.damageTime = damageTime;
	}

	public void setDamageType(String damageType) {
		this.damageType = damageType;
	}

	public void setImmediateDamager(Entity immediateDamager) {
		this.immediateDamager = immediateDamager;
	}

	public void setSource(DamageSource source) {
		this.source = source;
		this.immediateDamager = source.getImmediateSource();
		this.trueDamager = source.getTrueSource();
	}

	public void setTrueDamager(Entity trueDamager) {
		this.trueDamager = trueDamager;
	}

	@Override
	public void readData(Dynamic<?> dyn) {
		damaged = (LivingEntity) ServerPos.getEntityFromUUID(UUID.fromString(dyn.get("damaged").asString("")),
				world.getServer());
		if (dyn.get("immediate").get().isPresent()) {
			immediateDamager = (LivingEntity) ServerPos
					.getEntityFromUUID(UUID.fromString(dyn.get("immediate").asString("")), world.getServer());
		}
		if (dyn.get("true").get().isPresent()) {
			trueDamager = (LivingEntity) ServerPos.getEntityFromUUID(UUID.fromString(dyn.get("true").asString("")),
					world.getServer());
		}
		damageAmount = dyn.get("amount").asFloat(0);
		damageType = dyn.get("dtype").asString("");
		source = null;
		if (immediateDamager != trueDamager) {
			this.source = new IndirectEntityDamageSource(damageType, trueDamager, immediateDamager);
		} else if (immediateDamager != null) {
			setSource(new EntityDamageSource(damageType, immediateDamager));
		} else {
			if (damageType.equals("netherBed")) {
				setSource(DamageSource.netherBedExplosion());
			} else {
				Set<Field> fields = Sets.newHashSet(DamageSource.class.getDeclaredFields()).stream().filter((f) -> {
					return Modifier.isStatic(f.getModifiers()) && f.getType().equals(DamageSource.class);
				}).collect(Collectors.toSet());
				fields.addAll(Sets.newHashSet(MagicDamage.class.getDeclaredFields()).stream().filter((f) -> {
					return Modifier.isStatic(f.getModifiers()) && f.getType().equals(DamageSource.class);
				}).collect(Collectors.toSet()));
				for (Field f : fields) {
					DamageSource dam = null;
					try {
						dam = (DamageSource) f.get(null);
					} catch (Throwable thr) {
						continue;
					}
					if (dam.damageType.equals(damageType)) {
						setSource(dam);
					}
				}
				if (source == null) {
					setSource(new DamageSource(damageType));

				}
			}
		}
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {

		Map<T, T> mapa = new HashMap<>();
		mapa.put(ops.createString("damaged"), ops.createString(damaged.getCachedUniqueIdString()));
		if (immediateDamager != null) {
			mapa.put(ops.createString("immediate"), ops.createString(immediateDamager.getCachedUniqueIdString()));
		}

		if (trueDamager != null) {
			mapa.put(ops.createString("true"), ops.createString(trueDamager.getCachedUniqueIdString()));
		}

		mapa.put(ops.createString("amount"), ops.createFloat(this.damageAmount));
		mapa.put(ops.createString("dtype"), ops.createString(this.damageType));
		return ops.createMap(mapa);
	}

	@Override
	public Object[] getDataForDisplay(CitizenEntity en) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OccurrenceEffect getEffect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSimilarTo(Occurrence other) {

		if (!(other instanceof DamageOccurrence))
			return false;
		DamageOccurrence c = (DamageOccurrence) other;
		boolean sameCauser = (this.trueDamager != null && c.trueDamager != null
				&& this.trueDamager.equals(c.trueDamager))
				|| (this.immediateDamager != null && c.immediateDamager != null
						&& this.immediateDamager.equals(c.immediateDamager));
		if (sameCauser)
			return true;
		boolean samePos = this.getPlace().equals(c.getPlace());
		if (samePos)
			return true;

		boolean sameType = this.damageType.equals(c.damageType);
		if (sameType)
			return true;

		return false;
	}

}
