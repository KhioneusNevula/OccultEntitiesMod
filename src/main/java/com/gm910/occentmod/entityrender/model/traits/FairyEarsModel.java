package com.gm910.occentmod.entityrender.model.traits;

import com.gm910.occentmod.entities.citizen.CitizenEntity;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

// Made with Blockbench 3.6.3
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

public class FairyEarsModel extends BipedModel<CitizenEntity> {
	private final ModelRenderer fairyEars;
	private final ModelRenderer earleft;
	private final ModelRenderer earright;

	public FairyEarsModel(float modelSize) {
		super(modelSize);
		textureWidth = 128;
		textureHeight = 128;
		fairyEars = new ModelRenderer(this);
		fairyEars.setRotationPoint(-0.0872F, -6.1041F, 0.9907F);
		bipedHead.addChild(fairyEars);
		setRotationAngle(fairyEars, -0.1309F, 0.0F, 0.0F);

		earleft = new ModelRenderer(this);
		earleft.setRotationPoint(4.0872F, 1.1041F, -1.9907F);
		fairyEars.addChild(earleft);
		setRotationAngle(earleft, 0.0F, 0.3054F, 0.0F);
		earleft.setTextureOffset(52, 80).addBox(0.0F, -4.0F, 0.0F, 0.0F, 5.0F, 3.0F, 0.0F, false);

		earright = new ModelRenderer(this);
		earright.setRotationPoint(-3.9128F, 1.1041F, -1.9907F);
		fairyEars.addChild(earright);
		setRotationAngle(earright, 0.0F, -0.3054F, 0.0F);
		earright.setTextureOffset(0, 96).addBox(0.0F, -4.0F, 0.0F, 0.0F, 5.0F, 3.0F, 0.0F, false);

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
		fairyEars.showModel = visible;
		earleft.showModel = visible;
		earright.showModel = visible;
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}