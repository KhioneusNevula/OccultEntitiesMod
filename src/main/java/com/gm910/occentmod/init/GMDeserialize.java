package com.gm910.occentmod.init;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientWalkTarget;
import com.gm910.occentmod.util.GMFiles;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class GMDeserialize<M> {

	private static final Map<ResourceLocation, GMDeserialize<?>> TYPES = new HashMap<>();

	public static final GMDeserialize<SapientWalkTarget> SAPIENT_WALK_TARGET = new GMDeserialize<SapientWalkTarget>(
			GMFiles.rl("sapient_walk_target"), SapientWalkTarget.class, (m, v) -> {
				return new SapientWalkTarget(m, v);
			}, SapientWalkTarget::serialize);

	public static final GMDeserialize<ItemUseContext> ITEM_USE_CONTEXT = new GMDeserialize<ItemUseContext>(
			GMFiles.rl("item_use_context"), ItemUseContext.class, (world, dynamic) -> {
				LivingEntity user = (LivingEntity) ServerPos
						.getEntityFromUUID(UUID.fromString(dynamic.get("user").asString("")), world.getServer());

				return new ItemUseContext(null, Hand.MAIN_HAND, null);
			}, (object, ops) -> {
				return null;
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

	public static <M extends IDynamicSerializable> GMDeserialize<M> get(ResourceLocation rl) {
		return (GMDeserialize<M>) TYPES.get(rl);
	}

	public static Collection<GMDeserialize<?>> getAll() {
		return TYPES.values();
	}

	public static <M extends IDynamicSerializable> GMDeserialize<M> getFromClass(Class<M> obj) {
		return (GMDeserialize<M>) TYPES.values().stream().filter((e) -> e.clazz.equals(obj)).findAny().orElse(null);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + " " + this.resource;
	}
}
