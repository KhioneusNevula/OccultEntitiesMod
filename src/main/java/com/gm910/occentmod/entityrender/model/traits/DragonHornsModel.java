package com.gm910.occentmod.entityrender.model.traits;

import com.gm910.occentmod.entities.citizen.CitizenEntity;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

// Made with Blockbench 3.6.3
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

public class DragonHornsModel extends BipedModel<CitizenEntity> {
	private final ModelRenderer dragonhorns;
	private final ModelRenderer dragonrighthorn;
	private final ModelRenderer upperdragonrighthorn;
	private final ModelRenderer dragonlefthorn;
	private final ModelRenderer upperdragonlefthorn;

	public DragonHornsModel(float modelSize) {
		super(modelSize);
		textureWidth = 128;
		textureHeight = 128;

		dragonhorns = new ModelRenderer(this);
		dragonhorns.setRotationPoint(0.0F, -8.0F, 0.0F);
		bipedHead.addChild(dragonhorns);
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

	}

	@Override
	public void setRotationAngles(CitizenEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {

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
		dragonhorns.showModel = visible;
		dragonrighthorn.showModel = visible;
		upperdragonrighthorn.showModel = visible;
		dragonlefthorn.showModel = visible;
		upperdragonlefthorn.showModel = visible;

	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}