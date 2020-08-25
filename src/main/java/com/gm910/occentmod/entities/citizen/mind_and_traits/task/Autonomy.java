package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.EntityDependentInformationHolder;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories.CauseEffectMemory;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories.MemoryOfDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Needs;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Need;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceEffect;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceEffect.Connotation;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.deeds.CitizenDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.PersonalityTrait;
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

import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.Task.Status;
import net.minecraft.world.server.ServerWorld;

public class Autonomy extends EntityDependentInformationHolder<CitizenEntity> {

	/**
	 * <order, map<context, task, priority>>
	 */
	private Map<Context, List<CitizenTask>> immediateTasks = Maps.newTreeMap();
	private Map<Integer, Set<Task<? super CitizenEntity>>> backgroundTasks = Maps.newTreeMap();
	private Map<Integer, Set<CitizenTask>> coreTasks = Maps.newTreeMap();
	private Set<CitizenTask> toExecute = Sets.newHashSet();

	public Autonomy(CitizenEntity entity) {
		super(entity);
	}

	public Autonomy(CitizenEntity en, Dynamic<?> dyn) {
		this(en);
		immediateTasks = dyn.get("persistentTasks").asMap((d1) -> Context.valueOf(d1.asString("")),
				(d2) -> d2.asStream().<CitizenTask>map((dr) -> TaskType.deserialize(dr)).collect(Collectors.toList()));
		/*backgroundTasks = dyn.get("persistentBGTasks").<Integer, Set<CitizenTask>>asMap((dyn2) -> dyn2.asInt(0),
				(dyn1) -> dyn1.asStream().<CitizenTask>map((dynn) -> IPersistentTask.deserialize(dynn))
						.collect(Collectors.toSet()));*/
		coreTasks = dyn.get("persistentCoreTasks").<Integer, Set<CitizenTask>>asMap((dyn2) -> dyn2.asInt(0),
				(dyn1) -> dyn1.asStream().<CitizenTask>map((dynn) -> TaskType.deserialize(dynn))
						.collect(Collectors.toSet()));
	}

	/**
	 * Register the tasks that run in the background. These are not persistent
	 * 
	 * @param tasks
	 * @return
	 */
	public Autonomy registerBackgroundTasks(Set<Pair<Integer, Task<? super CitizenEntity>>> tasks) {
		for (Pair<Integer, Task<? super CitizenEntity>> tasque : tasks) {
			this.addBackgroundTask(tasque.getFirst(), tasque.getSecond());
		}
		return this;
	}

