package com.gm910.occentmod.entities.citizen.mind_and_traits.emotions;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.entities.citizen.mind_and_traits.InformationHolder;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.Personality;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.PersonalityTrait;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class Emotions extends InformationHolder {

	/**
	 * Positive: entertained<br>
	 * Negative: bored<br>
	 * May cause citizen to start hobby activities Starts at 0 upon creation. Range
	 * of -3 to 3
	 */
	private float funLevel;

	/**
	 * Positive: happy<br>
	 * Negative: sad<br>
	 * May cause citizen to do hobbies or talk with people they like Starts at 0 on
	 * creation. Range of -3 to 3
	 */
	private float happinessLevel;

	/**
	 * above threshhold: doesn't need more socializing <Br>
	 * below threshhold / 3: deprived of socialization <br>
	 * Resets upon sleeping<br>
	 * May cause citizen to attempt talking with people they like or new people
	 * Starts at 0 on creation. Range of 0 to 3.
	 */
	private float socialLevel;

	/**
	 * The citizen's comfort with their current situation<br>
	 * Different depending on paranoia levels. Also depends on the environment
	 * Starts at 0 on creation. Range of -3 to 3.
	 */
	private float comfortLevel;

	private Object2IntMap<Mood<?>> moods = new Object2IntOpenHashMap<>();

	public Emotions() {
		super();
	}

	public <T extends LivingEntity> void addMood(Mood<?> mood, int time, T entity) {
		if (mood.isForClass(entity.getClass())) {
			this.moods.put(mood, time);
		}
	}

	@Override
	public void tick() {
		for (Mood<?> m : new HashSet<>(moods.keySet())) {
			moods.put(m, moods.getInt(m) - 1);
			if (moods.getInt(m) <= 0) {
				moods.removeInt(m);
			}
		}
	}

	public float getThreshholdOfSatisfaction(Personality persona, EmotionType type) {
		switch (type) {
		case HAPPINESS: {
			return this.getMax(type) * 3 / 4;
		}
		case COMFORT: {
			return Math.max(0, persona.getTrait(PersonalityTrait.PARANOIA) * getMax(type));
		}
		case FUN: {
			return persona.getTrait(PersonalityTrait.RESTLESSNESS) * getMax(type);
		}
		case SOCIAL: {
			return persona.getTrait(PersonalityTrait.EXTROVERSION) * getMax(type);
		}
		default: {
			return 0;
		}
		}
	}

	public Set<Mood<?>> getMoods() {
		return moods.keySet();
	}

	public int getTimeLeft(Mood<?> mood) {
		return moods.getInt(mood);
	}

	public void changeLevel(EmotionType type, float amt) {
		this.setLevel(type, this.getLevel(type) + amt);
	}

	public float getLevel(EmotionType type) {
		switch (type) {
		case HAPPINESS: {
			return happinessLevel;
		}
		case COMFORT: {
			return comfortLevel;
		}
		case FUN: {
			return funLevel;
		}
		case SOCIAL: {
			return socialLevel;
		}
		default: {
			return 0;
		}
		}
	}

	public void setLevel(EmotionType type, float val) {
		switch (type) {
		case HAPPINESS: {
			happinessLevel = clamp(type, val);
		}
		case COMFORT: {
			comfortLevel = clamp(type, val);
		}
		case FUN: {
			funLevel = clamp(type, val);
		}
		case SOCIAL: {
			socialLevel = clamp(type, val);
		}
		}
	}

	public float getMin(EmotionType type) {
		switch (type) {
		case HAPPINESS: {
			return -3;
		}
		case COMFORT: {
			return -3;
		}
		case FUN: {
			return -3;
		}
		case SOCIAL: {
			return 0;
		}
		default: {
			return -3;
		}
		}
	}

	public float getMax(EmotionType type) {
		switch (type) {
		case HAPPINESS: {
			return 3;
		}
		case COMFORT: {
			return 3;
		}
		case FUN: {
			return 3;
		}
		case SOCIAL: {
			return 3;
		}
		default: {
			return 3;
		}
		}
	}

	public float clamp(EmotionType type, float val) {
		switch (type) {
		case HAPPINESS: {
			return MathHelper.clamp(val, getMin(type), getMax(type));
		}
		case COMFORT: {
			return MathHelper.clamp(val, getMin(type), getMax(type));
		}
		case FUN: {
			return MathHelper.clamp(val, getMin(type), getMax(type));
		}
		case SOCIAL: {
			return MathHelper.clamp(val, getMin(type), getMax(type));
		}
		default: {
			return val;
		}
		}
	}

	public <T> Emotions(Dynamic<T> dyn) {
		super();
		this.happinessLevel = dyn.get("happiness").asFloat(0);
		this.socialLevel = dyn.get("social").asFloat(0);
		this.funLevel = dyn.get("fun").asFloat(0);
		this.comfortLevel = dyn.get("comfort").asFloat(0);
		this.moods = new Object2IntOpenHashMap<>(
				dyn.get("moods").asMap((e) -> Mood.get(new ResourceLocation(e.asString(""))), (e) -> e.asInt(0)));
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T h = ops.createFloat(happinessLevel);
		T s = ops.createFloat(socialLevel);
		T f = ops.createFloat(funLevel);
		T c = ops.createFloat(comfortLevel);
		T m = ops.createMap(moods.object2IntEntrySet().stream()
				.map((e) -> Pair.of(ops.createString(e.getKey().getRL().toString()), ops.createInt(e.getIntValue())))
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		return ops.createMap(ImmutableMap.of(ops.createString("happiness"), h, ops.createString("social"), s,
				ops.createString("fun"), f, ops.createString("comfort"), c, ops.createString("moods"), m));
	}

	@Override
	public long getTicksExisted() {
		return 0;
	}

	public static enum EmotionType {
		HAPPINESS, SOCIAL, FUN, COMFORT
	}

}
