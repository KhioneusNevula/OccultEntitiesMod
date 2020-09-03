package com.gm910.occentmod.sapience.mind_and_traits.needs.checkers;

import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.sapience.mind_and_traits.emotions.Emotions.EmotionType;
import com.gm910.occentmod.sapience.mind_and_traits.needs.Need;
import com.gm910.occentmod.sapience.mind_and_traits.needs.NeedChecker;
import com.gm910.occentmod.sapience.mind_and_traits.needs.NeedType;

import net.minecraft.entity.LivingEntity;

public class EmotionChecker extends NeedChecker<LivingEntity, Float> {

	private EmotionType emotionType;

	public EmotionChecker(NeedType<LivingEntity, Float> type, EmotionType type2, LivingEntity entity) {
		super(type, entity);
		this.emotionType = type2;

	}

	public EmotionType getEmotionType() {
		return emotionType;
	}

	@Override
	public boolean fulfillNeeds() {

		SapientInfo<LivingEntity> entity = SapientInfo.get(this.entity);
		float curval = entity.getEmotions().getLevel(emotionType);
		float desval = entity.getEmotions().getThreshholdOfSatisfaction(entity.getPersonality(), emotionType);
		return curval >= desval;
	}

	@Override
	public Need<LivingEntity, Float> findNeeds() {
		SapientInfo<LivingEntity> entity = SapientInfo.get(this.entity);
		float curval = entity.getEmotions().getLevel(emotionType);
		float desval = entity.getEmotions().getThreshholdOfSatisfaction(entity.getPersonality(), emotionType);

		LivingEntity e = this.entity;
		if (curval <= desval) {
			return new Need<LivingEntity, Float>(super.getType(), desval);
		}
		return null;
	}
}