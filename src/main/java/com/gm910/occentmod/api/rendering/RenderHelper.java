package com.gm910.occentmod.api.rendering;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RenderHelper {
	public static void render(World world, Vec3d pos, BlockState blockstate, float entityYaw, float partialTicks,
			MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

		BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
		if (blockstate.getRenderType() == BlockRenderType.MODEL) {
			if (blockstate != world.getBlockState(new BlockPos(pos))
					&& blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
				matrixStackIn.push();
				BlockPos blockpos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
				matrixStackIn.translate(-0.5D, 0.0D, -0.5D);
				for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType
						.getBlockRenderTypes()) {
					if (RenderTypeLookup.canRenderInLayer(blockstate, type)) {
						net.minecraftforge.client.ForgeHooksClient.setRenderLayer(type);
						blockrendererdispatcher.getBlockModelRenderer().renderModel(world,
								blockrendererdispatcher.getModelForState(blockstate), blockstate, blockpos,
								matrixStackIn, bufferIn.getBuffer(type), false, new Random(),
								blockstate.getPositionRandom(new BlockPos(pos)), OverlayTexture.NO_OVERLAY);
					}
				}
				net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
				matrixStackIn.pop();
			}
		}
	}
}
