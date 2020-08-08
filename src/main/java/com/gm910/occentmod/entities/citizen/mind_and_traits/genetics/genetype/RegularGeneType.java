package com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.genetype;

import java.util.Map;
import java.util.function.BiFunction;

import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Gene;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Race;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class RegularGeneType<E extends LivingEntity> extends GeneType<Boolean, E> {

	private Map<Race, Boolean> ofRace;

	public RegularGeneType(ResourceLocation loc, Map<Race, Boolean> ofRace) {
		super(loc, null);
		this.setGetRandomGene((race, e) -> new Gene<Boolean>(this, ofRace.get(race)).setRaceMarker(race));
		this.ofRace = ofRace;
	}

	@Override
	public Gene<Boolean> mix(Gene<?> parent1, Gene<?> parent2) {

		Gene<Boolean> g = new Gene<Boolean>(this, (boolean) parent1.getValue() || (boolean) parent2.getValue());
		if (g.getValue().booleanValue() == ((Boolean) parent1.getValue()).booleanValue()) {
			g.setRaceMarker(parent1.getRaceMarker());
		} else if (g.getValue().booleanValue() == ((Boolean) parent2.getValue()).booleanValue()) {
			g.setRaceMarker(parent2.getRaceMarker());
		} else {
			g.setRaceMarker(Race.MIXED);
		}
		return g;
	}

	@Override
	public <M> M serialize(DynamicOps<M> dynamic, Gene<Boolean> value) {
		// TODO Auto-generated method stub
		return dynamic.createBoolean(value.getValue());
	}

	@Override
	public Gene<Boolean> deserialize(Dynamic<?> from) {
		return new Gene<>(this, from.asBoolean(false));
	}

	@Override
	public Gene<Boolean> defaultGetRandom(Race race, E en) {
		return new Gene<>(this, ofRace.getOrDefault(race, false)).setRaceMarker(race);
	}

	@Override
	public RegularGeneType<E> setGetRandomGene(BiFunction<Race, E, Gene<Boolean>> getRandomGene) {
		// TODO Auto-generated method stub
		return (RegularGeneType<E>) super.setGetRandomGene(getRandomGene);
	}

	@Override
	public Boolean getNullValue() {
		// TODO Auto-generated method stub
		return false;
	}

}
