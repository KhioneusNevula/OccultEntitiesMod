package com.gm910.occentmod.api.sitting;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.NonNullMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.DeferredRegister;

public class SitEntity extends Entity {

	public static final NonNullMap<BlockState, Set<Predicate<Entity>>> SITTABLES = new NonNullMap<>(
			() -> Sets.newHashSet());

	public static RegistryObject<EntityType<SitEntity>> SIT_ENTITY_TYPE = null;

	public static void introduceSitEntity(String modid, DeferredRegister<EntityType<?>> register) {
		SIT_ENTITY_TYPE = register.register("sitter_entity", () -> {
			return EntityType.Builder.<SitEntity>create(SitEntity::new, EntityClassification.MISC).size(1.0f, 1.0f)
					.build(new ResourceLocation(modid, "sitter_entity").toString());
		});

		MinecraftForge.EVENT_BUS.register(SitEntity.class);
	}

	public static boolean isSittable(BlockState state, Entity en) {
		Set<Predicate<Entity>> preds = SITTABLES.get(state);
		for (Predicate<Entity> pred : preds) {
			if (pred.test(en)) {
				return true;
			}
		}
		return false;
	}

	public static void registerSittableBlockStatesFromPredicate(BlockState state, Set<Predicate<Entity>> entities) {
		SITTABLES.put(state, ImmutableSet.copyOf(entities));
	}

	public static void registerSittableBlockStatesFromPredicate(Set<BlockState> states,
			Set<Predicate<Entity>> entities) {
		for (BlockState state : states) {
			SITTABLES.put(state, ImmutableSet.copyOf(entities));
		}
	}

	public static void registerSittableBlockStatesFromPredicate(Block block, Set<Predicate<Entity>> entities) {
		for (BlockState state : block.getStateContainer().getValidStates()) {
			SITTABLES.put(state, ImmutableSet.copyOf(entities));
		}
	}

	public static void registerSittableBlockStates(BlockState state, Set<EntityType<?>> entities) {
		registerSittableBlockStatesFromPredicate(state,
				entities.stream().<Predicate<Entity>>map((e) -> ((f) -> f.getType() == e)).collect(Collectors.toSet()));
	}

	public static void registerSittableBlockStates(Set<BlockState> states, Set<EntityType<?>> entities) {
		registerSittableBlockStatesFromPredicate(states,
				entities.stream().<Predicate<Entity>>map((e) -> ((f) -> f.getType() == e)).collect(Collectors.toSet()));
	}

	public static void registerSittableBlockStates(Block block, Set<EntityType<?>> entities) {
		registerSittableBlockStatesFromPredicate(block,
				entities.stream().<Predicate<Entity>>map((e) -> ((f) -> f.getType() == e)).collect(Collectors.toSet()));
	}

	public SitEntity(EntityType<SitEntity> type, World world) {
		super(type, world);

	}

	public SitEntity(World world, BlockPos pos) {
		super(SIT_ENTITY_TYPE.get(), world);
		setPosition(pos.getX() + 0.5D, pos.getY() + 0.25, pos.getZ() + 0.5D);
		noClip = true;
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	protected void registerData() {
	}

	@Override
	protected void readAdditional(CompoundNBT tag) {
	}

	@Override
	protected void writeAdditional(CompoundNBT tag) {
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@SubscribeEvent
	public static void blockUpdate(NeighborNotifyEvent event) {
		if (event.getWorld().isRemote())
			return;
		List<SitEntity> sitters = event.getWorld().getEntitiesWithinAABB(SitEntity.class,
				new AxisAlignedBB(event.getPos()));
		if (!SitEntity.SITTABLES.containsKey(event.getState())) {
			sitters.forEach((e) -> e.remove());
		}
	}

	@SubscribeEvent
	public static void liv(LivingUpdateEvent event) {
		LivingEntity entity = (LivingEntity) event.getEntity();
		World world = entity.world;
		if (world.isRemote || entity instanceof PlayerEntity)
			return;
		BlockPos pos = entity.getPosition();
		EntitySize size = entity.getSize(entity.getPose());
		for (int x = (int) (-size.width - 0.5) - 1; x <= (int) (size.width + 0.5) + 1; x++) {
			for (int y = (int) (-size.height - 0.5) - 1; y <= (int) (size.height + 0.5) + 1; y++) {
				for (int z = (int) (-size.width - 0.5) - 1; z <= (int) (size.width + 0.5) + 1; z++) {
					BlockPos newpos = pos.add(x, y, z);
					if (!world.getBlockState(newpos).getShape(world, newpos).isEmpty()) {
						AxisAlignedBB box = world.getBlockState(newpos).getShape(world, newpos).getBoundingBox()
								.grow(0.5);
						if (box.intersects(entity.getBoundingBox().grow(0.5))) {
							trySitEntity(entity, newpos);
						}
					}
				}
			}
		}

	}

	public static SitEntity get(World world, BlockPos pos) {
		return world.getEntitiesWithinAABB(SitEntity.class, new AxisAlignedBB(pos)).stream().findAny().orElse(null);
	}

	public static boolean withinDistance(Entity entity, BlockPos sitPos) {
		return sitPos.distanceSq(entity.getPosition()) < 25;
	}

	public static boolean isOccupied(World world, BlockPos pos) {
		SitEntity en = get(world, pos);
		return en != null ? !en.getPassengers().isEmpty() : false;
	}

	public static void sitEntity(Entity en, BlockPos pos) {
		World world = en.world;
		SitEntity sitter = get(world, pos) == null ? new SitEntity(world, pos) : get(world, pos);
		world.addEntity(sitter);
		en.startRiding(sitter);
	}

	public static boolean trySitEntity(Entity entity, BlockPos pos) {
		World world = entity.world;
		if (isSittable(world.getBlockState(pos), entity) && withinDistance(entity, pos) && !isOccupied(world, pos)) {
			sitEntity(entity, pos);
			return true;
		}
		return false;
	}

	@SubscribeEvent
	public static void onBlockRightClick(RightClickBlock event) {
		if (event.getWorld().isRemote)
			return;
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		PlayerEntity entity = event.getPlayer();
		trySitEntity(entity, pos);
	}

	@SubscribeEvent
	public static void onMount(EntityMountEvent event) {
		if (!event.getWorldObj().isRemote && event.isDismounting()
				&& event.getEntityBeingMounted() instanceof SitEntity) {
			event.getEntityBeingMounted().remove();
		}
	}

	public static class EmptyRenderer extends EntityRenderer<SitEntity> {
		public EmptyRenderer(EntityRendererManager renderManager) {
			super(renderManager);
		}

		@Override
		public boolean shouldRender(SitEntity p_225626_1_, ClippingHelperImpl p_225626_2_, double p_225626_3_,
				double p_225626_5_, double p_225626_7_) {
			return false;
		}

		@Override
		public ResourceLocation getEntityTexture(SitEntity entity) {
			return null;
		}
	}
}