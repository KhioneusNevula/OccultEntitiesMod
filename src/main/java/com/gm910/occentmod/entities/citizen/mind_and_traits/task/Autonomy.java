package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.capabilities.citizeninfo.CitizenInfo;
import com.gm910.occentmod.entities.citizen.mind_and_traits.EntityDependentInformationHolder;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories.CauseEffectMemory;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories.CauseEffectMemory.Certainty;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories.MemoryOfDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories.MemoryOfOccurrence;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Need;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Needs;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceData;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceEffect;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceEffect.Connotation;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.deeds.CitizenDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.PersonalityTrait;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.PersonalityTrait.TraitLevel;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.Relationships;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.CitizenTask.Context;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.Task.Status;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;

public class Autonomy<E extends LivingEntity> extends EntityDependentInformationHolder<E> {

	/**
	 * <order, map<context, task, priority>>
	 */
	private Map<Context, List<CitizenTask<? super E>>> immediateTasks = Maps.newTreeMap();
	private Map<Integer, Set<Task<? super E>>> backgroundTasks = Maps.newTreeMap();
	private Map<Integer, Set<CitizenTask<? super E>>> coreTasks = Maps.newTreeMap();
	private Set<CitizenTask<? super E>> toExecute = Sets.newHashSet();

	public Autonomy(E entity) {
		super(entity);
	}

	public Autonomy(E en, Dynamic<?> dyn) {
		this(en);
		immediateTasks = dyn.get("persistentTasks").asMap((d1) -> Context.valueOf(d1.asString("")), (d2) -> d2
				.asStream().<CitizenTask<? super E>>map((dr) -> TaskType.deserialize(dr)).collect(Collectors.toList()));
		/*backgroundTasks = dyn.get("persistentBGTasks").<Integer, Set<CitizenTask<? super E>>>asMap((dyn2) -> dyn2.asInt(0),
				(dyn1) -> dyn1.asStream().<CitizenTask<? super E>>map((dynn) -> IPersistentTask.deserialize(dynn))
						.collect(Collectors.toSet()));*/
		coreTasks = dyn.get("persistentCoreTasks").<Integer, Set<CitizenTask<? super E>>>asMap((dyn2) -> dyn2.asInt(0),
				(dyn1) -> dyn1.asStream().<CitizenTask<? super E>>map((dynn) -> TaskType.deserialize(dynn))
						.collect(Collectors.toSet()));
	}

	/**
	 * Register the tasks that run in the background. These are not persistent
	 * 
	 * @param tasks
	 * @return
	 */
	public Autonomy<E> registerBackgroundTasks(Set<Pair<Integer, Task<? super E>>> tasks) {
		for (Pair<Integer, Task<? super E>> tasque : tasks) {
			this.addBackgroundTask(tasque.getFirst(), tasque.getSecond());
		}
		return this;
	}

	/**
	 * 
	 * @param tasks
	 * @return
	 */
	public Autonomy<E> addTasks(Set<Pair<Integer, CitizenTask<? super E>>> tasks) {

		for (Pair<Integer, CitizenTask<? super E>> pair : tasks) {
			if (pair.getSecond().getContexts().contains(Context.BACKGROUND)) {
				this.backgroundTasks.computeIfAbsent(pair.getFirst(), (e) -> Sets.newHashSet()).add(pair.getSecond());
				toExecute.add(pair.getSecond());
				continue;
			} else if (pair.getSecond().getContexts().contains(Context.CORE)) {
				this.coreTasks.computeIfAbsent(pair.getFirst(), (e) -> Sets.newHashSet()).add(pair.getSecond());
				toExecute.add(pair.getSecond());
				continue;
			}

		}

		List<Pair<Integer, CitizenTask<? super E>>> sorted = new ArrayList<>(
				tasks.stream().filter((p) -> !p.getSecond().getContexts().contains(Context.BACKGROUND)
						&& !p.getSecond().getContexts().contains(Context.CORE)).collect(Collectors.toSet()));
		sorted.sort((t1, t2) -> t1.getFirst().compareTo(t2.getFirst()));
		for (Pair<Integer, CitizenTask<? super E>> pair : sorted) {
			for (Context cont : pair.getSecond().getContexts()) {
				immediateTasks.computeIfAbsent(cont, (e) -> Lists.newArrayList()).add(pair.getSecond());
				toExecute.add(pair.getSecond());
			}
		}
		return this;
	}

