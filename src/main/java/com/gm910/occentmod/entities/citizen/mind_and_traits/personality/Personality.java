package com.gm910.occentmod.entities.citizen.mind_and_traits.personality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.GMHelper;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.InformationHolder;
import com.gm910.occentmod.entities.citizen.mind_and_traits.gossip.GossipType;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

public class Personality extends InformationHolder {

	private Object2FloatMap<NumericPersonalityTrait> traits = Object2FloatMaps.emptyMap();

	private List<GossipType<?>> gossipPriority = new ArrayList<>();

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T trait1 = ops.createMap(traits.entrySet().stream().map((trait) -> {
			return Pair.of(ops.createString(trait.getKey().getName()), ops.createFloat(trait.getValue()));
		}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		T gos = ops.createList(gossipPriority.stream().map((e) -> ops.createString(e.regName.toString())));
		return ops.createMap(
				ImmutableMap.of(ops.createString("traits"), trait1, ops.createString("gossipPriority"), gos));
	}

	public Personality(Dynamic<?> dyn) {
		Map<NumericPersonalityTrait, Float> map = dyn.get("traits")
				.asMap((d) -> NumericPersonalityTrait.fromName(d.asString("")), (d) -> d.asFloat(0));
		traits.putAll(map);
		List<GossipType<?>> gos = dyn.get("gossipPriority")
				.asList((ee) -> GossipType.get(new ResourceLocation(ee.asString(""))));
		this.gossipPriority.addAll(gos);
	}

	public Personality() {
		for (NumericPersonalityTrait trait : NumericPersonalityTrait.values()) {
			traits.put(trait, 0);
		}
		Collection<GossipType<?>> ls = GossipType.getGossipTypes();
		this.gossipPriority = new ArrayList<>(ls);
		Collections.shuffle(gossipPriority);
	}

	public static double gaussian(Random rand, double mean, double standev) {
		return rand.nextGaussian() * standev + mean;
	}

	public Personality initializeRandomTraits(CitizenEntity e) {
		for (NumericPersonalityTrait trait : NumericPersonalityTrait.values()) {

			traits.put(trait,
					clamp((float) (gaussian(e.getRNG(), ((trait.max + trait.min) / 2), ((trait.max - trait.min) / 4))),
							trait.min, trait.max));
		}
		return this;
	}

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

	public void setTrait(NumericPersonalityTrait trait, float value) {
		this.traits.put(trait, clamp(value, trait.min, trait.max));
	}

	public float getTrait(NumericPersonalityTrait trait) {
		return this.traits.getFloat(trait);
	}

	/**
	 * [-1, 1] float scale
	 * 
	 * @author borah
	 *
	 */
	public static enum NumericPersonalityTrait implements IStringSerializable {
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
		 * higher values = more likely to have outrageously violent reactions. A
		 * completely sane person is at 0; an insane person is at 1
		 */
		INSANITY("insanity", 0, 1),
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
		 * + more likely to spend free time working <br>
		 * more likely to spend work time doing other stuff
		 */
		WORKAHOLISM("workaholism");
		;

		private String name;
		private float min = -1;
		private float max = 1;

		private NumericPersonalityTrait(String name) {
			this.name = name;
		}

		private NumericPersonalityTrait(String name, float min, float max) {
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

		public static NumericPersonalityTrait fromName(String name) {
			for (NumericPersonalityTrait trait : values()) {
				if (trait.getName().equals(name)) {
					return trait;
				}
			}
			return null;
		}

		public ReactionType getTypicalReactionType(float trait) {
			return ReactionType.getType(trait, min, max);
		}

		public ReactionType getWeightedRandomReaction(float trait) {
			ReactionType typic = getTypicalReactionType(trait);
			float rxnTypic = 0.5f + (trait / 0.5f);
			int rangeEitherSide = ReactionType.values().length - 1;
			Object2FloatMap<ReactionType> weights = Object2FloatMaps.emptyMap();
			weights.put(typic, rxnTypic);

			for (int i = -rangeEitherSide; i <= rangeEitherSide; i++) {
				if (ReactionType.getById(i) == null)
					continue;
				if (i == 0)
					continue;
				ReactionType rxn = ReactionType.getById(i);
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

	public static enum ReactionType {
		EXCEPTIONAL_LOW(-1, (min, max) -> {
			return new Pair<>(new Pair<>(min, true), new Pair<>(min + (max - min) / 12, false));
		}), LOW(0, (min, max) -> {
			return new Pair<>(new Pair<>(min + (max - min) / 12, true), new Pair<>((max - min) / 3 + min, false));
		}), AVERAGE(1, (min, max) -> {
			return new Pair<>(new Pair<>(min + (max - min) / 3, true), new Pair<>((max - min) / 3 * 2 + min, false));
		}), HIGH(2, (min, max) -> {
			return new Pair<>(new Pair<>(min + (max - min) / 3 * 2, true),
					new Pair<>(min + (max - min) / 12 * 11, false));
		}), EXCEPTIONAL(3, (min, max) -> {
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
		private ReactionType(int id,
				BiFunction<Float, Float, Pair<Pair<Float, Boolean>, Pair<Float, Boolean>>> bounds) {
			this.id = id;
			this.bounds = bounds;
		}

		public boolean isWithinBounds(float input, float min, float max) {
			Pair<Pair<Float, Boolean>, Pair<Float, Boolean>> fl = bounds.apply(min, max);
			return (fl.getFirst().getSecond() ? input >= fl.getFirst().getFirst() : input > fl.getFirst().getFirst())
					&& (fl.getSecond().getSecond() ? input <= fl.getSecond().getFirst()
							: input < fl.getSecond().getFirst());
		}

		public static ReactionType getType(float degree, float min, float max) {
			for (ReactionType type : values()) {
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

		public static ReactionType getById(int id) {
			for (ReactionType type : values()) {
				if (type.id == id) {
					return type;
				}
			}
			return null;
		}
	}

}
