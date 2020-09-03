package com.gm910.occentmod.sapience.mind_and_traits.memory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.gm910.occentmod.api.language.Translate;
import com.gm910.occentmod.init.GMDeserialize;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.CauseEffectMemory;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.ExternallyGivenMemory;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.IdeaMemory;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.Memory;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.MemoryOfBlockRegion;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.MemoryOfBlockstate;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.MemoryOfDeed;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.MemoryOfOccurrence;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.MemoryOfSerializable;
import com.gm910.occentmod.util.GMFiles;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

public class MemoryType<T extends Memory<?>> {

	private static final Map<ResourceLocation, MemoryType<?>> TYPES = new HashMap<>();

	public static final MemoryType<MemoryOfDeed<?>> DEED = new MemoryType<MemoryOfDeed<?>>(GMFiles.rl("deed"),
			MemoryOfDeed::new) {
		@Override
		public <M extends LivingEntity> ITextComponent display(Memory<M> obj) {
			MemoryOfDeed<M> gos = (MemoryOfDeed<M>) obj;
			return gos.getEvent().getDisplay(obj.getOwner());
		}
	};

	public static final MemoryType<ExternallyGivenMemory<?>> EXTERNALLY_GIVEN_MEMORY = new MemoryType<ExternallyGivenMemory<?>>(
			GMFiles.rl("externally_given"), ExternallyGivenMemory::new) {
		@Override
		public <M extends LivingEntity> ITextComponent display(Memory<M> obj) {
			return ((ExternallyGivenMemory<M>) obj).getDelegate().getType().display(obj);
		}
	};

	public static final MemoryType<MemoryOfOccurrence<?>> EVENT = new MemoryType<MemoryOfOccurrence<?>>(
			GMFiles.rl("event"), MemoryOfOccurrence::new) {
		@Override
		public <M extends LivingEntity> ITextComponent display(Memory<M> obj) {
			MemoryOfOccurrence<M> gos = (MemoryOfOccurrence<M>) obj;
			return gos.getEvent().getDisplay(obj.getOwner());
		}
	};

	public static final MemoryType<MemoryOfBlockstate<?>> BLOCKSTATE = new MemoryType<MemoryOfBlockstate<?>>(
			GMFiles.rl("blockstate"), (t, u) -> {
				try {
					return new MemoryOfBlockstate<>(t, u);
				} catch (CommandSyntaxException e) {
					return null;
				}
			}) {
		public <M extends LivingEntity> ITextComponent display(Memory<M> obj) {

			MemoryOfBlockstate<M> mem = ((MemoryOfBlockstate<M>) obj);

			return Translate.make("memory.blockstate", mem.getStoredState().getBlock().getNameTextComponent(),
					mem.getStoredPos().getX(), mem.getStoredPos().getY(), mem.getStoredPos().getZ(),
					mem.getStoredPos().getDimension().getRegistryName());
		}
	};

	public static final MemoryType<MemoryOfBlockRegion<?>> BLOCK_REGION = new MemoryType<MemoryOfBlockRegion<?>>(
			GMFiles.rl("block_region"), (t, u) -> {
				return new MemoryOfBlockRegion<>(t, u);
			}) {
		public <M extends LivingEntity> ITextComponent display(Memory<M> obj) {

			MemoryOfBlockRegion<M> mem = ((MemoryOfBlockRegion<M>) obj);

			return Translate.make("memory.block_region", mem.getDim().getRegistryName(), mem.getBlocks());
		}
	};

	public static final MemoryType<IdeaMemory<?>> IDEA = new MemoryType<IdeaMemory<?>>(GMFiles.rl("idea"), (t, u) -> {
		return new IdeaMemory<>(t, u);
	});

	public static final MemoryType<MemoryOfSerializable<?, ?>> SERIALIZABLE = new MemoryType<MemoryOfSerializable<?, ?>>(
			GMFiles.rl("idea"), (t, u) -> {
				GMDeserialize<?> des = GMDeserialize.get(new ResourceLocation(u.get("deserializer").asString("")));
				return new MemoryOfSerializable(t, des,
						des.deserialize((ServerWorld) t.getEntityWorld(), u.get("value").get().get()));
			});

	public static final MemoryType<CauseEffectMemory<?>> CAUSE_EFFECT = new MemoryType<CauseEffectMemory<?>>(
			GMFiles.rl("cause_effect"), CauseEffectMemory::new) {
		@Override
		public <M extends LivingEntity> ITextComponent display(Memory<M> obj) {
			CauseEffectMemory<M> theo = (CauseEffectMemory<M>) obj;

			return Translate.make("memory.cause_effect", theo.getCause(), theo.getEffect(),
					theo.getCertainty().getDisplay());
		}
	};

	public final ResourceLocation regName;

	public final BiFunction<LivingEntity, Dynamic<?>, T> deserializer;

	public final Function<T, Object[]> displayer;

	public MemoryType(ResourceLocation regName, BiFunction<LivingEntity, Dynamic<?>, T> deserializer) {
		this(regName, deserializer, (e) -> new Object[] {});
	}

	public MemoryType(ResourceLocation regName, BiFunction<LivingEntity, Dynamic<?>, T> deserializer,
			Function<T, Object[]> displayer) {
		this.regName = regName;
		this.deserializer = deserializer;
		this.displayer = displayer;
		TYPES.put(regName, this);
	}

	public static MemoryType<?> get(ResourceLocation rl) {
		return TYPES.get(rl);
	}

	public static Collection<MemoryType<?>> getMemoryTypes() {
		return TYPES.values();
	}

	public <M extends LivingEntity> ITextComponent display(Memory<M> obj) {
		return Translate.make("memory." + this.regName.getNamespace() + "." + this.regName.getPath(),
				displayer.apply((T) obj));
	}
}