package com.gm910.occentmod.entityrender.model.traits;

import com.gm910.occentmod.entities.citizen.CitizenEntity;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

// Made with Blockbench 3.6.3
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

public class DragonTailModel extends BipedModel<CitizenEntity> {
	private final ModelRenderer tail;
	private final ModelRenderer tailPart2;

	public DragonTailModel(float modelSize) {
		super(modelSize);
		textureWidth = 128;
		textureHeight = 128;

		tail = new ModelRenderer(this);
		tail.setRotationPoint(0.0F, 11.0F, 2.0F);
		bipedBody.addChild(tail);
		setRotationAngle(tail, 0.6545F, 0.0F, 0.0F);
		tail.setTextureOffset(18, 101).addBox(-1.0F, 6.0F, -1.0F, 2.0F, -6.0F, 2.0F, 0.0F, false);

		tailPart2 = new ModelRenderer(this);
		tailPart2.setRotationPoint(0.0F, 6.0858F, 0.0F);
		tail.addChild(tailPart2);
		setRotationAngle(tailPart2, -0.0436F, 0.0F, 0.0F);
		tailPart2.setTextureOffset(18, 104).addBox(-1.0F, 4.9142F, -1.0F, 2.0F, -5.0F, 2.0F, 0.0F, false);
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
		tail.showModel = visible;
		tailPart2.showModel = visible;
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}