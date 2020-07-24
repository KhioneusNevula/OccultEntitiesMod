package com.gm910.occentmod.world;

import java.util.function.BiFunction;

import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ModDimension;

public class OccultDimensionFactory extends ModDimension {
	
	private BiFunction<World, DimensionType, ? extends Dimension> factory;
	
	public OccultDimensionFactory(BiFunction<World, DimensionType, ? extends Dimension> factoire) {
		 factory = factoire;
	}

	@Override
	public BiFunction<World, DimensionType, ? extends Dimension> getFactory() {
		
		return factory;
	}
	
	
	/**
     * Convenience method for generating an ElkloriaDimension with a specific factory but no special
     * data handling behaviour. 
     *
     * @param factory Factory for creating {@link Dimension} instances from DimType and World.
     * @return A custom ModDimension with that factory.
     */
    public static OccultDimensionFactory withFactory(BiFunction<World, DimensionType, ? extends Dimension> factory) {
        return new OccultDimensionFactory(factory);
    }

}
