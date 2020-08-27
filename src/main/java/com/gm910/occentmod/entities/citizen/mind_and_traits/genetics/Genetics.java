package com.gm910.occentmod.entities.citizen.mind_and_traits.genetics;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.math.Fraction;

import com.gm910.occentmod.entities.citizen.mind_and_traits.InformationHolder;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.genetype.GeneType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class Genetics<E extends LivingEntity> extends InformationHolder {

	private Map<GeneType<?, ? super E>, Gene<?>> genes = Maps.newHashMap();

	/*
		public static void forceClinit() {
		}
	*/

	private Class<E> ownerClass;

	public Genetics(Class<E> ownerClass) {
		this.ownerClass = ownerClass;
	}

	public Class<E> getOwnerClass() {
		return ownerClass;
	}

	public Genetics<E> copy() {
		Genetics<E> copy = new Genetics<E>(this.ownerClass);
		copy.genes = this.genes.entrySet().stream().map((e) -> Pair.of(e.getKey(), e.getValue().copy()))
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
		return copy;
	}

	public Genetics<E> initGenes(Race race, E entity) {
		Set<GeneType<?, E>> geneTypes = race.getGeneTypes(entity);
		for (GeneType<?, E> type1 : geneTypes) {
			GeneType<?, E> type = type1;
			this.genes.put(type, type.getInitialValue(race, entity));
		}
		return this;
	}

	public void addGene(GeneType<?, ? super E> gene, Race race, E entity) {
		this.genes.put(gene, gene.getInitialValue(race, entity));
	}

	public void addGenes(Map<GeneType<?, ? super E>, Gene<?>> genes) {
		this.genes.putAll(genes);
	}

	public Map<GeneType<?, ? super E>, Gene<?>> removeGenes(BiPredicate<GeneType<?, ? super E>, Gene<?>> remover) {
		Map<GeneType<?, ? super E>, Gene<?>> map = new HashMap<>();
		for (GeneType<?, ? super E> type : new HashSet<>(genes.keySet())) {
			if (remover.test(type, genes.get(type))) {
				map.put(type, genes.get(type));
				genes.remove(type);
			}
		}
		return map;
	}

	public Map<GeneType<?, ? super E>, Gene<?>> removeGenesFromRace(Race race) {
		return this.removeGenes((g, ge) -> ge.getRaceMarker() == race);
	}

	public Genetics<E> setGenes(Map<GeneType<?, E>, Gene<?>> genes) {
		this.genes = Maps.newHashMap(genes);

		return this;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <T> Gene<T> getGene(GeneType<T, ? super E> type) {
		return (Gene<T>) genes.get(type);
	}

	public <T> void setGene(GeneType<?, ? super E> type, Gene<?> gene) {
		this.genes.put(type, gene);
	}

	public <T> T getValue(GeneType<T, ? super E> genetype) {
		Gene<T> gene = getGene(genetype);
		if (gene == null) {
			return genetype.getNullValue();
		}
		return gene.getValue();
	}

	@SuppressWarnings("unchecked")
	public Genetics(Dynamic<?> dyn) {
		this.genes = dyn.get("genes").<GeneType<?, ? super E>, Gene<?>>asMap(
				(e) -> (GeneType<?, ? super E>) GeneType.get(new ResourceLocation(e.asString(""))), Gene::deserialize);
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T map = ops.createMap(genes.entrySet().stream().map((entry) -> {
			return Pair.of(ops.createString(entry.getKey().getResource().toString()), entry.getValue().serialize(ops));
		}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		return ops.createMap(ImmutableMap.of(ops.createString("genes"), map));
	}

	/**
	 * returns null if there are no genes Otherwise, checks the race marker of every
	 * gene and determines which race is in majority; if two or more races are in
	 * majority this will return MIXED
	 * 
	 * @return
	 */
	public Race getRace() {
		if (genes.isEmpty())
			return null;
		Collection<Gene<?>> geneSet = this.genes.values();
		Object2IntMap<Race> valsForEach = new Object2IntOpenHashMap<>();
		for (Race race : Race.getRaces()) {
			valsForEach.put(race, 0);
		}
		for (Gene<?> gene : geneSet) {
			if (gene.getRaceMarker() == Race.MIXED || gene.getRaceMarker() == null || gene.getRaceMarker().isHidden())
				continue;
			valsForEach.put(gene.getRaceMarker(), valsForEach.getInt(gene.getRaceMarker()) + 1);
		}
		int sum = 0;
		for (int i : valsForEach.values()) {
			sum += i;
		}
		Fraction greatestFraction = Fraction.getFraction(0, 1);
		Set<Race> greaterThanHalf = Sets.newHashSet();
		for (Race race : valsForEach.keySet()) {
			int val = valsForEach.getInt(race);
			if (Fraction.getReducedFraction(val, sum).compareTo(greatestFraction) > 0) {
				greaterThanHalf = Sets.newHashSet(race);
			} else if (Fraction.getReducedFraction(val, sum).compareTo(greatestFraction) == 0) {
				greaterThanHalf.add(race);
			}
		}
		if (greaterThanHalf.size() > 1) {
			return Race.MIXED;
		} else if (greaterThanHalf.size() == 1) {
			return greaterThanHalf.stream().findAny().get();
		} else {
			return null;
		}

	}

	public Genetics<E> getChild(Genetics<E> other, E child) {
		Set<GeneType<?, ? super E>> geneTypes = Sets.newHashSet(this.genes.keySet());
		geneTypes.addAll(other.genes.keySet());
		Genetics<E> new1 = new Genetics<E>(this.ownerClass);
		for (GeneType<?, ? super E> type : geneTypes) {
			if (this.getGene(type) != null && other.getGene(type) != null) {
				new1.setGene(type, type.mix(this.getGene(type), other.getGene(type)));
			} else {
				if (this.getGene(type) == null) {
					new1.setGene(type, other.getGene(type));
				} else {
					new1.setGene(type, this.getGene(type));
				}
			}
		}
		return new1;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " : "
				+ this.genes.entrySet().stream()
						.<Pair<ResourceLocation, Object>>map(
								(e) -> Pair.of(e.getKey().getResource(), (Object) e.getValue().getValue()))
						.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
	}

	@Override
	public long getTicksExisted() {
		return 0;
	}

}
