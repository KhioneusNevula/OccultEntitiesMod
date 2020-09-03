package com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds;

import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceEffect;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceType;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceEffect.Connotation;
import com.gm910.occentmod.sapience.mind_and_traits.personality.PersonalityTrait;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.Relationships;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.WorldTickEvent;

public class MurderDeed extends SapientDeed {

	private SapientIdentity killed;

	/**
	 * Basic deserialization constructor, DO NOT USE
	 * 
	 * @param citizen
	 */
	public MurderDeed(SapientIdentity citizen) {
		super(OccurrenceType.MURDER, citizen);
	}

	public MurderDeed() {
		super(OccurrenceType.MURDER);
	}

	public MurderDeed(SapientIdentity murderer, SapientIdentity victim) {
		this(murderer);
		killed = victim;
	}

	public SapientIdentity getMurderer() {
		return this.citizen;
	}

	public SapientIdentity getVictim() {
		return killed;
	}

	public void setVictim(SapientIdentity killed) {
		this.killed = killed;
	}

	@Override
	public void readAdditionalData(Dynamic<?> dyn) {
		killed = new SapientIdentity(dyn);
	}

	@Override
	public <T> T writeAdditionalData(DynamicOps<T> ops) {

		return killed.serialize(ops);
	}

	@Override
	public Object[] getDataForDisplay(LivingEntity en) {
		return new Object[] { citizen.getString((ServerWorld) en.getEntityWorld()),
				killed.getString((ServerWorld) en.getEntityWorld()) };
	}

	@Override
	public float getRelationshipChange(LivingEntity en) {
		Relationships ships = SapientInfo.get(en).getRelationships();
		float sadism = SapientInfo.get(en).getPersonality().getTrait(PersonalityTrait.SADISM);
		int change = (int) ships.getLikeValue(killed);

		return (int) (-sadism * (change));
	}

	@Override
	public void tick(WorldTickEvent event, long gameTime, long dayTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public OccurrenceEffect getEffect() {

		return new OccurrenceEffect(ImmutableMap.of(this.killed, Connotation.FATAL));
	}

	@Override
	public boolean isSimilarTo(Occurrence other) {
		if (!(other instanceof MurderDeed))
			return false;
		return other.getType() == this.type && this.getMurderer().equals(((MurderDeed) other).getMurderer());
	}

}