	/**
	 * Takes into account previous experiences, personality, and skill in order to
	 * consider adding a certain task
	 * 
	 * @param ord
	 * @param task
	 * @param makeFirstTask
	 * @return
	 */
	public boolean considerTask(int ord, CitizenTask<? super E> task, boolean makeFirstTask) {
		if (!task.canExecute(this.getEntityIn())) {
			return false;
		}
		E enna = this.getEntityIn();
		CitizenInfo<E> en = CitizenInfo.get(enna).orElse(null);
		CitizenDeed d = task.getDeed(en.getIdentity());
		float sympathy = en.getPersonality().getTrait(PersonalityTrait.SYMPATHY);
		float selfishness = en.getPersonality().getTrait(PersonalityTrait.SELFISHNESS);
		float kindness = -en.getPersonality().getTrait(PersonalityTrait.SADISM);
		float activeness = en.getPersonality().getTrait(PersonalityTrait.ACTIVENESS);
		float chance = makeFirstTask ? 1 : (activeness + 1) / 2;
		if (d != null) {
			Set<CauseEffectMemory<? super E>> theories = en.getKnowledge()
					.getByPredicate((e) -> e instanceof CauseEffectMemory);
			theories.forEach((e) -> e.access());

			for (CauseEffectMemory<? super E> theory : theories) {
				if (theory.getCause().isSimilarTo(d)) {
					Occurrence effect = theory.getEffect();
					OccurrenceEffect con = effect.getEffect();
					Connotation selfCon = con.getEffect(en.getIdentity());
					if (selfCon == Connotation.FATAL && selfishness >= 0) {
						return false;
					}
					chance += (1 - chance) * (selfCon.getValue() / Connotation.MAX);
					for (CitizenIdentity id : con.getAffected()) {
						float like = en.getRelationships().getLikeValue(id) * 2 / Relationships.MAX_LIKE_VALUE;
						float pleasureAtNegative = like * kindness;
						float careFor = (((kindness * sympathy) - selfishness));
						Connotation citcon = con.getEffect(id);
						int conlevel = citcon.getValue();
						if (like * 0.75 <= careFor) {
							chance += (1 - chance) * (conlevel * pleasureAtNegative);
						} else {
							continue;
						}
					}
				}
			}
		}
		float rand = this.getEntityIn().getRNG().nextFloat();
		if (rand <= chance) {
			this.forceAddTask(ord, task, makeFirstTask);
			return true;
		}
		return false;
	}

	/**
	 * Forces citizen to add this task to their queue
	 * 
	 * @param ord
	 * @param task
	 * @param makeFirstTask
	 * @return
	 */
	public Autonomy<E> forceAddTask(int ord, CitizenTask<? super E> task, boolean makeFirstTask) {
		Pair<Integer, CitizenTask<? super E>> pair = Pair.of(ord, task);
		if (pair.getSecond().getContexts().contains(Context.BACKGROUND)) {
			this.addBackgroundTask(ord, task);
		} else if (pair.getSecond().getContexts().contains(Context.CORE)) {
			this.addCoreTask(ord, task);
		} else {

			this.addActiveTask(task, makeFirstTask);
		}
		return this;
	}

	public void refreshInactiveTask(CitizenTask<? super E> task) {
		toExecute.add(task);
	}

