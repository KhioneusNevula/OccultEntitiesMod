package com.gm910.occentmod.world.mindrealm;

import java.util.Iterator;

import com.gm910.occentmod.init.BiomeInit;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;

public class MindDimension extends Dimension {

	public MindDimension(World world, DimensionType type) {
		super(world, type, 0.0f);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new MindChunkGenerator(world, BiomeProviderType.FIXED.create(
				BiomeProviderType.FIXED.createSettings(this.world.getWorldInfo()).setBiome(BiomeInit.MIND.get())),
				new MindGenSettings(new BlockPos(0, 100, 0)));
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
		double d0 = MathHelper.frac((double) worldTime / 24000.0D - 0.25D);

		double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;

		return (float) (d0 * 2.0D + d1) / 3.0F;
	}

	@Override
	public boolean isSurfaceWorld() {
		return true;
	}

	@Override
	public Vec3d getFogColor(float celestialAngle, float partialTicks) {
		return new Vec3d(1.0, 0.7, 0.75);
	}

	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	public boolean doesXZShowFog(int x, int z) {
		return false;
	}

	@Override
	public SleepResult canSleepAt(PlayerEntity player, BlockPos pos) {
		return SleepResult.DENY;
	}

	@Override
	public double getMovementFactor() {
		// TODO Auto-generated method stub
		return 1.0;
	}

	public MindDimensionData getMindData() {
		if (this.world instanceof ServerWorld) {
			return MindDimensionData.get((ServerWorld) world);
		}
		return null;
	}

	@Override
	public void tick() {

		super.tick();

		if (world instanceof ServerWorld) {

			ServerWorld sworld = (ServerWorld) world;
			Iterator<Entity> iter = sworld.getEntities().iterator();

			MindDimensionData data = this.getMindData();

			while (iter.hasNext()) {
				Entity en = iter.next();
				// ServerWorld over = sworld.getServer().getWorld(DimensionType.OVERWORLD);

				PlayerEntity owner = data.getOwnerEntity();
				if (owner == en) {
					owner.abilities.allowFlying = true;
					owner.abilities.isFlying = true;
					owner.abilities.disableDamage = true;

				}

				if (en.getPosY() <= -1 && data.isFriendly(en)) {
					en.setPosition(en.getPosX(), 255, en.getPosZ());
				}
			}

		}
	}

	@Override
	public int getHeight() {
		return 512;
	}

	@Override
	public void onWorldSave() {
		super.onWorldSave();
	}

}
