package com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.deeds;

import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.SapientIdentity;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.TickEvent.WorldTickEvent;

public abstract class SapientDeed extends Occurrence {

	protected SapientIdentity citizen;

	public SapientDeed(OccurrenceType<?> type, SapientIdentity citizen) {
		super(type, null, 0);
		this.citizen = citizen;
	}

	public SapientDeed(OccurrenceType<?> type) {
		super(type);
	}

	public void setCitizen(SapientIdentity citizen) {
		this.citizen = citizen;
	}

	public void readData(Dynamic<?> dyn) {

		this.citizen = new SapientIdentity(dyn.get("id").get().get());
		this.readAdditionalData(dyn.get("data").get().get());
	}

	public abstract void readAdditionalData(Dynamic<?> dyn);

	public abstract <T> T writeAdditionalData(DynamicOps<T> ops);

	public SapientIdentity getCitizen() {
		return citizen;
	}

	@Override
	public void tick(WorldTickEvent event, long gameTime, long dayTime) {
		Entity e = ServerPos.getEntityFromUUID(this.citizen.getTrueId(), event.world.getServer());
		this.position = e.getPositionVector();
		this.dimension = e.dimension.getId();
		super.tick(event, gameTime, dayTime);
	}

	@Override
	public void affectCitizen(SapientInfo<? extends LivingEntity> e) {
	}

	/**
	 * Relationship change between the given citizen and deed performer
	 * 
	 * @param e
	 * @return
	 */
	public float getRelationshipChange(LivingEntity e) {
		return 0;
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {
		T dat = writeAdditionalData(ops);
		T uu = citizen.serialize(ops);
		T rl = ops.createString(this.type.getName().toString());
		return ops.createMap(
				ImmutableMap.of(ops.createString("id"), uu, ops.createString("data"), dat, ops.createString("rl"), rl));
	}

}