	/**
	 * 
	 * @param tasks
	 * @return
	 */
	public Autonomy addTasks(Set<Pair<Integer, CitizenTask>> tasks) {

		for (Pair<Integer, CitizenTask> pair : tasks) {
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

		List<Pair<Integer, CitizenTask>> sorted = new ArrayList<>(
				tasks.stream().filter((p) -> !p.getSecond().getContexts().contains(Context.BACKGROUND)
						&& !p.getSecond().getContexts().contains(Context.CORE)).collect(Collectors.toSet()));
		sorted.sort((t1, t2) -> t1.getFirst().compareTo(t2.getFirst()));
		for (Pair<Integer, CitizenTask> pair : sorted) {
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
	public boolean considerTask(int ord, CitizenTask task, boolean makeFirstTask) {
		if (!task.canExecute(this.getEntityIn())) {
			return false;
		}
		CitizenEntity en = this.getEntityIn();

		CitizenDeed d = task.getDeed(en.getIdentity());
		float sympathy = en.getPersonality().getTrait(PersonalityTrait.SYMPATHY);
		float selfishness = en.getPersonality().getTrait(PersonalityTrait.SELFISHNESS);
		float kindness = -en.getPersonality().getTrait(PersonalityTrait.SADISM);
		float activeness = en.getPersonality().getTrait(PersonalityTrait.ACTIVENESS);
		float chance = makeFirstTask ? 1 : (activeness + 1) / 2;
		if (d != null) {
			Set<CauseEffectMemory> theories = en.getKnowledge().getByPredicate((e) -> e instanceof CauseEffectMemory);
			theories.forEach((e) -> e.access());

			for (CauseEffectMemory theory : theories) {
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
	public Autonomy forceAddTask(int ord, CitizenTask task, boolean makeFirstTask) {
		Pair<Integer, CitizenTask> pair = Pair.of(ord, task);
		if (pair.getSecond().getContexts().contains(Context.BACKGROUND)) {
			this.addBackgroundTask(ord, task);
		} else if (pair.getSecond().getContexts().contains(Context.CORE)) {
			this.addCoreTask(ord, task);
		} else {

			this.addActiveTask(task, makeFirstTask);
		}
		return this;
	}

	public void refreshInactiveTask(CitizenTask task) {
		toExecute.add(task);
	}

	private Autonomy addActiveTask(CitizenTask task, boolean makeFirstTask) {
		for (Context con : task.getContexts()) {
			List<CitizenTask> list = this.immediateTasks.computeIfAbsent(con, (e) -> Lists.newArrayList());
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

	public Set<CitizenTask> getImmediateTasks() {
		return this.immediateTasks.values().stream().flatMap((mapa) -> mapa.stream()).collect(Collectors.toSet());
	}

	public Set<Task<? super CitizenEntity>> getBackgroundTasks() {
		return this.backgroundTasks.values().stream().flatMap((e) -> e.stream()).collect(Collectors.toSet());
	}

	public Set<CitizenTask> getCoreTasks() {
		return this.coreTasks.values().stream().flatMap((e) -> e.stream()).collect(Collectors.toSet());
	}

	public Set<Task<? super CitizenEntity>> getRunningTasks() {
		Set<Task<? super CitizenEntity>> tasks = Sets.newHashSet();
		Needs needs = this.getEntityIn().getNeeds();
		for (NeedType<?> type : needs.getNeedTypes()) {
			Set<Need<?>> ns = needs.getNeeds(type);
			for (Need<?> need : ns) {
				if (!need.isFulfilled()) {
					Set<CitizenTask> fulTasks = need.getFulfillmentTasks(this.getEntityIn());
					fulTasks.forEach((tasq) -> this.considerTask(0, tasq,
							need.isInDanger() || tasq.isUrgent(this.getEntityIn())));
				}
			}
		}
		tasks.addAll(
				this.immediateTasks.values().stream().flatMap((mapa) -> mapa.stream()).collect(Collectors.toSet()));
		tasks.addAll(this.backgroundTasks.values().stream().flatMap((e) -> e.stream()).collect(Collectors.toSet()));
		tasks.addAll(this.coreTasks.values().stream().flatMap((e) -> e.stream()).collect(Collectors.toSet()));
		tasks.removeIf((t) -> t.getStatus() != Task.Status.RUNNING && !toExecute.contains(t));
		return tasks;
	}

	public void stopAllTasks(ServerWorld world, CitizenEntity en) {
		this.getRunningTasks().forEach((m) -> m.stop(world, en, world.getGameTime()));
	}

	public Autonomy addBackgroundTask(int ord, Task<? super CitizenEntity> task) {
		if (task instanceof CitizenTask && !((CitizenTask) task).getContexts().contains(Context.BACKGROUND)) {
			throw new IllegalArgumentException("Task " + task + " for " + this.getEntityIn() + " is not a bg task");
		}
		backgroundTasks.computeIfAbsent(ord, (e) -> Sets.newHashSet()).add(task);
		return this;
	}

	private Autonomy addCoreTask(int ord, CitizenTask task) {
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
		CitizenEntity entity = this.getEntityIn();
		long time = world.getGameTime();
		this.startTasks(world, entity, time);
		this.getBackgroundTasks().forEach((e) -> e.tick(world, entity, time));
		this.getCoreTasks().forEach((e) -> e.tick(world, entity, time));
		this.tickImportantTasks(world, entity, time);
		super.tick();
	}

	public void tickImportantTasks(ServerWorld world, CitizenEntity en, long time) {
		for (Context c : this.immediateTasks.keySet()) {
			List<CitizenTask> tasks = immediateTasks.computeIfAbsent(c, (e) -> Lists.newArrayList());
			if (tasks.isEmpty())
				continue;
			tasks.get(0).tick(world, en, time);
		}
	}

	public void startTasks(ServerWorld world, CitizenEntity en, long time) {
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
			List<CitizenTask> tasks = immediateTasks.computeIfAbsent(c, (e) -> Lists.newArrayList());
			if (tasks.isEmpty())
				continue;
			if (toExecute.contains(tasks.get(0))) {
				tasks.get(0).start(world, en, time);
				toExecute.remove(tasks.get(0));
			} else {
				if (tasks.get(0).getStatus() == Status.STOPPED) {
					CitizenTask t = tasks.remove(0);
					CitizenDeed d = t.getDeed(this.getEntityIn().getIdentity());
					if (d != null) {
						this.getEntityIn().getKnowledge().receiveKnowledge(new MemoryOfDeed(this.getEntityIn(), d));
					}
				}
			}
		}
	}

}
