package com.gm910.occentmod.sapience.mind_and_traits.memory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.math.Fraction;

import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.init.GMDeserialize;
import com.gm910.occentmod.sapience.EntityDependentInformationHolder;
import com.gm910.occentmod.sapience.mind_and_traits.emotions.Mood;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.CauseEffectMemory.Certainty;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.ExternallyGivenMemory;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.IdeaMemory;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.Memory;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.MemoryOfBlockRegion;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.MemoryOfSerializable;
import com.gm910.occentmod.sapience.mind_and_traits.personality.PersonalityTrait;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.Relationships;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class Memories<E extends LivingEntity> extends EntityDependentInformationHolder<E> {

	/**
	 * 
	 */
	private Set<Memory<? super E>> knowledge = new HashSet<>();

	private Set<Memory<? super E>> forgotten = new HashSet<>();

	private Map<GMDeserialize<?>, MemoryOfSerializable<?, E>> fixedMemoryModules = new HashMap<>();

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T gmemo = ops.createList(knowledge.stream().map((trait) -> {
			return trait.serialize(ops);
		}));
		T g = ops.createList(forgotten.stream().map((trait) -> {
			return trait.serialize(ops);
		}));
		T z = ops.createMap(fixedMemoryModules.entrySet().stream()
				.map((e) -> Pair.of(ops.createString(e.getKey().getResource().toString()), e.getValue().serialize(ops)))
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		return ops.createMap(ImmutableMap.of(ops.createString("knowledge"), gmemo, ops.createString("forgotten"), g,
				ops.createString("modules"), z));
	}

	public Memories(E en, Dynamic<?> dyn) {
		super(en);
		Set<Memory<? super E>> map2 = dyn.get("knowledge").asStream()
				.map((d) -> Memory.deserialize(this.getEntityIn(), d)).collect(Collectors.toSet());
		knowledge.addAll(map2);
		map2 = dyn.get("forgotten").asStream().map((d) -> Memory.deserialize(this.getEntityIn(), d))
				.collect(Collectors.toSet());
		forgotten.addAll(map2);
		this.fixedMemoryModules = dyn.get("modules").asMap(
				(m) -> GMDeserialize.get(new ResourceLocation(m.asString(""))),
				(v) -> (MemoryOfSerializable<?, E>) MemoryType.SERIALIZABLE.deserializer.apply(this.getEntityIn(), v));
	}

	public Memories(E en) {
		super(en);
	}

	public void tick() {
		System.out.println("Memory tick : " + this.getKnowledge());
		processMemories();
		generateIdeas();
	}

	public <M extends IDynamicSerializable> MemoryOfSerializable<M, E> getMemoryModule(GMDeserialize<M> type) {
		return (MemoryOfSerializable<M, E>) this.fixedMemoryModules.get(type);
	}

	public <M extends IDynamicSerializable> MemoryOfSerializable<M, E> setMemoryModule(MemoryOfSerializable<M, E> val) {
		return (MemoryOfSerializable<M, E>) this.fixedMemoryModules.put(val.getDeserializer(), val);
	}

	public <M extends IDynamicSerializable> Optional<M> setValueModule(GMDeserialize<M> des, M value) {
		Optional<M> op = getValueModule(des);
		if (value == null)
			this.fixedMemoryModules.remove(des);
		this.setMemoryModule(new MemoryOfSerializable<>(this.getEntityIn(), des, value));
		return op;
	}

	public <M extends IDynamicSerializable> Optional<M> setValueModule(@Nonnull M value) {
		assert value != null
				&& GMDeserialize.getFromClass(value.getClass()) != null : "There is no Deserializer registered for "
						+ value + (value != null ? " with class " + value.getClass() : "");
		Optional<M> op = (Optional<M>) getValueModule(GMDeserialize.getFromClass(value.getClass()));
		this.setMemoryModule(new MemoryOfSerializable<>(this.getEntityIn(),
				(GMDeserialize<M>) GMDeserialize.getFromClass(value.getClass()), value));
		return op;
	}

	public <M extends IDynamicSerializable> Optional<M> getValueModule(GMDeserialize<M> type) {

		return this.fixedMemoryModules.get(type) != null
				? (Optional<M>) Optional.ofNullable(this.fixedMemoryModules.get(type).getValue())
				: Optional.empty();
	}

	public void generateIdeas() {

		SapientInfo<E> info = SapientInfo.get(getEntityIn());

		float chancia = this.getEntityIn().getRNG().nextFloat();
		float inqui = info.getPersonality().getTrait(PersonalityTrait.INQUISITIVITY);
		if (chancia < inqui && !info.getEmotions().getMoods().contains(Mood.CREATIVE)) {
			info.getEmotions().addMood(Mood.CREATIVE, (int) (inqui * 40), this.getEntityIn());
			// System.out.println("Added creative mood for citizen with inquisitivity " +
			// inqui);
		}

		if (info.getEmotions().getMoods().contains(Mood.CREATIVE)) {

			// System.out.println("Has creative mood");
		}

		if (info.getEmotions().getMoods().contains(Mood.CREATIVE) && chancia < inqui) {
			this.addKnowledge(new IdeaMemory<E>(this.getEntityIn()));

		}
		/*System.out.println("Has idea " + this.<IdeaMemory<? super E>>getByPredicate((e) -> e instanceof IdeaMemory)
				.stream().map((e) -> e.getDoTask()).collect(Collectors.toSet()));*/

		for (IdeaMemory<? super E> mem : this.<IdeaMemory<? super E>>getByPredicate((e) -> e instanceof IdeaMemory)) {
			if (!mem.isUseless()) {
				chancia = this.getEntityIn().getRNG().nextFloat();
				if (chancia < inqui) {
					mem.affectCitizen(this.getEntityIn());
					mem.access();
				}
			}
		}
	}

	public void memorizeRegion() {

		if (this.getTicksExisted() % (1 + this.getEntityIn().getRNG().nextInt(5)) == 0) {
			ServerWorld world = (ServerWorld) this.getEntityIn().world;
			Set<Pair<BlockPos, BlockState>> stats = Sets.newHashSet();
			for (int x = -20; x <= 20; x++) {
				for (int y = -20; y <= 20; y++) {
					for (int z = -20; z <= 20; z++) {
						BlockPos pos = this.getEntityIn().getPosition().add(x, y, z);
						RayTraceResult result = world.rayTraceBlocks(new RayTraceContext(new Vec3d(pos),
								getEntityIn().getPositionVector(), RayTraceContext.BlockMode.COLLIDER,
								RayTraceContext.FluidMode.NONE, getEntityIn()));
						Vec3d hit = result.getHitVec();
						if (result.getType() == Type.BLOCK && new BlockPos(hit).distanceSq(pos) >= 4) {
							continue;
						}
						if (!this.getByPredicate(
								(e) -> e instanceof MemoryOfBlockRegion && ((MemoryOfBlockRegion<E>) e).contains(pos))
								.isEmpty()) {
							continue;
						}
						stats.add(Pair.of(pos, world.getBlockState(pos)));
					}
				}
			}
			MemoryOfBlockRegion<E> memreg = new MemoryOfBlockRegion<E>(this.getEntityIn(), world.dimension.getType(),
					stats.toArray(new Pair[0]));
			memreg.setMemTolerance(Fraction.getFraction(2, 1));
			this.receiveKnowledge(memreg);
		}
	}

	public void processMemories() {
		for (Memory<? super E> mem : new HashSet<>(this.knowledge)) {
			long existed = mem.getTicksExisted();
			int accesses = mem.getAccessedTimes();
			if (mem.memTolerance().floatValue() <= 0 ? false
					: (existed / 24000 % mem.memTolerance().getDenominator() > existed / 24000
							&& accesses < mem.memTolerance().getNumerator() || mem.isUseless())) {
				mem.setAccessedTimes(0);
				this.forget(mem);
			}
		}

	}

	public Set<Memory<? super E>> getKnowledge() {
		return new HashSet<>(this.knowledge);
	}

	public Set<Memory<? super E>> getForgottenKnowledge() {
		return new HashSet<>(this.forgotten);
	}

	public boolean knows(Memory<? super E> mem) {
		return this.knowledge.contains(mem);
	}

	public boolean knows(Predicate<? super Memory<? super E>> pred) {
		return this.knowledge.stream().anyMatch(pred);
	}

	public <T extends Memory<? super E>> Set<T> getByPredicate(Predicate<T> pred) {
		Set<T> t = this.knowledge.stream().filter((m) -> ModReflect.<T>instanceOf(m, Memory.class)).map((d) -> (T) d)
				.filter(pred).collect(Collectors.toSet());
		return t;
	}

	public Set<Memory<? super E>> fromTime(long minGrace, long maxGrace) {
		return this
				.getByPredicate((e) -> e.getMemoryCreationTime() >= minGrace && e.getMemoryCreationTime() <= maxGrace);
	}

	public void addKnowledge(Memory<? super E> mem) {
		mem.setOwner(this.getEntityIn());

		this.knowledge.add(mem);
	}

	public void shareKnowledge(Memory<? super E> mem, E other) {
		SapientInfo.get(other).getKnowledge().receiveKnowledge(mem);

	}

	public void receiveKnowledge(Memory<? super E> mem) {

		if (mem.getOwner() != this.getEntityIn()) {
			float trustProba = SapientInfo.get(this.getEntityIn()).getRelationships()
					.getTrustValue(SapientInfo.get(mem.getOwner()).getIdentity()) / Relationships.MAX_TRUST_VALUE;
			Certainty trust = Certainty.values()[(int) (trustProba * Certainty.values().length)];
			mem = new ExternallyGivenMemory<>(this.getEntityIn(),
					SapientInfo.getLazy(mem.getOwner()).orElse(null).getIdentity(), mem, trust);
		}

		Memory<? super E> copy = Memory.copy(this.getEntityIn(), mem);

		copy.affectCitizen(this.getEntityIn());
		copy.affectMood();

		this.addKnowledge(copy);
	}

	public void forget(Memory<? super E> mem) {
		this.knowledge.remove(mem);
		this.forgotten.add(mem);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.knowledge.toString();
	}

}
