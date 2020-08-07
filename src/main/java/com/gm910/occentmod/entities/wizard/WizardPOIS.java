package com.gm910.occentmod.entities.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.gm910.occentmod.init.DataInit;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Blocks;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;

@Deprecated
public final class WizardPOIS {
	private WizardPOIS() {
	}

	public static void forceClinit() {
	}

	public static final List<RegistryObject<? extends PointOfInterestType>> POIS = new ArrayList<>();

	public static final Predicate<PointOfInterestType> ANY_WIZARD_JOBSITE_PREDICATE = (e) -> POIS.stream()
			.anyMatch((p) -> e.equals(p.get()));

	public static final RegistryObject<PointOfInterestType> JOBLESS_POI = register(
			DataInit.registerPOIFromPredicateAndStates(WizardEntity.PREFIX + "_jobless", () -> ImmutableSet.of(),
					ANY_WIZARD_JOBSITE_PREDICATE, 2, 2));

	public static final RegistryObject<PointOfInterestType> SUMMONER_POI = register(
			DataInit.registerPOIFromBlocks(WizardEntity.PREFIX + "_summoner_job", () -> ImmutableSet.of(), 2, 2));
	public static final RegistryObject<PointOfInterestType> CUPID_POI = register(DataInit
			.registerPOIFromBlocks(WizardEntity.PREFIX + "_cupid_job", () -> ImmutableSet.of(Blocks.GLOWSTONE), 2, 2));

	public static <T extends PointOfInterestType> RegistryObject<T> register(RegistryObject<T> t) {
		POIS.add(t);
		return t;
	}
}
