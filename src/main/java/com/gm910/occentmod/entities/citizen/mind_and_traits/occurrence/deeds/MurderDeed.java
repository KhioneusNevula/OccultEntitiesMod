package com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.deeds;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.CitizenInformation;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.NumericPersonalityTrait;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.Relationships;
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
	public void affectCitizen(CitizenInformation<CitizenEntity> e) {
		super.affectCitizen(e);
		// TODO happiness and all that
	}

	@Override
	public int getRelationshipChange(CitizenEntity en) {
		Relationships ships = en.getRelationships();
		float sadism = en.getPersonality().getTrait(NumericPersonalityTrait.SADISM);
		int change = (int) ships.getLikeValue(killed);

		return (int) (-sadism * (change));
	}

	@Override
	public void tick(WorldTickEvent event, long gameTime, long dayTime) {
		// TODO Auto-generated method stub

	}

}
