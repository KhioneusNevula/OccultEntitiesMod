package com.gm910.occentmod.init;

import com.gm910.occentmod.OccultEntities;

import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StructureInit {

	public static final DeferredRegister<Feature<?>> FEATURES = new DeferredRegister<>(ForgeRegistries.FEATURES,
			OccultEntities.MODID);

	public static void registerStructures() {
		// Iterator<Biome> biomes = ForgeRegistries.BIOMES.iterator();
		// biomes.forEachRemaining((biome) -> {
		// biome.addStructure(BRICK_HOUSE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
		// biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES,
		// BRICK_HOUSE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
		// });
	}

}
