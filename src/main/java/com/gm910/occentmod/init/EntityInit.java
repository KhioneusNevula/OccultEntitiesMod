package com.gm910.occentmod.init;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.sitting.SitEntity;
import com.gm910.occentmod.entities.LivingBlockEntity;
import com.gm910.occentmod.entities.citizen.CitizenEntity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class EntityInit {
	private EntityInit() {
	}

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES,
			OccultEntities.MODID);

	static {
		SitEntity.introduceSitEntity(OccultEntities.MODID, ENTITY_TYPES);

	}

	public static void registerSittableEntities() {
		/*SitEntity.registerSittableBlockStatesFromPredicate(
				((ThroneBlock) BlockInit.THRONE.get()).getStatesForHalf(DoubleBlockHalf.LOWER),
				ImmutableSet.of((e) -> e instanceof LivingEntity));*/
	}

	public static final RegistryObject<EntityType<LivingBlockEntity>> LIVING_BLOCK = ENTITY_TYPES
			.register("living_block", () -> {
				return EntityType.Builder.<LivingBlockEntity>create(LivingBlockEntity::new, EntityClassification.MISC)
						.size(1.0f, 1.0f).build(new ResourceLocation(OccultEntities.MODID, "living_block").toString());
			});

	public static final RegistryObject<EntityType<CitizenEntity>> CITIZEN = ENTITY_TYPES.register("citizen", () -> {
		return EntityType.Builder.<CitizenEntity>create(CitizenEntity::new, EntityClassification.CREATURE)
				.size(1.0f, 1.0f).build(new ResourceLocation(OccultEntities.MODID, "citizen").toString());
	});

}
