package com.gm910.occentmod.world;

import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

public class Warper {

	public static Entity teleportEntity(Entity en, DimensionType destDim, Vec3d destPos) {
		if (en.dimension == destDim) {
			en.setPosition(destPos.x, destPos.y, destPos.z);
			return en;
		}
		return en.changeDimension(destDim, new ITeleporter()
        {
            @Override
            public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity)
            {
                Entity repositionedEntity = repositionEntity.apply(false);
                repositionedEntity.setPositionAndUpdate(destPos.getX(), destPos.getY(), destPos.getZ());
                return repositionedEntity;
            }
        });
	}
	
	public static Entity teleportEntity(Entity en, DimensionType destDim) {
		return teleportEntity(en, destDim, en.getPositionVector());
	}
	
	

}
