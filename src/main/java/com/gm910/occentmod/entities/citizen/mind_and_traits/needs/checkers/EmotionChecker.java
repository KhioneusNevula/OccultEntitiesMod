package com.gm910.occentmod.entities.citizen.mind_and_traits.needs.checkers;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.emotions.Emotions.EmotionType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Need;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedChecker;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedType;

public class EmotionChecker extends NeedChecker<Float> {

	private EmotionType emotionType;

	public EmotionChecker(NeedType<Float> type, EmotionType type2, CitizenEntity entity) {
		super(type, entity);
		this.emotionType = type2;

	}

	public EmotionType getEmotionType() {
		return emotionType;
	}

	@Override
	public boolean fulfillNeeds() {

		float curval = this.entity.getEmotions().getLevel(emotionType);
		float desval = this.entity.getEmotions().getThreshholdOfSatisfaction(entity.getPersonality(), emotionType);
		return curval >= desval;
	}

	@Override
	public Need<Float> findNeeds() {
		float curval = this.entity.getEmotions().getLevel(emotionType);
		float desval = this.entity.getEmotions().getThreshholdOfSatisfaction(entity.getPersonality(), emotionType);

		CitizenEntity e = this.entity;
		if (curval <= desval) {
			return new Need<Float>(super.getType(), desval);
		}
		return null;
	}
}