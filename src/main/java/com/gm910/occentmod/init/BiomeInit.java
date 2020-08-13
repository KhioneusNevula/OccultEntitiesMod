package com.gm910.occentmod.init;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.world.mindrealm.MindBiome;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeInit {

	public static final DeferredRegister<Biome> BIOMES = new DeferredRegister<>(ForgeRegistries.BIOMES,
			OccultEntities.MODID);

	public static final RegistryObject<Biome> MIND = BIOMES.register("mind", () -> new MindBiome());

}
