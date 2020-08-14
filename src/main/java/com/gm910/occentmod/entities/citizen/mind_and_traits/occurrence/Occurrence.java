package com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence;

import java.util.HashSet;
import java.util.Set;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.CitizenInformation;
import com.gm910.occentmod.entities.citizen.mind_and_traits.deeds.OccurrenceType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.CitizenTask;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;

public abstract class Occurrence implements IDynamicSerializable {

	protected OccurrenceType<?> type;

	public Occurrence(OccurrenceType<?> type) {
		this.type = type;
	}

	public void $readData(Dynamic<?> dyn) {

		this.type = OccurrenceType.get(new ResourceLocation(dyn.get("rl").asString("")));
		this.readData(dyn.get("data").get().get());
	}

	public abstract void readData(Dynamic<?> dyn);

	public abstract <T> T writeData(DynamicOps<T> ops);

	public OccurrenceType<?> getType() {
		return type;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T dat = writeData(ops);
		T rl = ops.createString(this.type.getName().toString());
		return ops.createMap(ImmutableMap.of(ops.createString("data"), dat, ops.createString("rl"), rl));
	}

	public abstract Object[] getDataForDisplay(CitizenEntity en);

	public Set<CitizenTask> getPotentialWitnessReactions() {
		return new HashSet<>();
	}

	/**
	 * Affect the given citizen's mental state
	 * 
	 * @param e
	 */
	public void affectCitizen(CitizenInformation<CitizenEntity> e) {

	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " of type " + this.getType();
	}

}
