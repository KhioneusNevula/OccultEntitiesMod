package com.gm910.occentmod.entities.citizen.mind_and_traits.work;

import java.util.function.Supplier;

import net.minecraft.util.ResourceLocation;
import net.minecraft.village.PointOfInterestType;

public enum CitizenJob {

	;
	private Supplier<PointOfInterestType> sup;
	private ResourceLocation id;

	private CitizenJob(ResourceLocation identity, Supplier<PointOfInterestType> poi) {
		this.sup = poi;
		this.id = identity;
	}

	public PointOfInterestType getPOI() {
		return sup.get();
	}

	public ResourceLocation getId() {
		return id;
	}
}
