package com.gm910.occentmod.sapience.mind_and_traits.task.background;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.init.GMDeserialize;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.CauseEffectMemory;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.CauseEffectMemory.Certainty;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.MemoryOfDeed;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.MemoryOfOccurrence;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceEffect.Connotation;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceType;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds.ExistAtLocationDeed;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds.SapientDeed;
import com.gm910.occentmod.sapience.mind_and_traits.personality.PersonalityTrait;
import com.gm910.occentmod.sapience.mind_and_traits.personality.PersonalityTrait.TraitLevel;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.gm910.occentmod.sapience.mind_and_traits.task.Necessity;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientTask;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientWalkTarget;
import com.gm910.occentmod.sapience.mind_and_traits.task.TaskType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SapientWalkToTargetTask extends SapientTask<CreatureEntity> {
	@Nullable
	private Path movementPath;
	@Nullable
	private BlockPos targetPos;
	private float speed;
	private int randomFactor;
	private ServerPos currentPos;

	public SapientWalkToTargetTask(int p_i50356_1_) {
		super(CreatureEntity.class, ImmutableMap.of(MemoryModuleType.PATH, MemoryModuleStatus.VALUE_ABSENT),
				p_i50356_1_);
	}

	public boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner) {
		currentPos = new ServerPos(owner);
		SapientInfo<? extends CreatureEntity> info = SapientInfo.get(owner);
		if (!SapientInfo.get(owner).getKnowledge().getValueModule(GMDeserialize.SAPIENT_WALK_TARGET).isPresent())
			return false;
		SapientWalkTarget walktarget = info.getKnowledge().getValueModule(GMDeserialize.SAPIENT_WALK_TARGET).get();
		if (!this.hasReachedTarget(owner, walktarget) && this.canMoveToTarget(owner, walktarget, worldIn.getGameTime())
				&& (hasTheGuts(worldIn, owner, walktarget))) {
			this.targetPos = walktarget.getTarget().getBlockPos();
			return true;
		} else {

			info.getKnowledge().setValueModule(GMDeserialize.SAPIENT_WALK_TARGET, null);
			return false;
		}
	}

	public boolean hasTheGuts(ServerWorld worldIn, CreatureEntity entity, SapientWalkTarget target) {
		Set<Pair<Connotation, ServerPos>> avoid = locationsToAvoid(entity).stream()
				.filter((loc) -> loc.getSecond().getDimension() == entity.dimension).collect(Collectors.toSet());
		TraitLevel para = PersonalityTrait.PARANOIA.getWeightedRandomReaction(
				SapientInfo.get(entity).getPersonality().getTrait(PersonalityTrait.PARANOIA));
		TraitLevel brav = PersonalityTrait.BRAVERY
				.getWeightedRandomReaction(SapientInfo.get(entity).getPersonality().getTrait(PersonalityTrait.BRAVERY));

		for (Pair<Connotation, ServerPos> avoidPos : avoid) {

			if (para != TraitLevel.EXCEPTIONAL_LOW
					&& target.getTarget().getPos().distanceTo(new Vec3d(avoidPos.getSecond()))
							- target.getDistance() <= para.id + 1
					&& (brav.isLessThan(TraitLevel.HIGH, true)
							? (target.getNecessity() == Necessity.NECESSARY ? brav == TraitLevel.EXCEPTIONAL_LOW
									: (target.getNecessity() == Necessity.PREFERABLE
											? brav.isLessThan(TraitLevel.HIGH_AVERAGE, false)
											: (avoidPos.getFirst() == Connotation.FATAL
													? brav != TraitLevel.EXCEPTIONAL_HIGH
													: true)))
							: (avoidPos.getFirst() == Connotation.FATAL ? brav != TraitLevel.EXCEPTIONAL_HIGH
									: true))) {
				return false;
			}
		}
		Set<Pair<Connotation, Entity>> enavoid = this.entitiesToAvoid(entity);
		for (Pair<Connotation, Entity> avoider : enavoid) {
			boolean trusts = avoider.getSecond() instanceof LivingEntity
					&& SapientInfo.isSapient((LivingEntity) avoider.getSecond())
							? SapientInfo.get(entity).getRelationships().getTrustValue(
									SapientInfo.get((LivingEntity) avoider.getSecond()).getIdentity()) >= 2.5
							: false;
			boolean reallyTrusts = avoider.getSecond() instanceof LivingEntity
					&& SapientInfo.isSapient((LivingEntity) avoider.getSecond())
							? SapientInfo.get(entity).getRelationships().getTrustValue(
									SapientInfo.get((LivingEntity) avoider.getSecond()).getIdentity()) >= 2.8
							: false;
			TraitLevel gullible = avoider.getSecond() instanceof LivingEntity
					&& SapientInfo.isSapient((LivingEntity) avoider.getSecond())
							? SapientInfo.get(entity).getPersonality().generateTraitReactionMap()
									.get(PersonalityTrait.GULLIBILITY)
							: TraitLevel.EXCEPTIONAL_LOW;
			if (gullible == TraitLevel.EXCEPTIONAL_HIGH)
				trusts = true;
			if (para != TraitLevel.EXCEPTIONAL_LOW
					&& target.getTarget().getPos().distanceTo(avoider.getSecond().getPositionVector())
							- target.getDistance() <= para.id + 1
					&& (brav.isLessThan(TraitLevel.HIGH, true)
							? (target.getNecessity() == Necessity.NECESSARY
									? brav == TraitLevel.EXCEPTIONAL_LOW && !trusts
									: (target.getNecessity() == Necessity.PREFERABLE
											? brav.isLessThan(TraitLevel.HIGH_AVERAGE, false)
											: (avoider.getFirst() == Connotation.FATAL
													? brav != TraitLevel.EXCEPTIONAL_HIGH
													: !trusts)))
							: (avoider.getFirst() == Connotation.FATAL
									? brav != TraitLevel.EXCEPTIONAL_HIGH && !reallyTrusts
									: !reallyTrusts))) {
				return false;
			}
		}
		return true;
	}

	public Set<Pair<Connotation, Entity>> entitiesToAvoid(CreatureEntity entity) {
		Set<MemoryOfOccurrence<?>> memsDa = SapientInfo.get(entity).getKnowledge()
				.getByPredicate((m) -> m instanceof MemoryOfOccurrence
						&& ((MemoryOfOccurrence<?>) m).getEvent().getDoer() != null)
				.stream().map((m) -> (MemoryOfOccurrence<?>) m).collect(Collectors.toSet());
		Set<Pair<Connotation, SapientIdentity>> setta = memsDa.stream().filter((b) -> b.getOpinion().harmful())
				.map((z) -> Pair.of(z.getOpinion(), z.getEvent().getDoer())).collect(Collectors.toSet());

		Set<CauseEffectMemory<?>> effDa = SapientInfo.get(entity).getKnowledge()
				.getByPredicate((m) -> m instanceof CauseEffectMemory).stream().map((b) -> (CauseEffectMemory<?>) b)
				.filter((m) -> m.getEffect().getDoer() != null
						&& m.getEffect().getEffect().getEffect(SapientInfo.get(entity).getIdentity()).harmful())
				.collect(Collectors.toSet());
		setta.addAll(effDa.stream()
				.map((m) -> Pair.of(m.getEffect().getEffect().getEffect(SapientInfo.get(entity).getIdentity()),
						m.getEffect().getDoer()))
				.collect(Collectors.toSet()));

		World worldIn = entity.world;

		Set<Pair<Connotation, Entity>> entitiesToAvoid = setta.stream()
				.filter((m) -> ServerPos.getEntityFromUUID(m.getSecond().getTrueId(), worldIn.getServer()) != null)
				.map((m) -> Pair.of(m.getFirst(),
						ServerPos.getEntityFromUUID(m.getSecond().getTrueId(), worldIn.getServer())))
				.collect(Collectors.toSet());

		Set<MemoryOfOccurrence<?>> memsDamen = SapientInfo.get(entity).getKnowledge()
				.getByPredicate((m) -> m instanceof MemoryOfOccurrence
						&& ((MemoryOfOccurrence<?>) m).getEvent().getDoerEntity((ServerWorld) worldIn) != null)
				.stream().map((m) -> (MemoryOfOccurrence<?>) m).collect(Collectors.toSet());

		entitiesToAvoid.addAll(memsDamen.stream()
				.map((e) -> Pair.of(e.getOpinion(), e.getEvent().getDoerEntity((ServerWorld) worldIn)))
				.collect(Collectors.toSet()));

		return entitiesToAvoid;
	}

	protected boolean shouldContinueExecuting(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn) {
		if (this.movementPath != null && this.targetPos != null) {
			Optional<SapientWalkTarget> optional = Optional.ofNullable(
					SapientInfo.get(entityIn).getKnowledge().getValueModule(GMDeserialize.SAPIENT_WALK_TARGET).get());
			PathNavigator pathnavigator = entityIn.getNavigator();
			return !pathnavigator.noPath() && optional.isPresent() && this.hasTheGuts(worldIn, entityIn, optional.get())
					&& !this.hasReachedTarget(entityIn, optional.get());
		} else {
			return false;
		}
	}

	protected void resetTask(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn) {
		entityIn.getNavigator().clearPath();
		SapientInfo.get(entityIn).getKnowledge().setValueModule(GMDeserialize.SAPIENT_WALK_TARGET, null);
		entityIn.getBrain().removeMemory(MemoryModuleType.PATH);
		this.movementPath = null;
	}

	protected void startExecuting(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn) {
		currentPos = new ServerPos(entityIn);
		entityIn.getBrain().setMemory(MemoryModuleType.PATH, this.movementPath);
		entityIn.getNavigator().setPath(this.movementPath, (double) this.speed);
		this.randomFactor = worldIn.getRandom().nextInt(10);
	}

	protected void updateTask(ServerWorld worldIn, CreatureEntity owner, long gameTime) {
		--this.randomFactor;
		currentPos = new ServerPos(owner);
		if (this.randomFactor <= 0) {
			Path path = owner.getNavigator().getPath();
			Brain<?> brain = owner.getBrain();
			if (this.movementPath != path) {
				this.movementPath = path;
				brain.setMemory(MemoryModuleType.PATH, path);
			}

			if (path != null && this.targetPos != null) {
				SapientWalkTarget walktarget = SapientInfo.get(owner).getKnowledge()
						.getValueModule(GMDeserialize.SAPIENT_WALK_TARGET).get();
				if (walktarget.getTarget().getBlockPos().distanceSq(this.targetPos) > 4.0D
						&& this.canMoveToTarget(owner, walktarget, worldIn.getGameTime())) {
					this.targetPos = walktarget.getTarget().getBlockPos();
					this.startExecuting(worldIn, owner, gameTime);
				}

			}
		}
	}

	private boolean canMoveToTarget(CreatureEntity entity, SapientWalkTarget target, long gameTime) {
		BlockPos blockpos = target.getTarget().getBlockPos();

		this.movementPath = entity.getNavigator().getPathToPos(blockpos, 0);
		this.speed = target.getSpeed();
		if (!this.hasReachedTarget(entity, target)) {
			Brain<?> brain = entity.getBrain();
			boolean flag = this.movementPath != null && this.movementPath.reachesTarget();
			if (flag) {
				brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, Optional.empty());
			} else if (!brain.hasMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
				brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, gameTime);
			}

			if (this.movementPath != null) {
				return true;
			}

			Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards((CreatureEntity) entity, 10, 7,
					new Vec3d(blockpos));

			if (vec3d != null) {
				this.movementPath = entity.getNavigator().getPathToPos(vec3d.x, vec3d.y, vec3d.z, 0);
				return this.movementPath != null;
			}
		}

		return false;
	}

	public Set<Pair<Connotation, ServerPos>> locationsToAvoid(CreatureEntity en) {
		SapientInfo<? extends MobEntity> info = SapientInfo.get(en);
		Set<MemoryOfDeed<?>> mems = info.getKnowledge()
				.getByPredicate((e) -> e instanceof MemoryOfDeed
						&& ((MemoryOfDeed<?>) e).getDeed().getType() == OccurrenceType.EXIST_AT_LOCATION
						&& ((MemoryOfDeed<?>) e).getOpinion().harmful())
				.stream().map((e) -> (MemoryOfDeed<?>) e).collect(Collectors.toSet());
		Set<CauseEffectMemory<?>> cemems = info.getKnowledge()
				.getByPredicate((e) -> e instanceof CauseEffectMemory
						&& (((CauseEffectMemory<?>) e).getCause().getType() == OccurrenceType.EXIST_AT_LOCATION)
						&& ((CauseEffectMemory<?>) e).getConnotation().getEffect(info.getIdentity()).harmful()
						&& ((CauseEffectMemory<?>) e).getCertainty().compareCertainty(Certainty.ALMOST_CERTAIN) >= 0
						&& ((CauseEffectMemory<?>) e).getConnotation().getEffect(info.getIdentity()).harmful())
				.stream().map((e) -> (CauseEffectMemory<?>) e).collect(Collectors.toSet());
		return Streams
				.concat(mems.stream().map((d) -> d)
						.map((e) -> Pair.of(e.getOpinion(), ((ExistAtLocationDeed) e.getDeed()).getLocation())),
						cemems.stream()
								.map((d) -> Pair.of(d.getEffect().getEffect().getEffect(info.getIdentity()),
										(ExistAtLocationDeed) d.getCause()))
								.map((l) -> Pair.of(l.getFirst(), l.getSecond().getLocation())))
				.collect(Collectors.toSet());
	}

	private boolean hasReachedTarget(MobEntity p_220486_1_, WalkTarget p_220486_2_) {
		return p_220486_2_.getTarget().getBlockPos().manhattanDistance(new BlockPos(p_220486_1_)) <= p_220486_2_
				.getDistance();
	}

	@Override
	public SapientDeed getDeed(SapientIdentity doer) {
		return new ExistAtLocationDeed(doer, currentPos);
	}

	@Override
	public TaskType<CreatureEntity, ? extends SapientTask<CreatureEntity>> getType() {
		return null;
	}
}