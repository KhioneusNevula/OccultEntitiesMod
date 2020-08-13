package com.gm910.occentmod.init;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.sitting.EmptyRenderer;
import com.gm910.occentmod.api.sitting.SitEntity;
import com.gm910.occentmod.entityrender.CitizenRender;
import com.gm910.occentmod.entityrender.LivingBlockRender;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = OccultEntities.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RenderInit {
	private RenderInit() {
	}

	static {
		System.out.println("Render init class loaded");
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		System.out.println("ClientSetupEvent");
		RenderingRegistry.registerEntityRenderingHandler(EntityInit.LIVING_BLOCK.get(), LivingBlockRender::new);
		// RenderingRegistry.registerEntityRenderingHandler(EntityInit.WIZARD.get(),
		// WizardRender::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityInit.CITIZEN.get(), CitizenRender::new);
		RenderingRegistry.registerEntityRenderingHandler(SitEntity.SIT_ENTITY_TYPE.get(),
				EmptyRenderer<SitEntity>::new);
	}
}
