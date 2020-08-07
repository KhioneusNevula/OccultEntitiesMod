package com.gm910.occentmod.entityrender;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.genetype.GeneType;
import com.gm910.occentmod.entityrender.model.CitizenFeaturesLayer;
import com.gm910.occentmod.entityrender.model.CitizenModel;
import com.gm910.occentmod.entityrender.model.traits.DragonHornsModel;
import com.gm910.occentmod.entityrender.model.traits.DragonTailModel;
import com.gm910.occentmod.entityrender.model.traits.DragonWingsModel;
import com.gm910.occentmod.entityrender.model.traits.FairyEarsModel;
import com.gm910.occentmod.entityrender.model.traits.FairyWingsModel;
import com.gm910.occentmod.entityrender.model.traits.TrollHornsModel;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;

public class CitizenRender extends MobRenderer<CitizenEntity, CitizenModel> {

	public CitizenRender(EntityRendererManager manager) {
		super(manager, new CitizenModel(0.0f), 0.5f);
		this.addLayer(new CitizenFeaturesLayer<BipedModel<CitizenEntity>>(this, new DragonWingsModel(0.0f), (e, g) -> {
			return g.getValue(GeneType.WINGS).isDominant();
		}));
		this.addLayer(new CitizenFeaturesLayer<BipedModel<CitizenEntity>>(this, new FairyWingsModel(0.0f), (e, g) -> {
			return g.getValue(GeneType.WINGS).isRecessive();
		}));
		this.addLayer(new CitizenFeaturesLayer<BipedModel<CitizenEntity>>(this, new FairyEarsModel(0.0f), (e, g) -> {
			return g.getValue(GeneType.FAIRY_EARS);
		}));
		this.addLayer(new CitizenFeaturesLayer<BipedModel<CitizenEntity>>(this, new DragonHornsModel(0.0f), (e, g) -> {
			return g.getValue(GeneType.HORNS).isRecessive();
		}));
		this.addLayer(new CitizenFeaturesLayer<BipedModel<CitizenEntity>>(this, new TrollHornsModel(0.0f), (e, g) -> {
			return g.getValue(GeneType.HORNS).isDominant();
		}));

		this.addLayer(new CitizenFeaturesLayer<BipedModel<CitizenEntity>>(this, new DragonTailModel(0.0f), (e, g) -> {
			return g.getValue(GeneType.DRAGON_TAIL);
		}));
		// this.addLayer(new FairyEarsLayer<CitizenEntity>(this));
		// this.addLayer(new DragonHornsLayer<CitizenEntity>(this));
		// this.addLayer(new TrollHornsLayer<CitizenEntity>(this));
		// this.addLayer(new DragonWingsLayer<CitizenEntity>(this));
		// this.addLayer(new FairyWingsLayer<CitizenEntity>(this));
		// this.addLayer(new DragonTailLayer<CitizenEntity>(this));
	}

	public static ResourceLocation constructTexture(CitizenEntity c) {

		return new ResourceLocation(OccultEntities.MODID, "textures/entity/citizen/citizen_texture_template.png"); // TODO
																													// /citizen/...
																													// special
																													// logic,
																													// etc
	}

	@Override
	public ResourceLocation getEntityTexture(CitizenEntity entity) {

		return constructTexture(entity);
	}

}
