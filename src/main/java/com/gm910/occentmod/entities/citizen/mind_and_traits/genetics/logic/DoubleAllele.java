package com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.logic;

import java.util.Random;

import com.mojang.datafixers.util.Pair;

public enum DoubleAllele {
	HOMOZYGOUS_DOMINANT(4, Allele.DOMINANT, Allele.DOMINANT),
	HOMOZYGOUS_RECESSIVE(2, Allele.RECESSIVE, Allele.RECESSIVE), HETEROZYGOUS(3, Allele.DOMINANT, Allele.RECESSIVE),
	NOT_PRESENT(0, Allele.NULL, Allele.NULL);

	int value;
	Allele first;
	Allele second;

	private DoubleAllele(int value, Allele first, Allele second) {
		this.value = value;
		this.first = first;
		this.second = second;
	}

	public int getValue() {
		return value;
	}

	public static DoubleAllele fromValue(int val) {
		for (DoubleAllele value : values()) {
			if (value.value == val)
				return value;
		}
		return null;
	}

	public Pair<Allele, Allele> getSingles() {
		return Pair.of(first, second);
	}

	public Allele getFirst() {
		return first;
	}

	public Allele getSecond() {
		return second;
	}

	/**
	 * True if even one allele is dominant
	 */
	public boolean isDominant() {
		return this.first == Allele.DOMINANT || this.second == Allele.DOMINANT;
	}

	/**
	 * True iff both alleles are recessive
	 */
	public boolean isRecessive() {

		return this.first == Allele.RECESSIVE && this.second == Allele.RECESSIVE;
	}

	public static DoubleAllele getRandomAllele(DoubleAllele d1, DoubleAllele d2) {
		Random rand = new Random();
		DoubleAllele[] choices = { d1.getFirst().add(d2.getFirst()), d1.getFirst().add(d2.getSecond()),
				d1.getSecond().add(d2.getFirst()), d1.getSecond().add(d2.getSecond()) };

		return choices[rand.nextInt(choices.length)];
	}

}