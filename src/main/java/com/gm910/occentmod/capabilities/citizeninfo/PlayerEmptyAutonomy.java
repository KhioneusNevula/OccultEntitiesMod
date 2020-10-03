package com.gm910.occentmod.capabilities.citizeninfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceType;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds.ExistAtLocationDeed;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds.SapientDeed;
import com.gm910.occentmod.sapience.mind_and_traits.task.Autonomy;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientAction;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientTask;
import com.google.common.collect.Sets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;

public class PlayerEmptyAutonomy extends Autonomy<PlayerEntity> {

	private Map<SapientDeed, Integer> activeDeeds = new HashMap<>();

	public PlayerEmptyAutonomy(PlayerEntity entity) {
		super(entity);
	}

	public Map<SapientDeed, Integer> getActiveDeedsMap() {
		return activeDeeds;
	}

	@Override
	public <T extends LivingEntity> void react(SapientAction<T> action, T doer) {
	}

	@Override
	public void reaction(Occurrence occur, Set<SapientTask<? super PlayerEntity>> event) {
	}

	@Override
	public Set<SapientDeed> getActiveDeeds(LivingEntity visibleTo) {
		return this.getActiveDeedsMap().keySet();
	}

	@Override
	public void observe() {
	}

	@Override
	public void tick() {
		System.out.println("Player autonomy tick");
		for (SapientDeed deed : new HashSet<>(this.activeDeeds.keySet())) {
			if (activeDeeds.get(deed) <= 0) {
				activeDeeds.remove(deed);
			} else {
				activeDeeds.put(deed, activeDeeds.get(deed) - 1);
			}
		}
		boolean addloc = true;
		Set<ExistAtLocationDeed> locdeeds = this.activeDeeds.keySet().stream()
				.filter((m) -> m.getType() != OccurrenceType.EXIST_AT_LOCATION).map((d) -> (ExistAtLocationDeed) d)
				.collect(Collectors.toSet());
		if (locdeeds.stream().findFirst().isPresent()) {
			if (locdeeds.stream().findFirst().get().getLocation().equals(this.getEntityIn().getPosition())) {
				addloc = false;
			}
		}
		if (addloc) {
			(new HashMap<>(activeDeeds)).forEach((deed, time) -> {
				if (deed.getType() == OccurrenceType.EXIST_AT_LOCATION) {
					activeDeeds.remove(deed);
				}
			});
			activeDeeds.put(new ExistAtLocationDeed(this.getInfo().getIdentity(), new ServerPos(this.getEntityIn())),
					10);

		}
	}

	@Override
	public void startTasks(ServerWorld world, PlayerEntity en, long time) {
	}

	@Override
	public void tickNeedTasks(ServerWorld world, PlayerEntity entity, long time) {
	}

	@Override
	public void tickImportantTasks(ServerWorld world, PlayerEntity en, long time) {
	}

	@Override
	public void stopAllTasks(ServerWorld world, PlayerEntity en) {
	}

	@Override
	public void reactToEvent(Occurrence event) {
	}

	@Override
	public void refreshInactiveTask(SapientTask<? super PlayerEntity> task) {
	}

	@Override
	public Set<Task<? super PlayerEntity>> getRunningTasks() {
		return Sets.newHashSet();
	}

	@Override
	public Set<SapientTask<? super PlayerEntity>> getImmediateTasks() {
		return Sets.newHashSet();
	}

	@Override
	public Set<Task<? super PlayerEntity>> getBackgroundTasks() {
		return Sets.newHashSet();
	}

	@Override
	public Set<SapientTask<? super PlayerEntity>> getCoreTasks() {
		return Sets.newHashSet();
	}
}