package com.gm910.occentmod.sapience.mind_and_traits.genetics.genetype;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.gm910.occentmod.sapience.mind_and_traits.genetics.Gene;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.Race;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class LinkedToGodGeneType<E extends LivingEntity> extends GeneType<Set<UUID>, E> {

	public LinkedToGodGeneType(Class<E> ownerType, ResourceLocation loc) {
		super(loc, ownerType, (Class<Set<UUID>>) (new HashSet<UUID>().getClass()), null);
		this.setGetRandomGene((race, e) -> new Gene<Set<UUID>>(this, Sets.newHashSet()).setRaceMarker(race));
	}

	@Override
	public Gene<Set<UUID>> mix(Gene<?> parent1, Gene<?> parent2) {

		Gene<Set<UUID>> g = new Gene<Set<UUID>>(this, Stream.concat(((Gene<Set<UUID>>) parent1).getValue().stream(),
				((Gene<Set<UUID>>) parent2).getValue().stream()).collect(Collectors.toSet()));

		g.setRaceMarker(Race.MIXED);
		return g;
	}

	@Override
	public <M> M serialize(DynamicOps<M> dynamic, Gene<Set<UUID>> value) {
		// TODO Auto-generated method stub
		return dynamic.createList(value.getValue().stream().map((e) -> dynamic.createString(e.toString())));
	}

	@Override
	public Gene<Set<UUID>> deserialize(Dynamic<?> from) {
		return new Gene<>(this, Sets.newHashSet(from.asList((e) -> UUID.fromString(e.asString("")))));
	}

	@Override
	public Gene<Set<UUID>> defaultGetRandom(Race race, E en) {
		return new Gene<Set<UUID>>(this, Sets.newHashSet()).setRaceMarker(race);
	}

	@Override
	public LinkedToGodGeneType<E> setGetRandomGene(BiFunction<Race, E, Gene<Set<UUID>>> getRandomGene) {
		return (LinkedToGodGeneType<E>) super.setGetRandomGene(getRandomGene);
	}

	@Override
	public Set<UUID> getNullValue() {
		return Sets.newHashSet();
	}

}
