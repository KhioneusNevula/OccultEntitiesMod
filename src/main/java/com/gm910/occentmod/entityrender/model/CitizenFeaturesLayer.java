package com.gm910.occentmod.entityrender.model;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Genetics;
import com.gm910.occentmod.entityrender.CitizenRender;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;

public class CitizenFeaturesLayer<E extends BipedModel<CitizenEntity>>
		extends LayerRenderer<CitizenEntity, CitizenModel> {

	private E layer;

	private BiPredicate<CitizenEntity, Genetics<CitizenEntity>> should;

	private BiFunction<CitizenEntity, Genetics<CitizenEntity>, ResourceLocation> getTextures = (e, m) -> CitizenRender
			.constructTexture(e);

	public CitizenFeaturesLayer(IEntityRenderer<CitizenEntity, CitizenModel> entityRendererIn, E layer,
			BiPredicate<CitizenEntity, Genetics<CitizenEntity>> shouldRender) {
		super(entityRendererIn);
		this.layer = layer;
		this.should = shouldRender;

	}

	public CitizenFeaturesLayer<E> setGetTextures(
			BiFunction<CitizenEntity, Genetics<CitizenEntity>, ResourceLocation> getTextures) {
		this.getTextures = getTextures;
		return this;
	}

	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn,
			CitizenEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch) {
		if (shouldRender(entitylivingbaseIn)) {
			IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(bufferIn,
					RenderType.getEntityCutoutNoCull(tex(entitylivingbaseIn)), false, false);
			this.layer.render(matrixStackIn, ivertexbuilder, packedLightIn,
					LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0f), 1f, 1f, 1f, 1f);
		}

	}

	public boolean shouldRender(CitizenEntity en) {

		return should.test(en, en.getGenetics());
	}

	public ResourceLocation tex(CitizenEntity en) {
		return this.getTextures.apply(en, en.getGenetics());
	}

}
