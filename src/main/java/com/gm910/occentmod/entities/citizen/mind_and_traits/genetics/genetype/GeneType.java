package com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.genetype;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Gene;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Race;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Race.SpiritRace;
import com.gm910.occentmod.util.GMFiles;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public abstract class GeneType<T, Q extends LivingEntity> {

	private static final Map<ResourceLocation, GeneType<?, ?>> GENE_TYPES = new HashMap<>();

	/**
	 * DOMINANT: dragon wings RECESSIVE: fairy wings
	 */
	public static final AllelicGeneType<CitizenEntity> WINGS = new AllelicGeneType<>(GMFiles.rl("wings_trait"),
			Race.DRACONIAN, Race.FAIRY);
	public static final RegularGeneType<CitizenEntity> FAIRY_EARS = new RegularGeneType<>(
			GMFiles.rl("fairy_ears_trait"), ImmutableMap.of(Race.FAIRY, true));
	/**
	 * DOMINANT: troll horns, RECESSIVE: dragon horns
	 */
	public static final AllelicGeneType<CitizenEntity> HORNS = new AllelicGeneType<>(GMFiles.rl("horns_trait"),
			Race.TROLL, Race.DRACONIAN);
	public static final RegularGeneType<CitizenEntity> DRAGON_TAIL = new RegularGeneType<>(
			GMFiles.rl("dragon_tail_trait"), ImmutableMap.of(Race.DRACONIAN, true));
	public static final RegularGeneType<CitizenEntity> DRAGON_FIRE = new RegularGeneType<>(
			GMFiles.rl("dragon_fire_ability"), ImmutableMap.of(Race.DRACONIAN, true));
	public static final RegularGeneType<CitizenEntity> WEREDRAGON = new RegularGeneType<>(
			GMFiles.rl("weredragon_ability"), ImmutableMap.of(Race.DRACONIAN, true));
	public static final DoubleIncompleteDominantGeneType<CitizenEntity> DAMAGE = new DoubleIncompleteDominantGeneType<>(
			GMFiles.rl("damage_trait"), ImmutableMap.of(Race.DRACONIAN, 4.0, Race.TROLL, 6.0));
	public static final RegularGeneType<CitizenEntity> SHOCKWAVE = new RegularGeneType<>(
			GMFiles.rl("troll_shockwave_ability"), ImmutableMap.of(Race.TROLL, true));
	public static final RegularGeneType<CitizenEntity> FAIRY_PLANT_EFFECT = new RegularGeneType<>(
			GMFiles.rl("fairy_plant_effect_ability"), ImmutableMap.of(Race.FAIRY, true));
	public static final RegularGeneType<CitizenEntity> FAIRY_ANIMAL_EFFECT = new RegularGeneType<>(
			GMFiles.rl("fairy_animal_effect_ability"), ImmutableMap.of(Race.FAIRY, true));
	public static final RegularGeneType<CitizenEntity> FLICK = new RegularGeneType<>(GMFiles.rl("flick_ability"),
			ImmutableMap.of(Race.FAIRY, true, SpiritRace.SPIRIT, true, SpiritRace.DEITY, true, SpiritRace.DEMON, true,
					SpiritRace.NATURE, true));
	/**
	 * Mining tier of bare hand based on tool tiers
	 */
	public static final IntegerIncompleteDominantGeneType<CitizenEntity> MINE_TIER = new IntegerIncompleteDominantGeneType<CitizenEntity>(
			GMFiles.rl("mine_tier_attribute"), ImmutableMap.of(Race.TROLL, 2)).setNullVal(-1);
	public static final DoubleIncompleteDominantGeneType<CitizenEntity> MOVEMENT_SPEED = new DoubleIncompleteDominantGeneType<CitizenEntity>(
			GMFiles.rl("movement_speed_attribute"),
			ImmutableMap.of(Race.TROLL, 0.07, Race.FAIRY, 0.2, Race.DRACONIAN, 0.2, Race.HUMAN, 0.1)).setDefVal(0.1);
	/**
	 * DOMINANT: no sight, RECESSIVE: sight
	 */
	public static final AllelicGeneType<CitizenEntity> FLICK_SIGHT = new AllelicGeneType<>(
			GMFiles.rl("fairy_flick_sight"), Race.FAIRY, Race.FAIRY);

	public static final RegularGeneType<CitizenEntity> SPIRIT_PLANAR_TRAVEL = new RegularGeneType<>(
			GMFiles.rl("spirit_planar_travel"), ImmutableMap.of(SpiritRace.SPIRIT, true, SpiritRace.DEMON, true,
					SpiritRace.DEITY, true, SpiritRace.NATURE, true));
	public static final RegularGeneType<CitizenEntity> FIREBENDING = new RegularGeneType<>(
			GMFiles.rl("spirit_demon_firebending"), ImmutableMap.of(SpiritRace.DEMON, true, SpiritRace.DEITY, true));
	public static final RegularGeneType<CitizenEntity> PLANTBENDING = new RegularGeneType<>(
			GMFiles.rl("spirit_nature_plantbending"), ImmutableMap.of(SpiritRace.NATURE, true, SpiritRace.DEITY, true));

	public static GeneType<?, ?> get(ResourceLocation loc) {
		return GENE_TYPES.get(loc);
	}

	public static Collection<GeneType<?, ?>> getAll() {
		return GENE_TYPES.values();
	}

	private ResourceLocation resource;
	private BiFunction<Race, Q, Gene<T>> getRandomGene = this::defaultGetRandom;

	public GeneType(ResourceLocation loc, BiFunction<Race, Q, Gene<T>> getForRace) {
		this.resource = loc;
		this.getRandomGene = getForRace;
		GENE_TYPES.put(loc, this);
	}

	protected abstract Gene<T> defaultGetRandom(Race race, Q en);

	/**
	 * When an entity does not have this gene, it WOULD return null, but in the case
	 * of Integers and the like that's just weird, so they'd return a different
	 * value
	 * 
	 * @return
	 */
	public T getNullValue() {
		return null;
	}

	public GeneType<T, Q> setGetRandomGene(BiFunction<Race, Q, Gene<T>> getRandomGene) {
		this.getRandomGene = getRandomGene;
		return this;
	}

	public ResourceLocation getResource() {
		return resource;
	}

	public abstract <M> M serialize(DynamicOps<M> dynamic, Gene<T> value);

	public abstract Gene<T> deserialize(Dynamic<?> from);

	public abstract Gene<T> mix(Gene<?> parent1, Gene<?> parent2);

	/**
	 * When creating an entity with this predefined race set, this is the returned
	 * Gene
	 * 
	 * @param race
	 * @param entity
	 * @return
	 */
	public Gene<T> getInitialValue(Race race, Q entity) {

		return this.getRandomGene.apply(race, entity);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + this.resource;
	}

}
