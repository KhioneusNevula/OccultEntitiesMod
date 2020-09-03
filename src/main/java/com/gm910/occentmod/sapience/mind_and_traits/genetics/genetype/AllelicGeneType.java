package com.gm910.occentmod.sapience.mind_and_traits.genetics.genetype;

import java.util.function.BiFunction;

import com.gm910.occentmod.sapience.mind_and_traits.genetics.Gene;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.Race;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.logic.DoubleAllele;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class AllelicGeneType<E extends LivingEntity> extends GeneType<DoubleAllele, E> {

	private Race dominantRace;
	private Race recessiveRace;

	public AllelicGeneType(Class<E> ownerType, ResourceLocation loc, Race dominant, Race recessive) {
		super(loc, ownerType, DoubleAllele.class, null);
		this.dominantRace = dominant;
		this.recessiveRace = recessive;
		this.setGetRandomGene((race, e) -> new Gene<DoubleAllele>(this, forRace(race)));
	}

	public Race getDominantRace() {
		return dominantRace;
	}

	public Race getRecessiveRace() {
		return recessiveRace;
	}

	@Override
	public Gene<DoubleAllele> mix(Gene<?> parent1, Gene<?> parent2) {

		DoubleAllele d1 = (DoubleAllele) parent1.getValue();
		DoubleAllele d2 = (DoubleAllele) parent2.getValue();

		DoubleAllele result = DoubleAllele.getRandomAllele(d1, d2);

		return new Gene<>(this, result).setRaceMarker(result.isDominant() ? dominantRace : recessiveRace);
	}

	@Override
	public <M> M serialize(DynamicOps<M> dynamic, Gene<DoubleAllele> value) {
		// TODO Auto-generated method stub
		return dynamic.createString(value.getValue().name());
	}

	@Override
	public Gene<DoubleAllele> deserialize(Dynamic<?> from) {
		return new Gene<>(this, DoubleAllele.valueOf(from.asString("")));
	}

	public DoubleAllele forRace(Race race) {
		return race == dominantRace ? DoubleAllele.HOMOZYGOUS_DOMINANT
				: (race == recessiveRace ? DoubleAllele.HOMOZYGOUS_RECESSIVE : DoubleAllele.NOT_PRESENT);
	}

	@Override
	public Gene<DoubleAllele> defaultGetRandom(Race race, E en) {
		return new Gene<>(this, forRace(race)).setRaceMarker(race);
	}

	@Override
	public AllelicGeneType<E> setGetRandomGene(BiFunction<Race, E, Gene<DoubleAllele>> getRandomGene) {
		// TODO Auto-generated method stub
		return (AllelicGeneType<E>) super.setGetRandomGene(getRandomGene);
	}

	@Override
	public DoubleAllele getNullValue() {
		// TODO Auto-generated method stub
		return DoubleAllele.NOT_PRESENT;
	}

}
