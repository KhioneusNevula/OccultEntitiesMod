package com.gm910.occentmod.entities.citizen.mind_and_traits.emotions;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.EntityDependentInformationHolder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class Emotions extends EntityDependentInformationHolder {

	/**
	 * Positive: entertained<br>
	 * Negative: bored<br>
	 * May cause citizen to start hobby activities
	 */
	private float funLevel;

	/**
	 * above threshhold: doesn't need more socializing <Br>
	 * below threshhold / 3: deprived of socialization <br>
	 * Resets upon sleeping<br>
	 * May cause citizen to attempt socializing
	 */
	private float socialLevel;

	/**
	 * The citizen's comfort with their current situation<br>
	 * Different depending on paranoia levels. Also depends on the environment
	 */
	private float comfortLevel;

	public Emotions(CitizenEntity en) {
		super(en);
	}

	public <T> Emotions(CitizenEntity en, Dynamic<T> dyn) {
		super(en);
	}

	@Override
	public <T> T serialize(DynamicOps<T> p_218175_1_) {
		return null;
	}

}
