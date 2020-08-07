package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.Map;
import java.util.Set;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.deeds.CitizenDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.Personality.NumericPersonalityTrait;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.ReactionDeterminer;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.google.common.collect.Sets;

import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;

public abstract class CitizenTask extends Task<CitizenEntity> {

	private Set<Context> contexts = Sets.newHashSet();

	public CitizenTask(Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn, int durationMinIn,
			int durationMaxIn) {
		super(requiredMemoryStateIn, durationMinIn, durationMaxIn);
	}

	public CitizenTask setContext(Context... contexts) {
		this.contexts = Sets.newHashSet(contexts);
		return this;
	}

	public CitizenTask addContext(Context... contexts) {
		this.contexts.addAll(Sets.newHashSet(contexts));
		return this;
	}

	public Set<Context> getContexts() {
		return contexts;
	}

	public CitizenTask(Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn, int duration) {
		super(requiredMemoryStateIn, duration, duration);
	}

	public CitizenTask(Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn) {
		super(requiredMemoryStateIn, 60);
	}

	/*
	 * Return null if it's nothing worth talking about
	 */
	public CitizenDeed getDeed(CitizenIdentity identity) {
		return null;
	}

	public final boolean isPersistent() {
		return this instanceof IPersistentTask;
	}

	public boolean cannotBeOverriden() {
		return false;
	}

	public abstract Map<NumericPersonalityTrait, ReactionDeterminer<ImmediateTask>> getPotentialWitnessReactions();

	public enum Context {
		TALK, USE_ITEM, MOVE, WORK, MAGIC,
		/**
		 * These should not explicitly cause the entity to behave a certain way; they
		 * only should manipulate the entity's internal information holders
		 */
		BACKGROUND,
		/**
		 * These should not explicitly cause the entity to behave a certain way; they
		 * only should manipulate the entity's internal information holders
		 */
		CORE

	}

}
