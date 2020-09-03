package com.gm910.occentmod.sapience.mind_and_traits.task;

public enum Necessity {
	UNNECESSARY("unnecessary", 0), PREFERABLE("preferable", 1), NECESSARY("necessary", 2);

	public final int ord;

	private Necessity(String name, int ord) {
		this.ord = ord;
	}

}
