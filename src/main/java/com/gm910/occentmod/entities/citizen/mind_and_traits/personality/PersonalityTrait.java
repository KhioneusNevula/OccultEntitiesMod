package com.gm910.occentmod.entities.citizen.mind_and_traits.personality;

import java.util.Collection;
import java.util.function.BiFunction;

import com.gm910.occentmod.api.util.GMHelper;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.IStringSerializable;

/**
 * [-1, 1] float scale
 * 
 * @author borah
 *
 */
public enum PersonalityTrait implements IStringSerializable {
	/**
	 * positive values = more likely to step in to help a citizen in danger <br>
	 * negative values = more likely to run away
	 */
	BRAVERY("bravery"),
	/**
	 * positive values = more likely to take an explicit action (e.g. fight an
	 * assailant) <br>
	 * negative values = more likely to take a "safe" action (e.g. call the police
	 * on an assailant)
	 */
	BOLDNESS("boldness"),
	/**
	 * higher values = more likely to have outrageously violent or deadly reactions.
	 * A completely sane person is at 0; an insane person is at 1
	 */
	INSANITY("insanity", 0, 1),
	/**
	 * positive values = more likely to take pleasure in pain and death of those
	 * they dislike and like the pain-causers or killers<br>
	 * negative values = more likely to be sad at the death of those they dislike
	 */
	SADISM("sadism"),
	/**
	 * higher = more likely to have a mood lowering if others are in pain or
	 * hurt<br>
	 * lower = more likely to not change in mood if others are hurt or in pain
	 */
	SYMPATHY("sympathy", 0, 1),
	/**
	 * less than zero = likely to react negatively if offended <br>
	 * more than zero = likely to not blame and just let go of offense
	 */
	FORGIVINGNESS("forgivingness"),
	/**
	 * + = more likely to perform a task that will harm someone for one's own goals
	 * <br>
	 * - = more likely to perform a task that will harm oneself to help someone else
	 */
	SELFISHNESS("selfishness"),
	/**
	 * positive values == more likely to be trusting <br>
	 * negative values == more likely to not trust
	 */
	GULLIBILITY("gullibility"),
	/**
	 * positive values == less chance of accidentally fumbling a job <br>
	 * negative values = more chance of fumbling
	 */
	GRACE("grace"),
	/**
	 * positive values = more likely to prioritize worship and god's rules <br>
	 * negative values = more likely to ignore god's words and less likely to
	 * worship
	 */
	PIETY("piety"),
	/**
	 * positive values = more likely to report crime <br>
	 * negative values = more likely to commit crime
	 */
	LAWFULNESS("lawfulness"),
	/**
	 * positive values = more likely to spread negative gossip <br>
	 * negative values = less likely to spread negative gossip
	 */
	BLABBERMOUTH("blabbermouth"),
	/**
	 * + = more likely to tell truths and sell for appropriate prices <br>
	 * - = more likely to tell lies and con
	 */
	HONESTY("honesty"),
	/**
	 * + more likely to look for cheap wares <br>
	 * - more likely to look for expensive wares
	 */
	FRUGALITY("frugality"),
	/**
	 * + more likely to perform a task when one is necessary or even much before
	 * then <br>
	 * - more likely to ignore a task until it is absolutely necessary
	 */
	ACTIVENESS("activeness"),
	/**
	 * higher = fun level lowers quicker
	 */
	RESTLESSNESS("restlessness", 0, 1),
	/**
	 * higher = comes up with ideas more
	 */
	INQUISITIVITY("inquisitivity", 0, 1),
	/**
	 * higher values = needs more socialization and can more easily make friends<Br>
	 * lower/zero values = doesn't often need socialization
	 */
	EXTROVERSION("extroversion", 0, 1),
	/**
	 * positive = more likely to perform safe actions<br>
	 * negative = more likely to perform dangerous actions
	 */
	PARANOIA("paranoia"),
	/**
	 * How likely the citizen is to copy its family's beliefs and opinions<br>
	 * negative = more likely to reject them
	 */
	CONFORMITY("conformity");
	;

	public static enum TraitLevel {
		EXCEPTIONAL_LOW(-1, (min, max) -> {
			return new Pair<>(new Pair<>(min, true), new Pair<>(min + (max - min) / 12, false));
		}), LOW(0, (min, max) -> {
			return new Pair<>(new Pair<>(min + (max - min) / 12, true), new Pair<>((max - min) / 3 + min, false));
		}), LOW_AVERAGE(1, (min, max) -> {
			return new Pair<>(new Pair<>(min + (max - min) / 3, true), new Pair<>((max - min) / 2 + min, false));
		}), HIGH_AVERAGE(1, (min, max) -> {
			return new Pair<>(new Pair<>(min + (max - min) / 2, true), new Pair<>((max - min) / 3 * 2 + min, false));
		}), HIGH(2, (min, max) -> {
			return new Pair<>(new Pair<>(min + (max - min) / 3 * 2, true),
					new Pair<>(min + (max - min) / 12 * 11, false));
		}), EXCEPTIONAL_HIGH(3, (min, max) -> {
			return new Pair<>(new Pair<>(min + (max - min) / 12 * 11, true), new Pair<>(max, true));
		});

		public final int id;
		private BiFunction<Float, Float, Pair<Pair<Float, Boolean>, Pair<Float, Boolean>>> bounds;

