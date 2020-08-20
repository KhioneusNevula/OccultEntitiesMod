package com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.deeds;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceEffect;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceEffect.Connotation;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.PersonalityTrait;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.Relationships;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.WorldTickEvent;

public class MurderDeed extends CitizenDeed {

	private CitizenIdentity killed;

	/**
	 * Basic deserialization constructor, DO NOT USE
	 * 
	 * @param citizen
	 */
	public MurderDeed(CitizenIdentity citizen) {
		super(OccurrenceType.MURDER, citizen);
	}

	public MurderDeed() {
		super(OccurrenceType.MURDER);
	}

	public MurderDeed(CitizenIdentity murderer, CitizenIdentity victim) {
		this(murderer);
		killed = victim;
	}

	public CitizenIdentity getMurderer() {
		return this.citizen;
	}

	public CitizenIdentity getVictim() {
		return killed;
	}

	public void setVictim(CitizenIdentity killed) {
		this.killed = killed;
	}

	@Override
	public void readAdditionalData(Dynamic<?> dyn) {
		killed = new CitizenIdentity(dyn);
	}

	@Override
	public <T> T writeAdditionalData(DynamicOps<T> ops) {

		return killed.serialize(ops);
	}

	@Override
	public Object[] getDataForDisplay(CitizenEntity en) {
		return new Object[] { citizen.getString((ServerWorld) en.getEntityWorld()),
				killed.getString((ServerWorld) en.getEntityWorld()) };
	}

	@Override
	public float getRelationshipChange(CitizenEntity en) {
		Relationships ships = en.getRelationships();
		float sadism = en.getPersonality().getTrait(PersonalityTrait.SADISM);
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
