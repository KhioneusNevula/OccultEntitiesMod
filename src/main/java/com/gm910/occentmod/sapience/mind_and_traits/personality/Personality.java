package com.gm910.occentmod.sapience.mind_and_traits.personality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.sapience.EntityDependentInformationHolder;
import com.gm910.occentmod.sapience.mind_and_traits.emotions.Emotions;
import com.gm910.occentmod.sapience.mind_and_traits.emotions.Mood;
import com.gm910.occentmod.sapience.mind_and_traits.memory.MemoryType;
import com.gm910.occentmod.sapience.mind_and_traits.personality.PersonalityTrait.TraitLevel;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientTask;
import com.gm910.occentmod.sapience.mind_and_traits.task.TaskType;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class Personality<E extends LivingEntity> extends EntityDependentInformationHolder<E> {

	private Object2FloatMap<PersonalityTrait> traits = new Object2FloatOpenHashMap<>();

	private List<MemoryType<?>> gossipPriority = new ArrayList<>();

	private Set<TaskType<? super E, ?>> hobbies = new HashSet<>();

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Map<T, T> of = new HashMap<>();
		T trait1 = ops.createMap(traits.object2FloatEntrySet().stream().map((trait) -> {
			return Pair.of(ops.createInt(trait.getKey().ordinal()), ops.createFloat(trait.getFloatValue()));
		}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		T gos = ops.createList(gossipPriority.stream().map((e) -> ops.createString(e.regName.toString())));
		T hobs = ops.createList(hobbies.stream().map((e) -> ops.createString(e.getResourceLocation().toString())));
		of.put(ops.createString("traits"), trait1);
		of.put(ops.createString("gossipPriority"), gos);
		of.put(ops.createString("hobbies"), hobs);
		return ops.createMap(of);
	}

	public Personality(E owner, Dynamic<?> dyn) {
		super(owner);
		Map<PersonalityTrait, Float> map = dyn.get("traits").asMap((d) -> PersonalityTrait.values()[d.asInt(0)],
				(d) -> d.asFloat(0));
		traits.putAll(map);
		List<MemoryType<?>> gos = dyn.get("gossipPriority")
				.asList((ee) -> MemoryType.get(new ResourceLocation(ee.asString(""))));
		this.gossipPriority.addAll(gos);
		Set<TaskType<? super E, ?>> tps = Sets.newHashSet(dyn.get("hobbies")
				.asList((ee) -> (TaskType<? super E, ?>) TaskType.get(new ResourceLocation(ee.asString("")))));
		this.hobbies.addAll(tps);
	}

	public Personality(E owner) {
		super(owner);
		for (PersonalityTrait trait : PersonalityTrait.values()) {
			traits.put(trait, 0.0f);
		}
		Collection<MemoryType<?>> ls = MemoryType.getMemoryTypes();
		this.gossipPriority = new ArrayList<>(ls);
		Collections.shuffle(gossipPriority);
	}

	public static double gaussian(Random rand, double mean, double standev) {
		return rand.nextGaussian() * standev + mean;
	}

	public Personality<E> initializeRandomTraits() {
		for (PersonalityTrait trait : PersonalityTrait.values()) {

			traits.put(trait, clamp((float) (gaussian(this.getEntityIn().getRNG(), ((trait.max + trait.min) / 2),
					((trait.max - trait.min) / 4))), trait.min, trait.max));
		}
		System.out.println("Initialized random traits to " + this.traits);
		this.initHobbies();
		return this;
	}

	public void initHobbies() {
		for (TaskType<? super E, ?> tasca : TaskType.getValues().stream()
				.filter((a) -> a.getDoerClass().isAssignableFrom(this.getEntityIn().getClass())
						&& ((SapientTask<? super E>) a.createNew(SapientInfo.get(this.getEntityIn()).getAutonomy()))
								.canExecute(getEntityIn())
						&& a.isHobby())
				.map((e) -> (TaskType<? super E, ?>) e).collect(Collectors.toSet())) {
			if (getEntityIn().getRNG().nextFloat() < this.getTrait(PersonalityTrait.RESTLESSNESS)) {
				this.hobbies.add(tasca);
			}
		}
	}

	public Set<TaskType<? super E, ?>> getHobbies() {
		return this.hobbies;
	}

	@Override
	public void tick() {
		super.tick();

		if (SapientInfo.get(this.getEntityIn()).getEmotions() != null) {
			for (TaskType<?, ?> tascatipa : this.hobbies) {
				if (SapientInfo.get(this.getEntityIn()).getAutonomy().getRunningTasks().stream()
						.anyMatch((e) -> e instanceof SapientTask && ((SapientTask<?>) e).getType() == tascatipa)) {
					Emotions ems = SapientInfo.get(this.getEntityIn()).getEmotions();
					ems.addMood(Mood.HOBBY, 40, this.getEntityIn());
				}
			}
		}
	}

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

	public void setTrait(PersonalityTrait trait, float value) {
		this.traits.put(trait, clamp(value, trait.min, trait.max));
	}

	public float getTrait(PersonalityTrait trait) {
		return this.traits.getFloat(trait);
	}

	public Map<PersonalityTrait, TraitLevel> generateTraitReactionMap() {
		Map<PersonalityTrait, TraitLevel> mapa = new HashMap<>();
		for (PersonalityTrait trait : PersonalityTrait.values()) {
			mapa.put(trait, trait.getWeightedRandomReaction(this.getTrait(trait)));
		}
		return mapa;
	}

	@Override
	public long getTicksExisted() {
		return 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + " with " + this.traits;
	}

}
