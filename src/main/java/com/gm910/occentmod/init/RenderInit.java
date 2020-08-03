package com.gm910.occentmod.init;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.sitting.SitEntity;
import com.gm910.occentmod.entityrender.LivingBlockRender;
import com.gm910.occentmod.entityrender.WizardRender;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = OccultEntities.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public final class RenderInit {
	private RenderInit() {
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityInit.LIVING_BLOCK.get(), LivingBlockRender::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityInit.WIZARD.get(), WizardRender::new);
		if (SitEntity.SIT_ENTITY_TYPE != null)
			RenderingRegistry.registerEntityRenderingHandler(SitEntity.SIT_ENTITY_TYPE.get(),
					SitEntity.EmptyRenderer::new);
	}
}
