package com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.genetype;

import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Gene;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Race;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class DoubleIncompleteDominantGeneType<E extends LivingEntity> extends GeneType<Double, E> {

	Map<Race, Double> ofRace;

	private double defVal = 0;

	private double nullVal = 0;

	public DoubleIncompleteDominantGeneType(Class<E> ownerType, ResourceLocation loc, Map<Race, Double> ofRace) {
		super(loc, ownerType, double.class, null);
		super.setGetRandomGene((e, t) -> new Gene<>(this, ofRace.get(e)).setRaceMarker(e));
		this.ofRace = ofRace;
	}

	public DoubleIncompleteDominantGeneType<E> setDefVal(double defVal) {
		this.defVal = defVal;
		return this;
	}

	public DoubleIncompleteDominantGeneType<E> setNullVal(double nullval) {
		this.nullVal = nullval;
		return this;
	}

	public double getDefVal() {
		return defVal;
	}

	@Override
	public <M> M serialize(DynamicOps<M> dynamic, Gene<Double> value) {

		return dynamic.createDouble(value.getValue());
	}

	@Override
	public Gene<Double> deserialize(Dynamic<?> from) {
		return new Gene<>(this, from.asDouble(0));
	}

	@Override
	public Gene<Double> mix(Gene<?> parent1, Gene<?> parent2) {
		double min = Math.min((double) parent1.getValue(), (double) parent2.getValue());
		double max = Math.min((double) parent2.getValue(), (double) parent1.getValue());
		if (min == max)
			return new Gene<>(this, min);
		double new1 = (new Random()).nextDouble() * (max + 1 + min) - min;

		Race marker = Race.MIXED;
		double diff1 = Math.abs((double) parent1.getValue() - new1);
		double diff2 = Math.abs((double) parent2.getValue() - new1);
		if (diff1 > diff2) {
			marker = parent2.getRaceMarker();
		} else if (diff1 < diff2) {
			marker = parent1.getRaceMarker();
		}

		return new Gene<>(this, new1).setRaceMarker(marker);
	}

	@Override
	public Gene<Double> defaultGetRandom(Race race, E en) {

		return new Gene<>(this, ofRace.getOrDefault(race, defVal)).setRaceMarker(race);
	}

	@Override
	public DoubleIncompleteDominantGeneType<E> setGetRandomGene(BiFunction<Race, E, Gene<Double>> getRandomGene) {
		// TODO Auto-generated method stub
		return (DoubleIncompleteDominantGeneType<E>) super.setGetRandomGene(getRandomGene);
	}

	@Override
	public Double getNullValue() {
		// TODO Auto-generated method stub
		return nullVal;
	}
}
