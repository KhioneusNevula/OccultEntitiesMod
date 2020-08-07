package com.gm910.occentmod.entities.citizen.mind_and_traits.deeds;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.world.server.ServerWorld;

public class MurderDeed extends CitizenDeed {

	private CitizenIdentity killed;

	/**
	 * Basic deserialization constructor, DO NOT USE
	 * 
	 * @param citizen
	 */
	public MurderDeed(CitizenIdentity citizen) {
		super(CitizenDeedType.MURDER, citizen);
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
	public void readData(Dynamic<?> dyn) {
		killed = new CitizenIdentity(dyn);
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {

		return killed.serialize(ops);
	}

	@Override
	public Object[] getDataForDisplay(CitizenEntity en) {
		return new Object[] { citizen.getString((ServerWorld) en.getEntityWorld()),
				killed.getString((ServerWorld) en.getEntityWorld()) };
	}

}
