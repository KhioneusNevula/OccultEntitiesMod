package com.gm910.occentmod.entityrender.model;

import com.gm910.occentmod.entities.citizen.CitizenEntity;

import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;

// Made with Blockbench 3.6.3
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

public class CitizenModelFeaturesEntireModel extends PlayerModel<CitizenEntity> {
	private final ModelRenderer Head;
	private final ModelRenderer trollHorns;
	private final ModelRenderer righthorn;
	private final ModelRenderer upperrighthorn;
	private final ModelRenderer lefthorn;
	private final ModelRenderer upperlefthorn;
	private final ModelRenderer dragonhorns;
	private final ModelRenderer dragonrighthorn;
	private final ModelRenderer upperdragonrighthorn;
	private final ModelRenderer dragonlefthorn;
	private final ModelRenderer upperdragonlefthorn;
	private final ModelRenderer fairyEars;
	private final ModelRenderer earleft;
	private final ModelRenderer earright;
	private final ModelRenderer Body;
	private final ModelRenderer fairyWings;
	private final ModelRenderer leftwing;
	private final ModelRenderer rightwing;
	private final ModelRenderer dragonWings;
	private final ModelRenderer leftdragonwing;
	private final ModelRenderer leftdragonwingend;
	private final ModelRenderer rightdragonwing;
	private final ModelRenderer rightdragonwingend;
	private final ModelRenderer Tail_use_xrotation;
	private final ModelRenderer tail2;
	private final ModelRenderer RightArm;
	private final ModelRenderer LeftArm;
	private final ModelRenderer RightLeg;
	private final ModelRenderer LeftLeg;

	public CitizenModelFeaturesEntireModel(float modelSize) {
		super(modelSize, false);
		textureWidth = 128;
		textureHeight = 128;

		Head = new ModelRenderer(this);
		Head.setRotationPoint(0.0F, 0.0F, 0.0F);
		Head.setTextureOffset(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
		Head.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.5F, false);

		trollHorns = new ModelRenderer(this);
		trollHorns.setRotationPoint(0.0F, -8.0F, 0.0F);
		Head.addChild(trollHorns);
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

		dragonhorns = new ModelRenderer(this);
		dragonhorns.setRotationPoint(0.0F, -8.0F, 0.0F);
		Head.addChild(dragonhorns);
		setRotationAngle(dragonhorns, -0.2618F, 0.0F, 0.0F);

		dragonrighthorn = new ModelRenderer(this);
		dragonrighthorn.setRotationPoint(1.9886F, -0.0273F, 0.26F);
		dragonhorns.addChild(dragonrighthorn);
		setRotationAngle(dragonrighthorn, 0.0F, -0.8727F, 0.0F);
		dragonrighthorn.setTextureOffset(10, 100).addBox(0.0114F, -2.9727F, -0.26F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		upperdragonrighthorn = new ModelRenderer(this);
		upperdragonrighthorn.setRotationPoint(0.0114F, -2.9727F, -0.26F);
		dragonrighthorn.addChild(upperdragonrighthorn);
		setRotationAngle(upperdragonrighthorn, -0.3491F, 0.0F, 0.0F);
		upperdragonrighthorn.setTextureOffset(10, 96).addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		dragonlefthorn = new ModelRenderer(this);
		dragonlefthorn.setRotationPoint(-1.9924F, 0.0182F, -0.1734F);
		dragonhorns.addChild(dragonlefthorn);
		setRotationAngle(dragonlefthorn, 0.0F, 0.8727F, 0.0F);
		dragonlefthorn.setTextureOffset(6, 100).addBox(-0.9886F, -2.9727F, -0.26F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		upperdragonlefthorn = new ModelRenderer(this);
		upperdragonlefthorn.setRotationPoint(-0.0076F, -3.0182F, 0.1734F);
		dragonlefthorn.addChild(upperdragonlefthorn);
		setRotationAngle(upperdragonlefthorn, -0.3491F, 0.0F, 0.0F);
		upperdragonlefthorn.setTextureOffset(6, 96).addBox(-0.981F, -2.9544F, -0.4334F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		fairyEars = new ModelRenderer(this);
		fairyEars.setRotationPoint(-0.0872F, -6.1041F, 0.9907F);
		Head.addChild(fairyEars);
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

		Body = new ModelRenderer(this);
		Body.setRotationPoint(0.0F, 0.0F, 0.0F);
		Body.setTextureOffset(60, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);
		Body.setTextureOffset(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.25F, false);

		fairyWings = new ModelRenderer(this);
		fairyWings.setRotationPoint(0.0F, 24.0F, 0.0F);
		Body.addChild(fairyWings);

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

		dragonWings = new ModelRenderer(this);
		dragonWings.setRotationPoint(0.0F, 24.0F, 0.0F);
		Body.addChild(dragonWings);

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

		Tail_use_xrotation = new ModelRenderer(this);
		Tail_use_xrotation.setRotationPoint(0.0F, 11.0F, 2.0F);
		Body.addChild(Tail_use_xrotation);
		setRotationAngle(Tail_use_xrotation, 0.6545F, 0.0F, 0.0F);
		Tail_use_xrotation.setTextureOffset(18, 101).addBox(-1.0F, 6.0F, -1.0F, 2.0F, -6.0F, 2.0F, 0.0F, false);

		tail2 = new ModelRenderer(this);
		tail2.setRotationPoint(0.0F, 6.0858F, 0.0F);
		Tail_use_xrotation.addChild(tail2);
		setRotationAngle(tail2, -0.0436F, 0.0F, 0.0F);
		tail2.setTextureOffset(18, 104).addBox(-1.0F, 4.9142F, -1.0F, 2.0F, -5.0F, 2.0F, 0.0F, false);

		RightArm = new ModelRenderer(this);
		RightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		RightArm.setTextureOffset(0, 80).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
		RightArm.setTextureOffset(32, 64).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, false);

		LeftArm = new ModelRenderer(this);
		LeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		LeftArm.setTextureOffset(16, 64).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
		LeftArm.setTextureOffset(0, 64).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, false);

		RightLeg = new ModelRenderer(this);
		RightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		setRotationAngle(RightLeg, 0.0F, 0.0F, 0.0349F);
		RightLeg.setTextureOffset(32, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
		RightLeg.setTextureOffset(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, false);

		LeftLeg = new ModelRenderer(this);
		LeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		setRotationAngle(LeftLeg, 0.0F, 0.0F, -0.0349F);
		LeftLeg.setTextureOffset(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
		LeftLeg.setTextureOffset(44, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, false);
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

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}