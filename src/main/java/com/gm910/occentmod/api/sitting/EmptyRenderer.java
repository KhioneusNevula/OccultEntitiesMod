package com.gm910.occentmod.api.sitting;

import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class EmptyRenderer<E extends Entity> extends EntityRenderer<E> {
	public EmptyRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public boolean shouldRender(E p_225626_1_, ClippingHelperImpl p_225626_2_, double p_225626_3_, double p_225626_5_,
			double p_225626_7_) {
		return false;
	}

	@Override
	public ResourceLocation getEntityTexture(E entity) {
		return null;
	}
}