package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.EntityDependentInformationHolder;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Need;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Needs;
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
	private Map<Integer, Set<CitizenTask>> backgroundTasks = Maps.newTreeMap();
	private Map<Integer, Set<CitizenTask>> coreTasks = Maps.newTreeMap();
	private Set<CitizenTask> toExecute = Sets.newHashSet();

	public Autonomy(CitizenEntity entity) {
		super(entity);
	}

	public Autonomy(CitizenEntity en, Dynamic<?> dyn) {
		this(en);
		immediateTasks = dyn.get("persistentTasks").asMap((d1) -> Context.valueOf(d1.asString("")), (d2) -> d2.asStream()
				.<CitizenTask>map((dr) -> IPersistentTask.deserialize(dr)).collect(Collectors.toList()));
		/*backgroundTasks = dyn.get("persistentBGTasks").<Integer, Set<CitizenTask>>asMap((dyn2) -> dyn2.asInt(0),
				(dyn1) -> dyn1.asStream().<CitizenTask>map((dynn) -> IPersistentTask.deserialize(dynn))
						.collect(Collectors.toSet()));*/
		coreTasks = dyn.get("persistentCoreTasks").<Integer, Set<CitizenTask>>asMap((dyn2) -> dyn2.asInt(0),
				(dyn1) -> dyn1.asStream().<CitizenTask>map((dynn) -> IPersistentTask.deserialize(dynn))
						.collect(Collectors.toSet()));
	}

	/**
	 * Register the tasks that run in the background. These are not persistent
	 * 
	 * @param tasks
	 * @return
	 */
	public Autonomy registerBackgroundTasks(Set<Pair<Integer, CitizenTask>> tasks) {
		for (Pair<Integer, CitizenTask> tasque : tasks) {
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

	public Autonomy addTask(int ord, CitizenTask task, boolean makeFirstTask) {
		Pair<Integer, CitizenTask> pair = Pair.of(ord, task);
		if (pair.getSecond().getContexts().contains(Context.BACKGROUND)) {
			this.addBackgroundTask(ord, task);
		} else if (pair.getSecond().getContexts().contains(Context.CORE)) {
			this.addCoreTask(ord, task);
		} else {

			this.addActiveTask(ord, task, makeFirstTask);
		}
		return this;
	}

	public void refreshInactiveTask(CitizenTask task) {
		toExecute.add(task);
	}

	public Autonomy addActiveTask(int order, CitizenTask task, boolean makeFirstTask) {
		for (Context con : task.getContexts()) {
			List<CitizenTask> list = this.immediateTasks.computeIfAbsent(con, (e) -> Lists.newArrayList());
			if (list.isEmpty()) {
				list.add(task);
				toExecute.add(task);
			} else {
				if (makeFirstTask) {
					boolean f = false;
					for (int i = 0; i < list.size(); i++) {
						if (!list.get(i).cannotBeOverriden()) {
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

	public Set<CitizenTask> getBackgroundTasks() {
		return this.backgroundTasks.values().stream().flatMap((e) -> e.stream()).collect(Collectors.toSet());
	}

	public Set<CitizenTask> getCoreTasks() {
		return this.coreTasks.values().stream().flatMap((e) -> e.stream()).collect(Collectors.toSet());
	}

	public Set<CitizenTask> getRunningTasks() {
		Set<CitizenTask> tasks = Sets.newHashSet();
		Needs needs = this.getEntityIn().getNeeds();
		for (NeedType<?> type : needs.getNeedTypes()) {
			Set<Need<?>> ns = needs.getNeeds(type);
			for (Need<?> need : ns) {
				if (!need.isFulfilled()) {
					Set<CitizenTask> fulTasks = need.getFulfillmentTasks(this.getEntityIn());
					fulTasks.forEach((tasq) -> this.addTask(0, tasq, tasq.isUrgent(this.getEntityIn())));
				}
			}
		}
		tasks.addAll(this.immediateTasks.values().stream().flatMap((mapa) -> mapa.stream()).collect(Collectors.toSet()));
		tasks.addAll(this.backgroundTasks.values().stream().flatMap((e) -> e.stream()).collect(Collectors.toSet()));
		tasks.addAll(this.coreTasks.values().stream().flatMap((e) -> e.stream()).collect(Collectors.toSet()));
		tasks.removeIf((t) -> t.getStatus() != Task.Status.RUNNING);
		return tasks;
	}

	public void stopAllTasks(ServerWorld world, CitizenEntity en) {
		this.getRunningTasks().forEach((m) -> m.stop(world, en, world.getGameTime()));
	}

	public Autonomy addBackgroundTask(int ord, CitizenTask task) {
		if (!task.getContexts().contains(Context.BACKGROUND)) {
			throw new IllegalArgumentException("Task " + task + " for " + this.getEntityIn() + " is not a bg task");
		}
		backgroundTasks.computeIfAbsent(ord, (e) -> Sets.newHashSet()).add(task);
		return this;
	}

	public Autonomy addCoreTask(int ord, CitizenTask task) {
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
						ops.createList(entry1.getValue().stream().filter((tasca) -> tasca.isPersistent())
								.map((tasca) -> ((IPersistentTask) tasca).serialize(ops)))))
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		/*T coree = ops.createMap(backgroundTasks.entrySet().stream()
				.map((e) -> Pair.<T, T>of(ops.createInt(e.getKey()),
						ops.createList(e.getValue().stream().map((m) -> ((IPersistentTask) m).serialize(ops)))))
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));*/
		T coreee = ops.createMap(coreTasks.entrySet().stream()
				.map((e) -> Pair.<T, T>of(ops.createInt(e.getKey()),
						ops.createList(e.getValue().stream().map((m) -> ((IPersistentTask) m).serialize(ops)))))
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
				}
			}
		}
	}

}
