package com.gm910.occentmod.sapience.mind_and_traits.needs.checkers;

import java.util.function.BiPredicate;
import java.util.function.Function;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.sapience.mind_and_traits.needs.Need;
import com.gm910.occentmod.sapience.mind_and_traits.needs.NeedChecker;
import com.gm910.occentmod.sapience.mind_and_traits.needs.NeedType;

public class HungerChecker extends NeedChecker<CitizenEntity, Float> {
	private static Function<CitizenEntity, Float> calc = (e) -> {
		return e.getMaxFoodLevel() * 3 / 4;
	};

	private static BiPredicate<CitizenEntity, Float> dangerous = (e, f) -> {
		return e.getMaxFoodLevel() / 4 > f;
	};

	public HungerChecker(NeedType<CitizenEntity, Float> type, CitizenEntity entity) {
		super(type, entity);
	}

	@Override
	public boolean fulfillNeeds() {
		return (this.entity.getFoodLevel() >= calc.apply(entity));
	}

	@Override
	public Need<CitizenEntity, Float> findNeeds() {

		CitizenEntity e = this.entity;
		if (e.getFoodLevel() < e.getMaxFoodLevel() / 2) {
			float m = calc.apply(e);
			Need<CitizenEntity, Float> n = new Need<CitizenEntity, Float>(NeedType.HUNGER, m);
			if (dangerous.test(e, m)) {
				n.makeDangerous();
			}
			return n;
		}
		return null;
	}
}