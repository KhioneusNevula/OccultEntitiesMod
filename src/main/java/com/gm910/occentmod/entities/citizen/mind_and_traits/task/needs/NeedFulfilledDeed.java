package com.gm910.occentmod.entities.citizen.mind_and_traits.task.needs;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Need;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceEffect;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceEffect.Connotation;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.deeds.CitizenDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NeedFulfilledDeed extends CitizenDeed {

	private Need<?> need;

	public NeedFulfilledDeed(CitizenIdentity citizen, Need<?> need) {
		super(OccurrenceType.NEED_FULFILLED, citizen);
		this.need = need;
	}

	public NeedFulfilledDeed() {
		super(OccurrenceType.NEED_FULFILLED);
	}

	@Override
	public void readAdditionalData(Dynamic<?> dyn) {
		need = NeedType.deserializeStatic(dyn.get("need").get().get());
	}

	@Override
	public <T> T writeAdditionalData(DynamicOps<T> ops) {
		return ops.createMap(ImmutableMap.of(ops.createString("need"), need.serialize(ops)));
	}

	@Override
	public Object[] getDataForDisplay(CitizenEntity en) {
		return new Object[] { this.citizen, need };
	}

	@Override
	public OccurrenceEffect getEffect() {
		return new OccurrenceEffect(
				ImmutableMap.of(this.citizen, this.need.isInDanger() ? Connotation.SAVIOR : Connotation.HELPFUL));
	}

	@Override
	public boolean isSimilarTo(Occurrence other) {

		if (!(other instanceof NeedFulfilledDeed)) {
			return false;
		}
		return ((NeedFulfilledDeed) other).need.equals(this.need);
	}

}
