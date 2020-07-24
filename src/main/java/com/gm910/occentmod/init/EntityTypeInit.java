package com.gm910.occentmod.init;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.entities.LivingBlockEntity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class EntityTypeInit { private EntityTypeInit() {}

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, OccultEntities.MODID);
	
	
	public static final RegistryObject<EntityType<LivingBlockEntity>> LIVING_BLOCK = ENTITY_TYPES.register("living_block", () -> {
		return EntityType.Builder.<LivingBlockEntity>create(LivingBlockEntity::new, 
				EntityClassification.MISC).size(1.0f, 1.0f).build(new ResourceLocation(OccultEntities.MODID, "living_block").toString());
	});
	
}
