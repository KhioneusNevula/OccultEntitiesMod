package com.gm910.occentmod.sapience.mind_and_traits.genetics.genetype;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.gm910.occentmod.sapience.mind_and_traits.genetics.Gene;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.Race;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.Race.SpiritRace;
import com.gm910.occentmod.util.GMFiles;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public abstract class GeneType<GeneValueType, GeneOwnerType extends LivingEntity> {

	private static final Map<ResourceLocation, GeneType<?, ?>> GENE_TYPES = new HashMap<>();

	/**
	 * DOMINANT: dragon wings RECESSIVE: fairy wings
	 */
	public static final AllelicGeneType<LivingEntity> WINGS = new AllelicGeneType<>(LivingEntity.class,
			GMFiles.rl("wings_trait"), Race.DRACONIAN, Race.FAIRY);
	public static final RegularGeneType<LivingEntity> FAIRY_EARS = new RegularGeneType<>(LivingEntity.class,
			GMFiles.rl("fairy_ears_trait"), ImmutableMap.of(Race.FAIRY, true));
	/**
	 * DOMINANT: troll horns, RECESSIVE: dragon horns
	 */
	public static final AllelicGeneType<LivingEntity> HORNS = new AllelicGeneType<>(LivingEntity.class,
			GMFiles.rl("horns_trait"), Race.TROLL, Race.DRACONIAN);
	public static final RegularGeneType<LivingEntity> DRAGON_TAIL = new RegularGeneType<>(LivingEntity.class,
			GMFiles.rl("dragon_tail_trait"), ImmutableMap.of(Race.DRACONIAN, true));
	public static final RegularGeneType<LivingEntity> DRAGON_FIRE = new RegularGeneType<>(LivingEntity.class,
			GMFiles.rl("dragon_fire_ability"), ImmutableMap.of(Race.DRACONIAN, true));
	public static final RegularGeneType<LivingEntity> WEREDRAGON = new RegularGeneType<>(LivingEntity.class,
			GMFiles.rl("weredragon_ability"), ImmutableMap.of(Race.DRACONIAN, true));
	public static final DoubleIncompleteDominantGeneType<LivingEntity> DAMAGE = new DoubleIncompleteDominantGeneType<>(
			LivingEntity.class, GMFiles.rl("damage_trait"), ImmutableMap.of(Race.DRACONIAN, 4.0, Race.TROLL, 6.0));
	public static final RegularGeneType<LivingEntity> SHOCKWAVE = new RegularGeneType<>(LivingEntity.class,
			GMFiles.rl("troll_shockwave_ability"), ImmutableMap.of(Race.TROLL, true));
	public static final RegularGeneType<LivingEntity> FAIRY_PLANT_EFFECT = new RegularGeneType<>(LivingEntity.class,
			GMFiles.rl("fairy_plant_effect_ability"), ImmutableMap.of(Race.FAIRY, true));
	public static final RegularGeneType<LivingEntity> FAIRY_ANIMAL_EFFECT = new RegularGeneType<>(LivingEntity.class,
			GMFiles.rl("fairy_animal_effect_ability"), ImmutableMap.of(Race.FAIRY, true));
	public static final RegularGeneType<LivingEntity> FLICK = new RegularGeneType<>(LivingEntity.class,
			GMFiles.rl("flick_ability"), ImmutableMap.of(Race.FAIRY, true, SpiritRace.SPIRIT, true, SpiritRace.DEITY,
					true, SpiritRace.DEMON, true, SpiritRace.NATURE, true));

	public static final LinkedToGodGeneType<LivingEntity> PATRON_DEITY = new LinkedToGodGeneType<>(LivingEntity.class,
			GMFiles.rl("patron_deity"));
	/**
	 * Mining tier of bare hand based on tool tiers
	 */
	public static final IntegerIncompleteDominantGeneType<LivingEntity> MINE_TIER = new IntegerIncompleteDominantGeneType<LivingEntity>(
			LivingEntity.class, GMFiles.rl("mine_tier_attribute"), ImmutableMap.of(Race.TROLL, 2)).setNullVal(-1);
	public static final DoubleIncompleteDominantGeneType<LivingEntity> MOVEMENT_SPEED = new DoubleIncompleteDominantGeneType<LivingEntity>(
			LivingEntity.class, GMFiles.rl("movement_speed_attribute"),
			ImmutableMap.of(Race.TROLL, 0.07, Race.FAIRY, 0.2, Race.DRACONIAN, 0.2, Race.HUMAN, 0.1)).setDefVal(0.1);
	/**
	 * DOMINANT: no sight, RECESSIVE: sight
	 */
	public static final AllelicGeneType<LivingEntity> FLICK_SIGHT = new AllelicGeneType<>(LivingEntity.class,
			GMFiles.rl("fairy_flick_sight"), Race.FAIRY, Race.FAIRY);

	public static final RegularGeneType<LivingEntity> SPIRIT_PLANAR_TRAVEL = new RegularGeneType<>(LivingEntity.class,
			GMFiles.rl("spirit_planar_travel"), ImmutableMap.of(SpiritRace.SPIRIT, true, SpiritRace.DEMON, true,
					SpiritRace.DEITY, true, SpiritRace.NATURE, true));
	public static final RegularGeneType<LivingEntity> FIREBENDING = new RegularGeneType<>(LivingEntity.class,
			GMFiles.rl("spirit_demon_firebending"), ImmutableMap.of(SpiritRace.DEMON, true, SpiritRace.DEITY, true));
	public static final RegularGeneType<LivingEntity> PLANTBENDING = new RegularGeneType<>(LivingEntity.class,
			GMFiles.rl("spirit_nature_plantbending"), ImmutableMap.of(SpiritRace.NATURE, true, SpiritRace.DEITY, true));

	public static GeneType<?, ?> get(ResourceLocation loc) {
		return GENE_TYPES.get(loc);
	}

	public static Collection<GeneType<?, ?>> getAll() {
		return GENE_TYPES.values();
	}

	private ResourceLocation resource;
	private BiFunction<Race, GeneOwnerType, Gene<GeneValueType>> getRandomGene = this::defaultGetRandom;

	private Class<GeneOwnerType> ownerType;
	private Class<GeneValueType> valueType;

	public GeneType(ResourceLocation loc, Class<GeneOwnerType> ownerType, Class<GeneValueType> valueType,
			BiFunction<Race, GeneOwnerType, Gene<GeneValueType>> getForRace) {
		this.resource = loc;
		this.ownerType = ownerType;
		this.valueType = valueType;
		this.getRandomGene = getForRace;
		GENE_TYPES.put(loc, this);
	}

	public Class<GeneOwnerType> getOwnerType() {
		return ownerType;
	}

	public Class<GeneValueType> getValueType() {
		return valueType;
	}

	protected abstract Gene<GeneValueType> defaultGetRandom(Race race, GeneOwnerType en);

	/**
	 * When an entity does not have this gene, it WOULD return null, but in the case
	 * of Integers and the like that's just weird, so they'd return a different
	 * value
	 * 
	 * @return
	 */
	public GeneValueType getNullValue() {
		return null;
	}

	public GeneType<GeneValueType, GeneOwnerType> setGetRandomGene(
			BiFunction<Race, GeneOwnerType, Gene<GeneValueType>> getRandomGene) {
		this.getRandomGene = getRandomGene;
		return this;
	}

	public ResourceLocation getResource() {
		return resource;
	}

	public abstract <M> M serialize(DynamicOps<M> dynamic, Gene<GeneValueType> value);

	public abstract Gene<GeneValueType> deserialize(Dynamic<?> from);

	public abstract Gene<GeneValueType> mix(Gene<?> parent1, Gene<?> parent2);

	public boolean isApplicableToDeities() {
		return false;
	}

	/**
	 * When creating an entity with this predefined race set, this is the returned
	 * Gene
	 * 
	 * @param race
	 * @param entity
	 * @return
	 */
	public Gene<GeneValueType> getInitialValue(Race race, GeneOwnerType entity) {

		return this.getRandomGene.apply(race, entity);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + this.resource;
	}

}