	private Autonomy<E> addActiveTask(CitizenTask<? super E> task, boolean makeFirstTask) {
		for (Context con : task.getContexts()) {
			List<CitizenTask<? super E>> list = this.immediateTasks.computeIfAbsent(con, (e) -> Lists.newArrayList());
			if (list.isEmpty()) {
				list.add(task);
				toExecute.add(task);
			} else {
				if (makeFirstTask) {
					boolean f = false;
					for (int i = 0; i < list.size(); i++) {
						if (!list.get(i).cannotBeOverriden(this.getEntityIn())) {
							list.add(i, task);
							toExecute.add(task);
							f = true;
							continue;
						}
					}
					if (!f) {
						list.add(task);
						toExecute.add(task);
					}
				} else {
					list.add(task);
					toExecute.add(task);
				}
			}
		}
		return this;
	}

	public Set<CitizenTask<? super E>> getImmediateTasks() {
		return this.immediateTasks.values().stream().flatMap((mapa) -> mapa.stream()).collect(Collectors.toSet());
	}

	public Set<Task<? super E>> getBackgroundTasks() {
		return this.backgroundTasks.values().stream().flatMap((e) -> e.stream()).collect(Collectors.toSet());
	}

	public Set<CitizenTask<? super E>> getCoreTasks() {
		return this.coreTasks.values().stream().flatMap((e) -> e.stream()).collect(Collectors.toSet());
	}

	public Set<Task<? super E>> getRunningTasks() {
		Set<Task<? super E>> tasks = Sets.newHashSet();

		tasks.addAll(
				this.immediateTasks.values().stream().flatMap((mapa) -> mapa.stream()).collect(Collectors.toSet()));
		tasks.addAll(this.backgroundTasks.values().stream().flatMap((e) -> e.stream()).collect(Collectors.toSet()));
		tasks.addAll(this.coreTasks.values().stream().flatMap((e) -> e.stream()).collect(Collectors.toSet()));
		tasks.removeIf((t) -> t.getStatus() != Task.Status.RUNNING && !toExecute.contains(t));
		return tasks;
	}

	public void stopAllTasks(ServerWorld world, E en) {
		this.getRunningTasks().forEach((m) -> m.stop(world, en, world.getGameTime()));
	}

	public Autonomy<E> addBackgroundTask(int ord, Task<? super E> task) {
		if (ModReflect.<CitizenTask<? super E>>instanceOf(task, null)
				&& !((CitizenTask<? super E>) task).getContexts().contains(Context.BACKGROUND)) {
			throw new IllegalArgumentException("Task " + task + " for " + this.getEntityIn() + " is not a bg task");
		}
		backgroundTasks.computeIfAbsent(ord, (e) -> Sets.newHashSet()).add(task);
		return this;
	}

