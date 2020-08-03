package com.gm910.occentmod.entities.citizen.mind;

import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.IStringSerializable;

public class PersonalityHolder implements IDynamicSerializable {

	private Object2FloatMap<NumericPersonalityTrait> traits = Object2FloatMaps.emptyMap();

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T obj = ops.createMap(traits.entrySet().stream().map((trait) -> {
			return Pair.of(ops.createString(trait.getKey().getName()), ops.createFloat(trait.getValue()));
		}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		return ops.createMap(ImmutableMap.of(ops.createString("traits"), obj));
	}

	public PersonalityHolder(Dynamic<?> dyn) {
		Map<NumericPersonalityTrait, Float> map = dyn.get("traits")
				.asMap((d) -> NumericPersonalityTrait.fromName(d.asString("")), (d) -> d.asFloat(0));
		traits.putAll(map);
	}

	public PersonalityHolder() {

	}

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

	public void setTrait(NumericPersonalityTrait trait, float value) {
		this.traits.put(trait, clamp(value, -1, 1));
	}

	/**
	 * [-1, 1] float scale
	 * 
	 * @author borah
	 *
	 */
	public static enum NumericPersonalityTrait implements IStringSerializable {
		/**
		 * positive values = more likely to step in to help a citizen in danger negative
		 * values = more likely to run away
		 */
		BRAVERY("bravery"),
		/**
		 * positive values = more likely to take an explicit action (e.g. fight an
		 * assailant) negative values = more likely to take a "safe" action (e.g. call
		 * the police on an assailant)
		 */
		BOLDNESS("boldness"),
		/**
		 * higher values = more likely to kill for their goals no negative values; a
		 * "sane" person is at zero
		 */
		INSANITY("insanity"),
		/**
		 * less than zero = likely to PlotRevenge if scorned zero = 50/50 more than zero
		 * = likely to not blame and just let go of scorn
		 */
		FORGIVINGNESS("forgivingness"),
		/**
		 * positive values == more likely to be trusting negative values == more likely
		 * to not trust
		 */
		GULLIBILITY("gullibility"),
		/**
		 * positive values == less chance of accidentally fumbling a job negative values
		 * = more chance of fumbling
		 */
		GRACE("grace"),
		/**
		 * positive values = more likely to prioritize worship and god's rules negative
		 * values = more likely to ignore god's words and less likely to worship
		 */
		PIETY("piety"),
		/**
		 * positive values = more likely to report crime negative values = more likely
		 * to commit crime
		 */
		LAWFULNESS("lawfulness"),
		/**
		 * positive values = more likely to spread gossip negative values = less likely
		 * to spread gossip
		 */
		BLABBERMOUTH("blabbermouth"),
		/**
		 * + = more likely to tell truths and sell for appropriate prices - = more
		 * likely to tell lies and con
		 */
		HONESTY("honesty"),
		/**
		 * + more likely to look for cheap wares - more likely to look for expensive
		 * wares
		 */
		FRUGALITY("frugality");

		private String name;

		private NumericPersonalityTrait(String name) {
			this.name = name;
		}

		public static NumericPersonalityTrait fromName(String name) {
			for (NumericPersonalityTrait trait : values()) {
				if (trait.getName().equals(name)) {
					return trait;
				}
			}
			return null;
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return name;
		}

	}

}
