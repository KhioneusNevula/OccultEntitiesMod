package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.gm910.occentmod.api.util.NonNullMap;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.deeds.CitizenDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.NumericPersonalityTrait;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.NumericPersonalityTrait.TraitLevel;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

public abstract class CitizenTask extends Task<CitizenEntity> {

	private Set<Context> contexts = Sets.newHashSet();

	private Map<NumericPersonalityTrait, Set<TraitLevel>> willPerform = new NonNullMap<>(() -> Sets.newHashSet());

	public CitizenTask(Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn, int durationMinIn,
			int durationMaxIn) {
		super(requiredMemoryStateIn, durationMinIn, durationMaxIn);
	}

	public CitizenTask setContext(Context... contexts) {
		this.contexts = Sets.newHashSet(contexts);
		return this;
	}

	/**
	 * 
	 * @param willPerform if this is false, it will determine which trait-levels do
	 *                    NOT perform this task
	 * @param pairs       the trait-types and the reaction-types that allow for this
	 *                    task's performance. If empty, this means all citizens are
	 *                    capable of performing the task regardless of personality.
	 * @return
	 */
	public CitizenTask setPersonalityConditions(boolean willPerform,
			Pair<NumericPersonalityTrait, Collection<TraitLevel>>... pairs) {
		if (willPerform) {
			for (Pair<NumericPersonalityTrait, Collection<TraitLevel>> pair : pairs) {
				this.willPerform.get(pair.getFirst()).addAll(pair.getSecond());
			}
		} else {
			Set<Pair<NumericPersonalityTrait, Collection<TraitLevel>>> pairsSet = Sets.newHashSet(pairs);
			for (NumericPersonalityTrait trait : NumericPersonalityTrait.values()) {
				for (TraitLevel level : TraitLevel.values()) {
					if (!pairsSet.stream().anyMatch((p) -> p.getFirst() == trait && p.getSecond().contains(level))) {
						this.willPerform.get(trait).add(level);
					}
				}
			}
		}
		return this;
	}

	public Map<NumericPersonalityTrait, Set<TraitLevel>> getWillPerform() {
		return willPerform;
	}

	@Override
	protected boolean shouldExecute(ServerWorld worldIn, CitizenEntity owner) {
		return this.canExecuteWithPersonality(owner.getPersonality().generateTraitReactionMap());
	}

	public boolean canExecuteWithPersonality(Map<NumericPersonalityTrait, TraitLevel> e) {
		boolean cond = true;
		for (NumericPersonalityTrait trait : e.keySet()) {
			cond = cond && this.willPerform.get(trait).contains(e.get(trait));
			if (!cond)
				break;
		}
		return cond;
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

	public boolean isUrgent(CitizenEntity en) {
		return false;
	}

	public boolean isVisible() {
		return true;
	}

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
