package com.gm910.occentmod.entities.citizen.mind_and_traits.needs.checkers;

import java.util.function.Function;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Need;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedChecker;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedType;

public class HungerChecker extends NeedChecker<Float> {
	private Function<CitizenEntity, Float> calc = (e) -> {
		return e.getMaxFoodLevel() * 3 / 4;
	};

	public HungerChecker(NeedType<Float> type, CitizenEntity entity) {
		super(type, entity);
	}

	@Override
	public boolean fulfillNeeds() {
		return (this.entity.getFoodLevel() >= calc.apply(entity));
	}

	@Override
	public Need<Float> findNeeds() {

		CitizenEntity e = this.entity;
		if (e.getFoodLevel() < e.getMaxFoodLevel() / 2) {
			return new Need<Float>(NeedType.HUNGER, calc.apply(e));
		}
		return null;
	}
}