		/**
		 * 
		 * @param id
		 * @param bounds takes in the min and max of a Trait and returns the min and max
		 *               of this reactionType's coverage. The second set of booleans is
		 *               whether the bound is inclusive
		 */
		private TraitLevel(int id, BiFunction<Float, Float, Pair<Pair<Float, Boolean>, Pair<Float, Boolean>>> bounds) {
			this.id = id;
			this.bounds = bounds;
		}

		public boolean isWithinBounds(float input, float min, float max) {
			Pair<Pair<Float, Boolean>, Pair<Float, Boolean>> fl = bounds.apply(min, max);
			return (fl.getFirst().getSecond() ? input >= fl.getFirst().getFirst() : input > fl.getFirst().getFirst())
					&& (fl.getSecond().getSecond() ? input <= fl.getSecond().getFirst()
							: input < fl.getSecond().getFirst());
		}

		public static TraitLevel getType(float degree, float min, float max) {
			for (TraitLevel type : values()) {
				if (type.isWithinBounds(degree, min, max)) {
					return type;
				}
			}
			return null;
		}

		public float getChanceByReference(float input, float min, float max) {

			if (isWithinBounds(input, min, max)) {

				Pair<Pair<Float, Boolean>, Pair<Float, Boolean>> fl = bounds.apply(min, max);
				return input / (max - min) + min;
			} else {
				return 0f;
			}
		}

		public static TraitLevel getById(int id) {
			for (TraitLevel type : values()) {
				if (type.id == id) {
					return type;
				}
			}
			return null;
		}

		/**
		 * Is high, but from a ternary mindset (low, average, high)
		 * 
		 * @return
		 */
		public boolean isTernaryHigh() {
			return this == EXCEPTIONAL_HIGH || this == HIGH;
		}

		public static Collection<TraitLevel> getTernaryHighs() {
			return Lists.newArrayList(EXCEPTIONAL_HIGH, HIGH);
		}

		/**
		 * Is average, but from a ternary mindset (low, average, high)
		 * 
		 * @return
		 */
		public boolean isTernaryAverage() {
			return this == LOW_AVERAGE || this == HIGH_AVERAGE;
		}

		public static Collection<TraitLevel> getTernaryAverages() {
			return Lists.newArrayList(LOW_AVERAGE, HIGH_AVERAGE);
		}

		/**
		 * Is low, but from a ternary mindset (low, average, high)
		 * 
		 * @return
		 */
		public boolean isTernaryLow() {
			return this == LOW || this == TraitLevel.EXCEPTIONAL_LOW;
		}

		public static Collection<TraitLevel> getTernaryLows() {
			return Lists.newArrayList(EXCEPTIONAL_LOW, LOW);
		}

		/**
		 * Is high, but from a binary mindset (high/low)
		 * 
		 * @return
		 */
		public boolean isBinaryHigh() {
			return this == HIGH || this == HIGH_AVERAGE || this == EXCEPTIONAL_HIGH;
		}

		public static Collection<TraitLevel> getBinaryHighs() {
			return Lists.newArrayList(EXCEPTIONAL_HIGH, HIGH, HIGH_AVERAGE);
		}

		/**
		 * Is low, but from a binary mindset (high/low)
		 * 
		 * @return
		 */
		public boolean isBinaryLow() {
			return this == LOW || this == LOW_AVERAGE || this == EXCEPTIONAL_LOW;
		}

		public static Collection<TraitLevel> getBinaryLows() {
			return Lists.newArrayList(EXCEPTIONAL_LOW, LOW_AVERAGE, LOW);
		}

	}

	private String name;
	float min = -1;
	float max = 1;

	private PersonalityTrait(String name) {
		this.name = name;
	}

	private PersonalityTrait(String name, float min, float max) {
		this(name);
		this.min = min;
		this.max = max;
	}

	public float getMax() {
		return max;
	}

	public float getMin() {
		return min;
	}

	public static PersonalityTrait fromName(String name) {
		for (PersonalityTrait trait : values()) {
			if (trait.getName().equals(name)) {
				return trait;
			}
		}
		return null;
	}

	public PersonalityTrait.TraitLevel getTypicalReactionType(float trait) {
		return PersonalityTrait.TraitLevel.getType(trait, min, max);
	}

	public PersonalityTrait.TraitLevel getWeightedRandomReaction(float trait) {
		PersonalityTrait.TraitLevel typic = getTypicalReactionType(trait);
		float rxnTypic = 0.5f + (trait / 0.5f);
		int rangeEitherSide = PersonalityTrait.TraitLevel.values().length - 1;
		Object2FloatMap<PersonalityTrait.TraitLevel> weights = new Object2FloatOpenHashMap<>();

		weights.put(typic, rxnTypic);

		for (int i = -rangeEitherSide; i <= rangeEitherSide; i++) {
			if (PersonalityTrait.TraitLevel.getById(i) == null)
				continue;
			if (i == 0)
				continue;
			PersonalityTrait.TraitLevel rxn = PersonalityTrait.TraitLevel.getById(i);
			int stv = Math.abs(i);
			float chancia = (float) (1 / (Math.pow(2, stv))) * (1 - rxnTypic);
			weights.put(rxn, chancia);
		}
		return GMHelper.weightedRandomFloats(weights);

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

}