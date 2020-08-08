package com.gm910.occentmod.world.structures;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.gm910.occentmod.empires.EmpireData;
import com.gm910.occentmod.init.EntityInit;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class EmpireCenterStructure extends Structure<NoFeatureConfig> {
	private static final List<Biome.SpawnListEntry> SPAWN_LIST = Lists
			.newArrayList(new Biome.SpawnListEntry(EntityInit.CITIZEN.get(), 1, 1, 1));
	private static final List<Biome.SpawnListEntry> CREATURE_SPAWN_LIST = Lists
			.newArrayList(new Biome.SpawnListEntry(EntityInit.LIVING_BLOCK.get(), 1, 1, 1));

	public EmpireCenterStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51424_1_) {
		super(p_i51424_1_);
	}

	public String getStructureName() {
		return EmpireData.structureTypeName.toString();
	}

	public int getSize() {
		return 3;
	}

	public Structure.IStartFactory getStartFactory() {
		return EmpireCenterStructure.Start::new;
	}

	protected int getSeedModifier() {
		return 14357620;
	}

	public List<Biome.SpawnListEntry> getSpawnList() {
		return SPAWN_LIST;
	}

	public List<Biome.SpawnListEntry> getCreatureSpawnList() {
		return CREATURE_SPAWN_LIST;
	}

	public boolean func_202383_b(IWorld worldIn, BlockPos pos) {
		StructureStart structurestart = this.getStart(worldIn, pos, true);
		if (structurestart != StructureStart.DUMMY && structurestart instanceof EmpireCenterStructure.Start
				&& !structurestart.getComponents().isEmpty()) {
			StructurePiece structurepiece = structurestart.getComponents().get(0);
			return structurepiece instanceof EmpireCenterPiece;
		} else {
			return false;
		}
	}

	@Override
	public boolean canBeGenerated(BiomeManager biomeManagerIn, ChunkGenerator<?> generatorIn, Random randIn, int chunkX,
			int chunkZ, Biome biomeIn) {
		ChunkPos chunkpos = this.getStartPositionForPosition(generatorIn, randIn, chunkX, chunkZ, 0, 0);
		return chunkX == chunkpos.x && chunkZ == chunkpos.z ? generatorIn.hasStructure(biomeIn, this) : false;
	}

	public static class Start extends StructureStart {
		public Start(Structure<?> p_i225819_1_, int p_i225819_2_, int p_i225819_3_, MutableBoundingBox boundingBox,
				int p_i225819_5_, long p_i225819_6_) {
			super(p_i225819_1_, p_i225819_2_, p_i225819_3_, boundingBox, p_i225819_5_, p_i225819_6_);
		}

		public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ,
				Biome biomeIn) {
			EmpireCenterPiece swamphutpiece = new EmpireCenterPiece(this.rand, chunkX * 16, chunkZ * 16);
			this.components.add(swamphutpiece);
			this.recalculateStructureSize();
		}

	}

}