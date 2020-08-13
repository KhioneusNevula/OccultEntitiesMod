package com.gm910.occentmod.entities.citizen.mind_and_traits.deeds;

import java.util.HashSet;
import java.util.Set;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.CitizenTask;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;

public abstract class CitizenDeed implements IDynamicSerializable {

	protected CitizenDeedType<?> type;
	protected CitizenIdentity citizen;

	public CitizenDeed(CitizenDeedType<?> type, CitizenIdentity citizen) {
		this.type = type;
		this.citizen = citizen;
	}

	public void $readData(Dynamic<?> dyn) {

		this.type = CitizenDeedType.get(new ResourceLocation(dyn.get("rl").asString("")));
		this.citizen = new CitizenIdentity(dyn.get("id").get().get());
		this.readData(dyn.get("data").get().get());
	}

	public abstract void readData(Dynamic<?> dyn);

	public abstract <T> T writeData(DynamicOps<T> ops);

	public CitizenIdentity getCitizen() {
		return citizen;
	}

	public CitizenDeedType<?> getType() {
		return type;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T dat = writeData(ops);
		T uu = citizen.serialize(ops);
		T rl = ops.createString(this.type.getName().toString());
		return ops.createMap(
				ImmutableMap.of(ops.createString("id"), uu, ops.createString("data"), dat, ops.createString("rl"), rl));
	}

	public abstract Object[] getDataForDisplay(CitizenEntity en);

	public Set<CitizenTask> getPotentialWitnessReactions() {
		return new HashSet<>();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " of type " + this.getType();
	}

}
