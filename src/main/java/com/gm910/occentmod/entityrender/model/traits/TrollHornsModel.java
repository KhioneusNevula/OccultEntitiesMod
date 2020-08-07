package com.gm910.occentmod.entityrender.model.traits;

import com.gm910.occentmod.entities.citizen.CitizenEntity;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

// Made with Blockbench 3.6.3
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

public class TrollHornsModel extends BipedModel<CitizenEntity> {
	private final ModelRenderer trollHorns;
	private final ModelRenderer righthorn;
	private final ModelRenderer upperrighthorn;
	private final ModelRenderer lefthorn;
	private final ModelRenderer upperlefthorn;

	public TrollHornsModel(float modelSize) {
		super(modelSize);
		textureWidth = 128;
		textureHeight = 128;

		trollHorns = new ModelRenderer(this);
		trollHorns.setRotationPoint(0.0F, -8.0F, 0.0F);
		bipedHead.addChild(trollHorns);
		setRotationAngle(trollHorns, -0.2618F, 0.0F, 0.0F);

		righthorn = new ModelRenderer(this);
		righthorn.setRotationPoint(1.9886F, -0.0273F, 0.26F);
		trollHorns.addChild(righthorn);
		setRotationAngle(righthorn, 0.0F, -0.6109F, 0.0F);
		righthorn.setTextureOffset(18, 100).addBox(0.0114F, -2.9727F, -0.26F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		upperrighthorn = new ModelRenderer(this);
		upperrighthorn.setRotationPoint(0.0114F, -2.9727F, -0.26F);
		righthorn.addChild(upperrighthorn);
		setRotationAngle(upperrighthorn, -0.3491F, 0.0F, 0.0F);
		upperrighthorn.setTextureOffset(18, 96).addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		lefthorn = new ModelRenderer(this);
		lefthorn.setRotationPoint(-1.9924F, 0.0182F, -0.1734F);
		trollHorns.addChild(lefthorn);
		setRotationAngle(lefthorn, 0.0F, 0.6109F, 0.0F);
		lefthorn.setTextureOffset(14, 100).addBox(-0.9886F, -2.9727F, -0.26F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		upperlefthorn = new ModelRenderer(this);
		upperlefthorn.setRotationPoint(-0.0076F, -3.0182F, 0.1734F);
		lefthorn.addChild(upperlefthorn);
		setRotationAngle(upperlefthorn, -0.3491F, 0.0F, 0.0F);
		upperlefthorn.setTextureOffset(14, 96).addBox(-0.981F, -2.9544F, -0.4334F, 1.0F, 3.0F, 1.0F, 0.0F, false);

	}

	@Override
	public void setRotationAngles(CitizenEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		// previously the render function, render code was moved to a method below
		super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
	}

	/*@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		Head.render(matrixStack, buffer, packedLight, packedOverlay);
		Body.render(matrixStack, buffer, packedLight, packedOverlay);
		RightArm.render(matrixStack, buffer, packedLight, packedOverlay);
		LeftArm.render(matrixStack, buffer, packedLight, packedOverlay);
		RightLeg.render(matrixStack, buffer, packedLight, packedOverlay);
		LeftLeg.render(matrixStack, buffer, packedLight, packedOverlay);
	}*/

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		trollHorns.showModel = visible;
		righthorn.showModel = visible;
		upperrighthorn.showModel = visible;
		lefthorn.showModel = visible;
		upperlefthorn.showModel = visible;
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}