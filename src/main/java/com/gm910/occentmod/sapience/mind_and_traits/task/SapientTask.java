package com.gm910.occentmod.sapience.mind_and_traits.task;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.api.util.NonNullMap;
import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds.SapientDeed;
import com.gm910.occentmod.sapience.mind_and_traits.personality.PersonalityTrait;
import com.gm910.occentmod.sapience.mind_and_traits.personality.PersonalityTrait.TraitLevel;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.gm910.occentmod.sapience.mind_and_traits.skills.SkillType;
import com.gm910.occentmod.sapience.mind_and_traits.skills.SkillType.Applicability;
import com.gm910.occentmod.sapience.mind_and_traits.skills.Skills;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.world.server.ServerWorld;

public abstract class SapientTask<Doer extends LivingEntity> extends Task<Doer> implements IDynamicSerializable {

	private Set<Context> contexts = Sets.newHashSet();

	private Map<MemoryModuleType<?>, MemoryModuleStatus> delegateMemoryMap = new HashMap<>();

	private Object2IntMap<SkillType> skill = new Object2IntOpenHashMap<>();

	private Map<PersonalityTrait, Set<TraitLevel>> willPerform = new NonNullMap<>(() -> Sets.newHashSet());

	private Class<Doer> doerType;

	public SapientTask(Class<Doer> doerClass, Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn,
			int durationMinIn, int durationMaxIn) {
		super(requiredMemoryStateIn, durationMinIn, durationMaxIn);
		this.delegateMemoryMap.putAll(requiredMemoryStateIn);
		this.doerType = doerClass;
	}

	public SapientTask(Class<Doer> doerClass, Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn) {
		this(doerClass, requiredMemoryStateIn, 60);
	}

	public SapientTask(Class<Doer> doerClass, Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn,
			int duration) {
		this(doerClass, requiredMemoryStateIn, duration, duration);
	}

	public Class<Doer> getDoerType() {
		return doerType;
	}

	public SapientTask<Doer> setContext(Context... contexts) {
		this.contexts = Sets.newHashSet(contexts);
		return this;
	}

	public Map<MemoryModuleType<?>, MemoryModuleStatus> getDelegateMemoryMap() {
		return delegateMemoryMap;
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
	public SapientTask<Doer> setPersonalityConditions(boolean willPerform,
			Pair<PersonalityTrait, Collection<TraitLevel>>... pairs) {
		if (willPerform) {
			for (Pair<PersonalityTrait, Collection<TraitLevel>> pair : pairs) {
				this.willPerform.get(pair.getFirst()).addAll(pair.getSecond());
			}
		} else {
			Set<Pair<PersonalityTrait, Collection<TraitLevel>>> pairsSet = Sets.newHashSet(pairs);
			for (PersonalityTrait trait : PersonalityTrait.values()) {
				for (TraitLevel level : TraitLevel.values()) {
					if (!pairsSet.stream().anyMatch((p) -> p.getFirst() == trait && p.getSecond().contains(level))) {
						this.willPerform.get(trait).add(level);
					}
				}
			}
		}
		return this;
	}

	public SapientTask<Doer> setSkillLevels(Pair<SkillType, Integer>... pairs) {
		for (Pair<SkillType, Integer> pair : pairs) {
			this.skill.put(pair.getFirst(), pair.getSecond() == null ? 0 : pair.getSecond());
		}

		return this;
	}

	/**
	 * Initializes the information that will create the task's deed
	 */
	public void preExecution(ServerWorld world, Doer owner) {
		this.shouldExecute(world, owner);
	}

	public boolean isTimedOut(long gameTime) {
		return super.isTimedOut(gameTime) && !isIndefinite();
	}

	/**
	 * Whether this task continues running indefinitely even after it has "timed
	 * out" by regular game logic
	 */
	public boolean isIndefinite() {
		return false;
	}

	public Map<PersonalityTrait, Set<TraitLevel>> getWillPerform() {
		return willPerform;
	}

	@Override
	public boolean shouldExecute(ServerWorld worldIn, Doer owner) {
		return true;
	}

	public boolean hasNecessarySkills(Skills skills) {
		for (SkillType skill : SkillType.getByApplicability(Applicability.BOTH, Applicability.CITIZEN_ONLY)) {
			if (this.skill.getInt(skill) > skills.getSkill(skill)) {
				return false;
			}
		}
		return true;
	}

	private boolean hasRequiredMemories(Doer owner) {
		return this.delegateMemoryMap.entrySet().stream().allMatch((p_220379_1_) -> {
			MemoryModuleType<?> memorymoduletype = p_220379_1_.getKey();
			MemoryModuleStatus memorymodulestatus = p_220379_1_.getValue();
			return owner.getBrain().hasMemory(memorymoduletype, memorymodulestatus);
		});
	}

	public boolean canExecute(Doer owner) {
		return this.canExecuteWithPersonality(SapientInfo.get(owner).getPersonality().generateTraitReactionMap())
				&& hasNecessarySkills(SapientInfo.get(owner).getSkills()) && hasRequiredMemories(owner);
	}

	public boolean canExecuteWithPersonality(Map<PersonalityTrait, TraitLevel> e) {
		boolean cond = true;
		for (PersonalityTrait trait : e.keySet()) {
			cond = cond && this.willPerform.get(trait).contains(e.get(trait));
			if (!cond)
				break;
		}
		return cond;
	}

	public Set<TraitLevel> getNecessaryPersonalityTrait(PersonalityTrait trata) {
		return this.willPerform.getOrDefault(trata, new HashSet<>());
	}

	public SapientTask<Doer> addContext(Context... contexts) {
		this.contexts.addAll(Sets.newHashSet(contexts));
		return this;
	}

	public Set<Context> getContexts() {
		return contexts;
	}

	@Nullable
	public abstract SapientDeed getDeed(SapientIdentity doer);

	/**
	 * Whether this task is required to happen immediately
	 * 
	 * @param en
	 * @return
	 */
	public Necessity getNecessity(Doer en) {
		return Necessity.PREFERABLE;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + " " + ToStringBuilder.reflectionToString(this);
	}

	/**
	 * Return whether the task is visible to seer when done by doer
	 * 
	 * @param doer
	 * @param seer
	 * @return
	 */
	public boolean isVisible(Doer doer, LivingEntity seer) {
		return true;
	}

	public abstract TaskType<Doer, ? extends SapientTask<Doer>> getType();

	public SapientTask<Doer> copy(Autonomy<Doer> aut) {
		if (getType() == null)
			return null;
		return this.getType().runDeserialize(aut, GMNBT.makeDynamic(this.writeData(NBTDynamicOps.INSTANCE)));
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		SapientTask<Doer> task = (SapientTask<Doer>) this;
		if (task.getType() == null)
			return ops.emptyMap();
		T type = ops.createString(getType().rl.toString());
		T data = writeData(ops);
		T cons = ops.createList(task.getContexts().stream().map((m) -> ops.createString(m.toString())));
		T stat = ops.createBoolean(task.getStatus() == Task.Status.RUNNING);
		T time = ops.createLong(ModReflect.getField(Task.class, long.class, "status", "field_220385_b", task));
		return ops.createMap(ImmutableMap.of(ops.createString("type"), type, ops.createString("data"), data,
				ops.createString("contexts"), cons, ops.createString("running"), stat, ops.createString("time"), time));
	}

	public <T> T writeData(DynamicOps<T> ops) {
		return ops.createBoolean(false);
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
