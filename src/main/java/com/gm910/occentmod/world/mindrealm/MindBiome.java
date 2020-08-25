/**
 * 
 */
package com.gm910.occentmod.world.mindrealm;

import com.gm910.occentmod.api.language.Translate;

import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

/**
 * @author borah
 *
 */
public class MindBiome extends Biome {

	public MindBiome() {
		super(new Biome.Builder().category(Biome.Category.THEEND).waterColor(0x00FFFF).waterFogColor(0x00FFFF)
				.surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG)
				.precipitation(RainType.RAIN).depth(2).scale(2).temperature(2).downfall(2));
		for (EntityClassification eclass : EntityClassification.values()) {
			getSpawns(eclass).clear();
		}

		/*addSpawn(EntityClassification.AMBIENT, new SpawnListEntry(EntityType.PHANTOM, 50, 1, 10));
		addSpawn(EntityClassification.AMBIENT, new SpawnListEntry(EntityType.BEE, 50, 1, 10));
		addSpawn(EntityClassification.AMBIENT, new SpawnListEntry(EntityType.BLAZE, 50, 1, 10));
		addSpawn(EntityClassification.AMBIENT, new SpawnListEntry(EntityType.VEX, 50, 1, 10));
		addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.WITHER_SKELETON, 50, 1, 3));
		addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.GUARDIAN, 50, 1, 1));
		addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.SQUID, 50, 1, 4));
		addSpawn(EntityClassification.AMBIENT, new SpawnListEntry(EntityType.FIREBALL, 50, 1, 10));
		addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.DROWNED, 50, 1, 10));*/

	}

	@Override
	public boolean doesWaterFreeze(IWorldReader worldIn, BlockPos water, boolean mustBeAtEdge) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doesWaterFreeze(IWorldReader worldIn, BlockPos pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doesSnowGenerate(IWorldReader worldIn, BlockPos pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		// TODO Auto-generated method stub
		return Translate.make("biome.mind");
	}

	@Override
	public int getFoliageColor() {
		// TODO Auto-generated method stub
		return 0x00AAFF;
	}

	@Override
	public int getGrassColor(double posX, double posZ) {
		// TODO Auto-generated method stub
		return getFoliageColor();
	}

	@Override
	public RainType getPrecipitation() {
		// TODO Auto-generated method stub
		return RainType.NONE;
	}

	@Override
	public int getSkyColor() {
		// TODO Auto-generated method stub
		return 0xDDDC00;
	}

	@Override
	public TempCategory getTempCategory() {
		// TODO Auto-generated method stub
		return TempCategory.WARM;
	}

	@Override
	public float getTemperatureRaw(BlockPos pos) {
		// TODO Auto-generated method stub
		return 0.0f;
	}

}
