package com.gm910.occentmod.sapience.mind_and_traits.trade;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.sapience.EntityDependentInformationHolder;
import com.mojang.datafixers.types.DynamicOps;

public class TradeHolder extends EntityDependentInformationHolder<CitizenEntity> {

	public TradeHolder(CitizenEntity en) {
		super(en);
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public <T> T serialize(DynamicOps<T> arg0) {
		return null;
	}

}
