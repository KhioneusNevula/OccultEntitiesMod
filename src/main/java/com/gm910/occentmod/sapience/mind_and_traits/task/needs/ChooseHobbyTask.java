package com.gm910.occentmod.sapience.mind_and_traits.task.needs;

import java.util.Optional;

import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.sapience.mind_and_traits.emotions.Emotions.EmotionType;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds.SapientDeed;
import com.gm910.occentmod.sapience.mind_and_traits.personality.PersonalityTrait;
import com.gm910.occentmod.sapience.mind_and_traits.personality.PersonalityTrait.TraitLevel;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.gm910.occentmod.sapience.mind_and_traits.task.ImmediateTask;
import com.gm910.occentmod.sapience.mind_and_traits.task.Necessity;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientTask;
import com.gm910.occentmod.sapience.mind_and_traits.task.TaskType;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public class ChooseHobbyTask<E extends LivingEntity> extends ImmediateTask<E> {

	public ChooseHobbyTask(E entity) {
		super((Class<E>) entity.getClass(), ImmutableMap.of());
		addContext(Context.CORE);
	}

	@Override
	protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn) {
		super.startExecuting(worldIn, entityIn, gameTimeIn);
		SapientInfo<E> info = SapientInfo.get(entityIn);
		float funLevel = info.getEmotions().getLevel(EmotionType.FUN);
		Optional<TaskType<? super E, ?>> otype = info.getPersonality().getHobbies().stream().findAny()
				.filter((e) -> ((TaskType<? super E, ?>) e).getDoerClass().isAssignableFrom(entityIn.getClass()))
				.map((e) -> (TaskType<? super E, ?>) e);
		if (!otype.isPresent()) {
			info.getPersonality().initHobbies();
			return;
		}
		info.getAutonomy().considerTask((int) (4 / info.getPersonality().getTrait(PersonalityTrait.RESTLESSNESS)),
				otype.get().createNew(info.getAutonomy()),
				PersonalityTrait.RESTLESSNESS.getWeightedRandomReaction(
						info.getPersonality().getTrait(PersonalityTrait.RESTLESSNESS)) == TraitLevel.EXCEPTIONAL_HIGH
								? Necessity.NECESSARY
								: (funLevel <= 0 ? Necessity.PREFERABLE : Necessity.UNNECESSARY));
	}

	@Override
	public SapientDeed getDeed(SapientIdentity doer) {
		return null;
	}

	@Override
	public TaskType<E, ? extends SapientTask<E>> getType() {
		return null;
	}

}
