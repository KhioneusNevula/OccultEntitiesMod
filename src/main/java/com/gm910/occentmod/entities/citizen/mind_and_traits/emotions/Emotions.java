package com.gm910.occentmod.entities.citizen.mind_and_traits.emotions;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.EntityDependentInformationHolder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class Emotions extends EntityDependentInformationHolder<CitizenEntity> {

	/**
	 * Positive: entertained<br>
	 * Negative: bored<br>
	 * May cause citizen to start hobby activities
	 */
	private float funLevel;

	/**
	 * Positive: happy<br>
	 * Negative: sad<br>
	 * May cause citizen to do hobbies or talk with people they like
	 */
	private float happinessLevel;

	/**
	 * above threshhold: doesn't need more socializing <Br>
	 * below threshhold / 3: deprived of socialization <br>
	 * Resets upon sleeping<br>
	 * May cause citizen to attempt talking with people they like or new people
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
