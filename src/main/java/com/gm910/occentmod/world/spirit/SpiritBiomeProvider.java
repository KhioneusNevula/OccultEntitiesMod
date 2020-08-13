package com.gm910.occentmod.world.spirit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.gm910.occentmod.api.util.ModReflect;
import com.google.common.collect.ImmutableSet;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;

public class SpiritBiomeProvider extends BiomeProvider {

	private Random rand;

	public static Set<Biome> biomelist = null;/*ImmutableSet.of(Biomes.OCEAN, 
												Biomes.PLAINS, 
												Biomes.DESERT, 
												Biomes.MOUNTAINS, 
												Biomes.FOREST, 
												Biomes.TAIGA, 
												Biomes.SWAMP, 
												Biomes.RIVER, 
												Biomes.FROZEN_OCEAN, 
												Biomes.FROZEN_RIVER, 
												Biomes.SNOWY_TUNDRA, 
												Biomes.SNOWY_MOUNTAINS, 
												Biomes.MUSHROOM_FIELDS, 
												Biomes.MUSHROOM_FIELD_SHORE, 
												Biomes.BEACH, 
												Biomes.DESERT_HILLS,
												Biomes.WOODED_HILLS, 
												Biomes.TAIGA_HILLS, 
												Biomes.MOUNTAIN_EDGE, 
												Biomes.JUNGLE, 
												Biomes.JUNGLE_HILLS, 
												Biomes.JUNGLE_EDGE, 
												Biomes.DEEP_OCEAN, 
												Biomes.STONE_SHORE, 
												Biomes.SNOWY_BEACH, 
												Biomes.BIRCH_FOREST, 
												Biomes.BIRCH_FOREST_HILLS, 
												Biomes.DARK_FOREST, 
												Biomes.SNOWY_TAIGA, 
												Biomes.SNOWY_TAIGA_HILLS, 
												Biomes.GIANT_TREE_TAIGA, 
												Biomes.GIANT_TREE_TAIGA_HILLS, 
												Biomes.WOODED_MOUNTAINS, 
												Biomes.SAVANNA, 
												Biomes.SAVANNA_PLATEAU, 
												Biomes.BADLANDS, 
												Biomes.WOODED_BADLANDS_PLATEAU, 
												Biomes.BADLANDS_PLATEAU, 
												Biomes.WARM_OCEAN, 
												Biomes.LUKEWARM_OCEAN, 
												Biomes.COLD_OCEAN, 
												Biomes.DEEP_WARM_OCEAN, 
												Biomes.DEEP_LUKEWARM_OCEAN, 
												Biomes.DEEP_COLD_OCEAN, 
												Biomes.DEEP_FROZEN_OCEAN, 
												Biomes.SUNFLOWER_PLAINS, 
												Biomes.DESERT_LAKES, 
												Biomes.GRAVELLY_MOUNTAINS, 
												Biomes.FLOWER_FOREST, 
												Biomes.TAIGA_MOUNTAINS, 
												Biomes.SWAMP_HILLS, 
												Biomes.ICE_SPIKES, 
												Biomes.MODIFIED_JUNGLE, 
												Biomes.MODIFIED_JUNGLE_EDGE, 
												Biomes.TALL_BIRCH_FOREST, 
												Biomes.TALL_BIRCH_HILLS, 
												Biomes.DARK_FOREST_HILLS, 
												Biomes.SNOWY_TAIGA_MOUNTAINS, 
												Biomes.GIANT_SPRUCE_TAIGA, 
												Biomes.GIANT_SPRUCE_TAIGA_HILLS, 
												Biomes.MODIFIED_GRAVELLY_MOUNTAINS, 
												Biomes.SHATTERED_SAVANNA, 
												Biomes.SHATTERED_SAVANNA_PLATEAU, 
												Biomes.ERODED_BADLANDS, 
												Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, 
												Biomes.MODIFIED_BADLANDS_PLATEAU);*/

	static {
		@SuppressWarnings("unchecked")
		ImmutableSet<Biome> imset = ModReflect.getField(OverworldBiomeProvider.class, ImmutableSet.class,
				"field_226847_e_", "field_226847_e_", null);

		List<Biome> actset = new ArrayList<>(imset);

		biomelist = ImmutableSet.copyOf(actset);
	}

	public SpiritBiomeProvider() {
		super(biomelist);
		rand = new Random();
	}

	public Biome getNoiseBiome(int x, int y, int z) {
		return Biomes.FLOWER_FOREST;
	}
}
