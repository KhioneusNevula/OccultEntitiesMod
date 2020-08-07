package com.gm910.occentmod.entityrender.model.traits;

import com.gm910.occentmod.entities.citizen.CitizenEntity;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

// Made with Blockbench 3.6.3
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

public class DragonWingsModel extends BipedModel<CitizenEntity> {
	private final ModelRenderer dragonWings;
	private final ModelRenderer leftdragonwing;
	private final ModelRenderer leftdragonwingend;
	private final ModelRenderer rightdragonwing;
	private final ModelRenderer rightdragonwingend;

	public DragonWingsModel(float modelSize) {
		super(modelSize);
		textureWidth = 128;
		textureHeight = 128;

		dragonWings = new ModelRenderer(this);
		dragonWings.setRotationPoint(0.0F, 24.0F, 0.0F);
		bipedBody.addChild(dragonWings);

		leftdragonwing = new ModelRenderer(this);
		leftdragonwing.setRotationPoint(-1.0F, -19.0F, 2.0F);
		dragonWings.addChild(leftdragonwing);
		setRotationAngle(leftdragonwing, 0.0F, 0.829F, 0.0F);
		leftdragonwing.setTextureOffset(30, 16).addBox(-15.0F, -7.0F, 0.0F, 15.0F, 13.0F, 0.0F, 0.0F, false);

		leftdragonwingend = new ModelRenderer(this);
		leftdragonwingend.setRotationPoint(-15.0F, 0.0F, 0.0F);
		leftdragonwing.addChild(leftdragonwingend);
		setRotationAngle(leftdragonwingend, 0.0F, -0.5236F, 0.0F);
		leftdragonwingend.setTextureOffset(22, 32).addBox(-11.0F, -7.0F, 0.0F, 11.0F, 13.0F, 0.0F, 0.0F, false);

		rightdragonwing = new ModelRenderer(this);
		rightdragonwing.setRotationPoint(1.0F, -18.0F, 2.0F);
		dragonWings.addChild(rightdragonwing);
		setRotationAngle(rightdragonwing, 0.0F, -0.829F, 0.0F);
		rightdragonwing.setTextureOffset(0, 16).addBox(0.0F, -8.0F, 0.0F, 15.0F, 13.0F, 0.0F, 0.0F, false);

		rightdragonwingend = new ModelRenderer(this);
		rightdragonwingend.setRotationPoint(15.0F, -1.0F, 0.0F);
		rightdragonwing.addChild(rightdragonwingend);
		setRotationAngle(rightdragonwingend, 0.0F, 0.5236F, 0.0F);
		rightdragonwingend.setTextureOffset(0, 32).addBox(0.0F, -7.0F, 0.0F, 11.0F, 13.0F, 0.0F, 0.0F, false);

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
		dragonWings.showModel = visible;
		leftdragonwing.showModel = visible;
		leftdragonwingend.showModel = visible;
		rightdragonwing.showModel = visible;
		rightdragonwingend.showModel = visible;
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}