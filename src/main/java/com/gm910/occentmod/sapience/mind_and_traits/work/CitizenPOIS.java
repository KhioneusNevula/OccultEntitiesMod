package com.gm910.occentmod.sapience.mind_and_traits.work;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;

public class CitizenPOIS {

	public static void reg() {
	}

	public static final Set<RegistryObject<? extends PointOfInterestType>> POIS = new HashSet<>();

	public static final Predicate<PointOfInterestType> ANY_JOBSITE_PRED = (e) -> POIS.stream()
			.anyMatch((p) -> e.equals(p.get()));

	public static <T extends PointOfInterestType> RegistryObject<T> register(RegistryObject<T> t) {
		POIS.add(t);
		return t;
	}

}
