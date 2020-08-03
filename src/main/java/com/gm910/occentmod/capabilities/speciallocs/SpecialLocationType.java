package com.gm910.occentmod.capabilities.speciallocs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import com.gm910.occentmod.api.functionalinterfaces.ModFunc.TriFunction;
import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.api.util.NonNullMap;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class SpecialLocationType<T extends SpecialLocation> {

	public final Class<T> clazz;
	protected TriFunction<Dynamic<?>, BlockPos, SpecialLocationManager, T> deserializer;
	protected Predicate<SpecialLocation> predicate;
	protected BiFunction<ChunkEvent.Load, SpecialLocationManager, NonNullMap<BlockPos, List<T>>> generator;
	protected BiFunction<BlockPos, SpecialLocationManager, T> supplier;
	protected BiConsumer<NeighborNotifyEvent, SpecialLocationManager> onChange;

	public SpecialLocationType(Class<T> clazz,
			TriFunction<Dynamic<?>, BlockPos, SpecialLocationManager, T> deserializer,
			Predicate<SpecialLocation> predicate,
			BiFunction<ChunkEvent.Load, SpecialLocationManager, NonNullMap<BlockPos, List<T>>> generator,
			BiFunction<BlockPos, SpecialLocationManager, T> supplier,
			BiConsumer<NeighborNotifyEvent, SpecialLocationManager> onChange) {
		this.clazz = clazz;
		this.deserializer = deserializer == null ? (d, pos, man) -> ModReflect.construct(clazz, this, pos, man)
				: deserializer;
		this.predicate = predicate == null ? (e) -> e.getType() == this : null;
		this.generator = generator == null ? (a, b) -> new NonNullMap<>(() -> new ArrayList<>()) : generator;
		this.supplier = supplier == null ? (a, b) -> ModReflect.construct(clazz, this, a, b) : supplier;
		this.onChange = onChange == null ? (a, b) -> {
		} : onChange;
	}

	public SpecialLocationType(Class<T> clazz,
			TriFunction<Dynamic<?>, BlockPos, SpecialLocationManager, T> deserializer) {
		this(clazz, deserializer, null, null, null, null);
	}

	public SpecialLocationType<T> register(ResourceLocation loc) {
		return SpecialLocationManager.registerPointType(loc, this);
	}

	public NonNullMap<BlockPos, List<T>> generate(ChunkEvent.Load event, SpecialLocationManager manager) {

		return generator.apply(event, manager);
	}

	public T create(BlockPos pos, SpecialLocationManager manager) {
		return supplier.apply(pos, manager);
	}

	public void blockChange(BlockEvent.NeighborNotifyEvent event, SpecialLocationManager manager) {
		this.onChange.accept(event, manager);
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public TriFunction<Dynamic<?>, BlockPos, SpecialLocationManager, T> getDeserializer() {
		return deserializer;
	}

	public BiFunction<ChunkEvent.Load, SpecialLocationManager, NonNullMap<BlockPos, List<T>>> getGenerator() {
		return generator;
	}

	public BiConsumer<NeighborNotifyEvent, SpecialLocationManager> getOnChange() {
		return onChange;
	}

	public Predicate<SpecialLocation> getPredicate() {
		return predicate;
	}

	public BiFunction<BlockPos, SpecialLocationManager, T> getSupplier() {
		return supplier;
	}

}
