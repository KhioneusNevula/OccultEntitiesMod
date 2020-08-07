package com.gm910.occentmod.entityrender.model.traits;

import com.gm910.occentmod.entities.citizen.CitizenEntity;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

// Made with Blockbench 3.6.3
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

public class FairyWingsModel extends BipedModel<CitizenEntity> {
	private final ModelRenderer fairyWings;
	private final ModelRenderer leftwing;
	private final ModelRenderer rightwing;

	public FairyWingsModel(float modelSize) {
		super(modelSize);
		textureWidth = 128;
		textureHeight = 128;

		fairyWings = new ModelRenderer(this);
		fairyWings.setRotationPoint(0.0F, 24.0F, 0.0F);
		bipedBody.addChild(fairyWings);

		leftwing = new ModelRenderer(this);
		leftwing.setRotationPoint(-1.0F, -19.0F, 2.0F);
		fairyWings.addChild(leftwing);
		setRotationAngle(leftwing, 0.0F, 0.6545F, 0.0F);
		leftwing.setTextureOffset(34, 80).addBox(-9.0F, -5.0F, 0.0F, 9.0F, 11.0F, 0.0F, 0.0F, false);

		rightwing = new ModelRenderer(this);
		rightwing.setRotationPoint(1.0F, -18.0F, 2.0F);
		fairyWings.addChild(rightwing);
		setRotationAngle(rightwing, 0.0F, -0.6545F, 0.0F);
		rightwing.setTextureOffset(16, 80).addBox(0.0F, -6.0F, 0.0F, 9.0F, 11.0F, 0.0F, 0.0F, false);

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
		fairyWings.showModel = visible;
		leftwing.showModel = visible;
		rightwing.showModel = visible;
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}