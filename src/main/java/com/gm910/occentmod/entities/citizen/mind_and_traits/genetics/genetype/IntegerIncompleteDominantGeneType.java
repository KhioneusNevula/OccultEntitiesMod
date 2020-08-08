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

public class IntegerIncompleteDominantGeneType<E extends LivingEntity> extends GeneType<Integer, E> {

	Map<Race, Integer> ofRace;

	private int defVal = 0;
	private int nullVal = 0;

	public IntegerIncompleteDominantGeneType(ResourceLocation loc, Map<Race, Integer> ofRace) {
		super(loc, null);
		super.setGetRandomGene((e, t) -> new Gene<>(this, ofRace.get(e)).setRaceMarker(e));
		this.ofRace = ofRace;
	}

	public IntegerIncompleteDominantGeneType<E> setDefVal(int defVal) {
		this.defVal = defVal;
		return this;
	}

	public IntegerIncompleteDominantGeneType<E> setNullVal(int nullVal) {
		this.nullVal = nullVal;
		return this;
	}

	public float getDefVal() {
		return defVal;
	}

	@Override
	public <M> M serialize(DynamicOps<M> dynamic, Gene<Integer> value) {

		return dynamic.createInt(value.getValue());
	}

	@Override
	public Gene<Integer> deserialize(Dynamic<?> from) {
		return new Gene<>(this, from.asInt(0));
	}

	@Override
	public Gene<Integer> mix(Gene<?> par1, Gene<?> par2) {
		Gene<Integer> parent1 = (Gene<Integer>) par1;
		Gene<Integer> parent2 = (Gene<Integer>) par2;
		int min = Math.min(parent1.getValue(), parent2.getValue());
		int max = Math.min(parent2.getValue(), parent1.getValue());
		if (min == max)
			return new Gene<>(this, min);
		int new1 = (new Random()).nextInt(max + 1 + min) - min;

		Race marker = Race.MIXED;
		int diff1 = Math.abs(parent1.getValue() - new1);
		int diff2 = Math.abs(parent2.getValue() - new1);
		if (diff1 > diff2) {
			marker = parent2.getRaceMarker();
		} else if (diff1 < diff2) {
			marker = parent1.getRaceMarker();
		}

		return new Gene<>(this, new1).setRaceMarker(marker);
	}

	@Override
	public Gene<Integer> defaultGetRandom(Race race, E en) {

		return new Gene<>(this, ofRace.getOrDefault(race, defVal)).setRaceMarker(race);
	}

	@Override
	public IntegerIncompleteDominantGeneType<E> setGetRandomGene(BiFunction<Race, E, Gene<Integer>> getRandomGene) {
		// TODO Auto-generated method stub
		return (IntegerIncompleteDominantGeneType<E>) super.setGetRandomGene(getRandomGene);
	}

	@Override
	public Integer getNullValue() {
		// TODO Auto-generated method stub
		return nullVal;
	}
}
