package com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.logic;

public enum Allele {
	DOMINANT(2), RECESSIVE(1), NULL(0);

	int value;

	private Allele(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static Allele fromValue(int val) {
		for (Allele value : values()) {
			if (value.value == val)
				return value;
		}
		return null;
	}

	public static DoubleAllele add(Allele first, Allele second) {
		return DoubleAllele.fromValue(first.value + second.value);
	}

	public DoubleAllele add(Allele other) {
		return add(this, other);
	}
}