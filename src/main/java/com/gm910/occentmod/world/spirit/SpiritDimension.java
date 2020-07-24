package com.gm910.occentmod.world.spirit;

import java.util.Iterator;

import com.gm910.occentmod.world.Warper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;

public class SpiritDimension extends Dimension {

	public SpiritDimension(World world, DimensionType type) {
		super(world, type, 0.0f);
	}
	

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new SpiritChunkGenerator(world, new SpiritBiomeProvider(), new SpiritGenSettings());
	}

	@Override
	public BlockPos findSpawn(ChunkPos chunkPos, boolean checkValid) {
		return null;
	}

	@Override
	public BlockPos findSpawn(int posX, int posZ, boolean checkValid) {
		return null;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks) {
		worldTime -= 5000;
		double d0 = MathHelper.frac((double)worldTime / 24000.0D - 0.25D);
		
		double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
		
		return (float)(d0 * 2.0D + d1) / 3.0F;
	}

	@Override
	public boolean isSurfaceWorld() {
		return false;
	}

	@Override
	public Vec3d getFogColor(float celestialAngle, float partialTicks) {
		return new Vec3d(1.0, 0.7, 0.75);
	}

	@Override
	public boolean canRespawnHere() {
		return true;
	}

	@Override
	public boolean doesXZShowFog(int x, int z) {
		return false;
	}
	
	@Override
	public SleepResult canSleepAt(PlayerEntity player, BlockPos pos) {
		return this.canRespawnHere() ? SleepResult.ALLOW : SleepResult.DENY;
	}
	
	@Override
	public double getMovementFactor() {
		// TODO Auto-generated method stub
		return 1.0;
	}

	@Override
	public void tick() {
		
		super.tick();
		
		if (world instanceof ServerWorld) {
		
			ServerWorld sworld = (ServerWorld)world;
			Iterator<Entity> iter = sworld.getEntities().iterator();
			
			while (iter.hasNext()) {
				Entity en = iter.next();
				ServerWorld over = sworld.getServer().getWorld(DimensionType.OVERWORLD);

				BlockPos toPos = over.getSpawnPoint();

				ChunkPos cpos = new ChunkPos(toPos);
				
				if (en.getPosY() <= -10) {
					Warper.teleportEntity(en, DimensionType.OVERWORLD, new Vec3d(toPos));
				} else if (en.getPosY() <= -1) {
					over.forceChunk(cpos.x, cpos.z, true);
					sworld.getServer().getWorld(DimensionType.OVERWORLD);
				}
			}
			
		}
	}
	
	@Override
	public int getHeight() {
		return 512;
	}
	
	
}