	private Autonomy<E> addCoreTask(int ord, CitizenTask<? super E> task) {
		if (!task.getContexts().contains(Context.CORE)) {
			throw new IllegalArgumentException("Task " + task + " for " + this.getEntityIn() + " is not a core task");
		}
		coreTasks.computeIfAbsent(ord, (e) -> Sets.newHashSet()).add(task);
		toExecute.add(task);
		return this;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T persTasks = ops.createMap(immediateTasks.entrySet().stream()
				.map((entry1) -> Pair.of(ops.createString(entry1.getKey().name()),
						ops.createList(entry1.getValue().stream().map((tasca) -> tasca.serialize(ops)))))
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		/*T coree = ops.createMap(backgroundTasks.entrySet().stream()
				.map((e) -> Pair.<T, T>of(ops.createInt(e.getKey()),
						ops.createList(e.getValue().stream().map((m) -> ((IPersistentTask) m).serialize(ops)))))
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));*/
		T coreee = ops.createMap(coreTasks.entrySet().stream()
				.map((e) -> Pair.<T, T>of(ops.createInt(e.getKey()),
						ops.createList(e.getValue().stream().map((m) -> m.serialize(ops)))))
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		return ops.createMap(ImmutableMap.of(ops.createString("persistentTasks"), persTasks,
				/*ops.createString("persistentBGTasks"), coree,*/ ops.createString("persistentCoreTasks"), coreee));
	}

	@Override
	public void tick() {
		ServerWorld world = (ServerWorld) this.getEntityIn().world;
		E entity = this.getEntityIn();
		long time = world.getGameTime();
		this.startTasks(world, entity, time);
		this.getBackgroundTasks().forEach((e) -> e.tick(world, entity, time));
		this.getCoreTasks().forEach((e) -> e.tick(world, entity, time));
		this.tickImportantTasks(world, entity, time);
		this.tickNeedTasks(world, entity, time);
		if (this.getEntityIn().getBrain().hasMemory(MemoryModuleType.VISIBLE_MOBS)) {
			observe();
		}
		super.tick();
	}

	public void tickNeedTasks(ServerWorld world, E entity, long time) {
		Needs<E> needs = CitizenInfo.<E>get(this.getEntityIn()).orElse(null).getNeeds();

		for (NeedType<E, ?> type : needs.getNeedTypes()) {
			Set<Need<E, ?>> ns = needs.getNeeds(type);
			for (Need<E, ?> need : ns) {
				if (!need.isFulfilled()) {
					Set<CitizenTask<E>> fulTasks = need.getFulfillmentTasks(this.getEntityIn());
					fulTasks.forEach((tasq) -> this.considerTask(0, tasq,
							need.isInDanger() || tasq.isUrgent(this.getEntityIn())));
				}
			}
		}
	}

	public void observe() {

		List<LivingEntity> vis = this.getEntityIn().getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS)
				.orElse(Lists.newArrayList());
		if (!vis.isEmpty()) {

			for (LivingEntity citi : vis) {
				LazyOptional<CitizenInfo<LivingEntity>> citizenO = CitizenInfo.get(citi);
				if (!citizenO.isPresent())
					continue;
				CitizenInfo<? extends LivingEntity> citizen = citizenO.orElse(null);
				Set<CitizenTask<? extends LivingEntity>> tasks = citizen.getAutonomy().getRunningTasks().stream()
						.filter((e) -> e instanceof CitizenTask
								&& ((CitizenTask) e).isVisible(citizen.$getOwner(), (LivingEntity) this.getEntityIn()))
						.map((a) -> (CitizenTask<? extends LivingEntity>) a).collect(Collectors.toSet());
				for (CitizenTask<? extends LivingEntity> task : tasks) {
					CitizenDeed deed = task.getDeed(citizen.getIdentity());
					if (deed == null)
						continue;
					this.reactToEvent(deed);
				}
			}
		}

		OccurrenceData occurrences = OccurrenceData.get((ServerWorld) this.getEntityIn().world);

		for (Occurrence occ : occurrences.getVisible(this.getEntityIn())) {
			this.reactToEvent(occ);
		}

	}

	public void tickImportantTasks(ServerWorld world, E en, long time) {
		for (Context c : this.immediateTasks.keySet()) {
			List<CitizenTask<? super E>> tasks = immediateTasks.computeIfAbsent(c, (e) -> Lists.newArrayList());
			if (tasks.isEmpty())
				continue;
			tasks.get(0).tick(world, en, time);
		}
	}

	public void startTasks(ServerWorld world, E en, long time) {
		for (int ts : this.coreTasks.keySet()) {
			coreTasks.get(ts).removeIf((e) -> e.getStatus() != Status.RUNNING && !toExecute.contains(e));
			coreTasks.get(ts).forEach((e) -> {
				if (toExecute.contains(e)) {
					toExecute.remove(e);
					e.start(world, en, time);
				}
			});
		}
		for (Context c : this.immediateTasks.keySet()) {
			List<CitizenTask<? super E>> tasks = immediateTasks.computeIfAbsent(c, (e) -> Lists.newArrayList());
			if (tasks.isEmpty())
				continue;
			if (toExecute.contains(tasks.get(0))) {
				tasks.get(0).start(world, en, time);
				toExecute.remove(tasks.get(0));
			} else {
				if (tasks.get(0).getStatus() == Status.STOPPED) {
					CitizenTask<? super E> t = tasks.remove(0);
					CitizenDeed d = t.getDeed(CitizenInfo.get(this.getEntityIn()).orElse(null).getIdentity());
					if (d != null) {
						CitizenInfo.get(this.getEntityIn()).orElse(null).getKnowledge()
								.receiveKnowledge(new MemoryOfDeed(this.getEntityIn(), d));
					}
				}
			}
		}
	}

	/**
	 * Reacts to an action done by the second parameter citizen entity; this action
	 * is assumed to be a CitizenTask
	 * 
	 * @param action
	 * @param doer
	 */
	public <T extends LivingEntity> void react(CitizenAction<T> action, T doer) {
		Set<CitizenTask<? super E>> mapa = action.getPotentialReactions().stream()
				.filter((m) -> ModReflect.<CitizenTask<? super E>>instanceOf(m, null))
				.map((m) -> (CitizenTask<? super E>) m).collect(Collectors.toSet());
		CitizenDeed deed = ((CitizenTask<? super E>) action).getDeed(CitizenInfo.get(doer).orElse(null).getIdentity());
		this.reaction(deed, mapa);
		CitizenInfo.get(this.getEntityIn()).orElse(null).getKnowledge()
				.receiveKnowledge(new MemoryOfDeed<>(this.getEntityIn(), deed));

	}

	/**
	 * Performs a generic reaction by adding all tasks in the set to the execution
	 * list of the autonomy controller
	 * 
	 * @param event
	 */
	public void reaction(Occurrence occur, Set<CitizenTask<? super E>> event) {
		Map<PersonalityTrait, TraitLevel> trets = CitizenInfo.get(this.getEntityIn()).orElse(null).getPersonality()
				.generateTraitReactionMap();
		for (CitizenTask<? super E> tasque : event) {
			if (tasque.canExecute(this.getEntityIn())) {

				this.considerTask(tasque.isUrgent(this.getEntityIn()) ? 0 : getImmediateTasks().size(), tasque,
						tasque.isUrgent(this.getEntityIn()));
			}
		}
	}

	/**
	 * Reacts to a generic event occurring in the world
	 * 
	 * @param event
	 */
	public void reactToEvent(Occurrence event) {
		Set<CitizenTask<? super E>> tasques = event.getPotentialWitnessReactions();
		this.reaction(event, tasques);
		CitizenInfo<E> info = CitizenInfo.get(this.getEntityIn()).orElse(null);
		MemoryOfOccurrence<E> meme = new MemoryOfOccurrence<>(this.getEntityIn(), event);
		Set<MemoryOfOccurrence<? super E>> occs = info.getKnowledge().<MemoryOfOccurrence<? super E>>getByPredicate(
				(e) -> ModReflect.<MemoryOfOccurrence<? super E>>instanceOf(e, null));
		occs.forEach((o) -> o.access());
		for (MemoryOfOccurrence<? super E> occ : occs) {

			if (occ.couldEventBeCauseOf(meme)) {
				CauseEffectMemory<E> theo = new CauseEffectMemory<E>(this.getEntityIn(), occ.getEvent(), event, null);
				Set<CauseEffectMemory<? super E>> theors = info.getKnowledge()
						.getByPredicate((e) -> ModReflect.<CauseEffectMemory<? super E>>instanceOf(e, null)
								&& ((CauseEffectMemory<? super E>) e).fitsObservation(theo.getCause(),
										theo.getEffect()));
				if (!theors.isEmpty()) {
					for (CauseEffectMemory<? super E> t : theors) {
						if (t.getCertainty() != Certainty.TRUE) {
							t.incrementObservation(1);
							t.getEffect().getEffect().getEffects().putAll(event.getEffect().getEffects());
						}
					}
				} else {
					info.getKnowledge().addKnowledge(theo);
				}
			}
		}
		info.getKnowledge().receiveKnowledge(meme);

	}

}
