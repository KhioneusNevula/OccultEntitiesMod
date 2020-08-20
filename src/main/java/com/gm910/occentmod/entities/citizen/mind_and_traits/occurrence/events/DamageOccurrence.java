package com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.events;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceEffect;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;

public class DamageOccurrence extends Occurrence {

	private long damageTime;

	private LivingEntity damaged;

	private DamageSource source;

	private Entity immediateDamager;

	private Entity trueDamager;

	private float damageAmount;

	private boolean wasUnblockable;

	private String damageType;

	private boolean wasMagic;

	private boolean wasFire;

	private float hungerDamage;

	private Vec3d location;

	public DamageOccurrence(LivingEntity damaged, long time, DamageSource source, float amount) {
		super(null, damaged.getPositionVector(), damaged.dimension.getId());
		this.damageTime = time;
		this.damaged = damaged;
		this.source = source;
		this.immediateDamager = source.getImmediateSource();
		this.trueDamager = source.getTrueSource();
		this.damageAmount = amount;
		this.wasFire = source.isFireDamage();
		this.wasMagic = source.isMagicDamage();
		this.hungerDamage = source.getHungerDamage();
		this.location = source.getDamageLocation();
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

	public float getHungerDamage() {
		return hungerDamage;
	}

	public Entity getImmediateDamager() {
		return immediateDamager;
	}

	public Vec3d getLocation() {
		return location;
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

	public void setHungerDamage(float hungerDamage) {
		this.hungerDamage = hungerDamage;
	}

	public void setImmediateDamager(Entity immediateDamager) {
		this.immediateDamager = immediateDamager;
	}

	public void setLocation(Vec3d location) {
		this.location = location;
	}

	public void setSource(DamageSource source) {
		this.source = source;
	}

	public void setTrueDamager(Entity trueDamager) {
		this.trueDamager = trueDamager;
	}

	public void setWasFire(boolean wasFire) {
		this.wasFire = wasFire;
	}

	public void setWasMagic(boolean wasMagic) {
		this.wasMagic = wasMagic;
	}

	public void setWasUnblockable(boolean wasUnblockable) {
		this.wasUnblockable = wasUnblockable;
	}

	@Override
	public void readData(Dynamic<?> dyn) {

	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {
		// TODO Auto-generated method stub
		return null;
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
		return this.damageType.equalsIgnoreCase(c.damageType)
				|| (this.trueDamager != null && c.trueDamager != null && this.trueDamager.equals(c.trueDamager))
				|| (this.immediateDamager != null && c.immediateDamager != null
						&& this.immediateDamager.equals(c.immediateDamager));
	}

}
