package com.gm910.occentmod.entityrender;

import java.util.Random;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.entities.LivingBlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LivingBlockRender extends EntityRenderer<LivingBlockEntity>{

	public LivingBlockRender(EntityRendererManager manager) {
		super(manager);
		this.shadowSize = 0.5f;
		
	}
	
	@Override
	public void render(LivingBlockEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
	      BlockState blockstate = entityIn.getBlock().getState();
	      if (blockstate.getRenderType() == BlockRenderType.MODEL) {
	         World world = entityIn.world;
	         if (blockstate != world.getBlockState(new BlockPos(entityIn)) && blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
	            matrixStackIn.push();
	            BlockPos blockpos = new BlockPos(entityIn.getPosX(), entityIn.getBoundingBox().maxY, entityIn.getPosZ());
	            matrixStackIn.translate(-0.5D, 0.0D, -0.5D);
	            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
	            for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType.getBlockRenderTypes()) {
	               if (RenderTypeLookup.canRenderInLayer(blockstate, type)) {
	                  net.minecraftforge.client.ForgeHooksClient.setRenderLayer(type);
	                  blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(blockstate), blockstate, blockpos, matrixStackIn, bufferIn.getBuffer(type), false, new Random(), blockstate.getPositionRandom(entityIn.getOriginPos()), OverlayTexture.NO_OVERLAY);
	               }
	            }
	            net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
	            matrixStackIn.pop();
	            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	         }
	      }
	   }
	
	@Override
	public ResourceLocation getEntityTexture(LivingBlockEntity entity) {
		
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
}
