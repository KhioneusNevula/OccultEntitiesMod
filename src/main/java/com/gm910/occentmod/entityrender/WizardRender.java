package com.gm910.occentmod.entityrender;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.entities.wizard.WizardEntity;
import com.gm910.occentmod.entities.wizard.WizardJob;
import com.gm910.occentmod.entityrender.model.WizardModel;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class WizardRender extends MobRenderer<WizardEntity, WizardModel> {

	public WizardRender(EntityRendererManager manager) {
		super(manager, new WizardModel(), 0.5f);

	}

	public static ResourceLocation fromJob(WizardJob job) {
		return new ResourceLocation(OccultEntities.MODID, "textures/entity/wizard/" + job.name + ".png");
	}

	@Override
	public ResourceLocation getEntityTexture(WizardEntity entity) {

		return fromJob(entity.getJob());
	}
}
