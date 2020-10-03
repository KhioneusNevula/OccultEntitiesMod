package com.gm910.occentmod.init;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientWalkTarget;
import com.gm910.occentmod.util.GMFiles;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class GMDeserialize<M> {

	private static final Map<ResourceLocation, GMDeserialize<?>> TYPES = new HashMap<>();

	public static final GMDeserialize<SapientWalkTarget> SAPIENT_WALK_TARGET = new GMDeserialize<SapientWalkTarget>(
			GMFiles.rl("sapient_walk_target"), SapientWalkTarget.class, (m, v) -> {
				return new SapientWalkTarget(m, v);
			}, SapientWalkTarget::serialize);

	public static final GMDeserialize<ItemUseContext> ITEM_USE_CONTEXT = new GMDeserialize<ItemUseContext>(
			GMFiles.rl("item_use_context"), ItemUseContext.class, (world, dynamic) -> {
				PlayerEntity user = dynamic.get("user").get().isPresent()
						? (PlayerEntity) ServerPos.getEntityFromUUID(UUID.fromString(dynamic.get("user").asString("")),
								world.getServer())
						: null;
				ItemStack stack = ItemStack
						.read((CompoundNBT) dynamic.get("stack").get().get().cast(NBTDynamicOps.INSTANCE));
				Hand hand = Hand.values()[dynamic.get("hand").asInt(0)];
				boolean isMiss = dynamic.get("isMiss").asBoolean(false);
				Vec3d hitVec = ServerPos.deserializeVec3d(dynamic.get("hitVec").get().get());
				Direction faceIn = Direction.byIndex(dynamic.get("faceIn").asInt(0));
				BlockPos posIn = BlockPos.deserialize(dynamic.get("posIn").get().get());
				boolean isInside = dynamic.get("isInside").asBoolean(false);

				BlockRayTraceResult res = isMiss ? BlockRayTraceResult.createMiss(hitVec, faceIn, posIn)
						: new BlockRayTraceResult(hitVec, faceIn, posIn, isInside);

				return ModReflect.construct(
						ItemUseContext.class, new Class[] { World.class, PlayerEntity.class, Hand.class,
								ItemStack.class, BlockRayTraceResult.class },
						new Object[] { world, user, hand, stack, res });
			}, (object, ops) -> {
				Map mapa = new HashMap<>();
				if (object.getPlayer() != null)
					mapa.put(ops.createString("user"), ops.createString(object.getPlayer().getCachedUniqueIdString()));
				CompoundNBT stackdat = object.getItem().serializeNBT();
				mapa.put(ops.createString("stack"),
						NBTDynamicOps.INSTANCE.cast(stackdat, NBTDynamicOps.INSTANCE.getType(stackdat)).get());
				mapa.put(ops.createString("hand"), ops.createInt(object.getHand().ordinal()));
				BlockRayTraceResult resultIn = ModReflect.getField(ItemUseContext.class, BlockRayTraceResult.class,
						"rayTraceResult", "field_221535_d", object);
				mapa.put(ops.createString("isMiss"), ops.createBoolean(resultIn.getType() == RayTraceResult.Type.MISS));
				mapa.put(ops.createString("hitVec"), ServerPos.serializeVec3d(resultIn.getHitVec(), ops));
				mapa.put(ops.createString("faceIn"), ops.createInt(resultIn.getFace().getIndex()));
				mapa.put(ops.createString("posIn"), resultIn.getPos().serialize(ops));
				mapa.put(ops.createString("isInside"), ops.createBoolean(resultIn.isInside()));
				return ops.createMap(mapa);
			});

	ResourceLocation resource;

	BiFunction<ServerWorld, Dynamic<?>, M> deserializer;

	BiFunction<M, DynamicOps<?>, ?> serializer;

	Class<M> clazz;

	public GMDeserialize(ResourceLocation rl, Class<M> clazz, BiFunction<ServerWorld, Dynamic<?>, M> deserializer,
			BiFunction<M, DynamicOps<?>, ?> serializer) {
		this.resource = rl;
		this.deserializer = deserializer;
		this.clazz = clazz;
		this.serializer = serializer;
		TYPES.put(rl, this);
	}

	public Class<M> getDeserializeClass() {
		return clazz;
	}

	public BiFunction<ServerWorld, Dynamic<?>, M> getDeserializer() {
		return deserializer;
	}

	public BiFunction<M, DynamicOps<?>, ?> getSerializer() {
		return serializer;
	}

	public ResourceLocation getResource() {
		return resource;
	}

	public M deserialize(ServerWorld world, Dynamic<?> dyn) {
		return deserializer.apply(world, dyn);
	}

	public <T> T serialize(M obj, DynamicOps<T> ops) {
		return (T) serializer.apply(obj, ops);
	}

	public static <M> GMDeserialize<M> get(ResourceLocation rl) {
		return (GMDeserialize<M>) TYPES.get(rl);
	}

	public static Collection<GMDeserialize<?>> getAll() {
		return TYPES.values();
	}

	public static <M> GMDeserialize<M> getFromClass(Class<M> obj) {
		return (GMDeserialize<M>) TYPES.values().stream().filter((e) -> e.clazz.equals(obj)).findAny().orElse(null);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + " " + this.resource;
	}
}
