package com.gm910.occentmod.entities.citizen.mind_and_traits.memory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.gm910.occentmod.api.util.Translate;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.util.GMFiles;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CitizenMemoryType<T extends CitizenMemory> {

	private static final Map<ResourceLocation, CitizenMemoryType<?>> TYPES = new HashMap<>();

	public static final CitizenMemoryType<MemoryOfDeed> DEED = new CitizenMemoryType<MemoryOfDeed>(GMFiles.rl("deed"),
			MemoryOfDeed::new) {
		@Override
		public ITextComponent display(CitizenMemory obj) {
			MemoryOfDeed gos = (MemoryOfDeed) obj;
			return gos.getEvent().getDisplay(obj.owner);
		}
	};

	public static final CitizenMemoryType<MemoryOfOccurrence> EVENT = new CitizenMemoryType<MemoryOfOccurrence>(
			GMFiles.rl("event"), MemoryOfOccurrence::new) {
		@Override
		public ITextComponent display(CitizenMemory obj) {
			MemoryOfOccurrence gos = (MemoryOfOccurrence) obj;
			return gos.getEvent().getDisplay(obj.owner);
		}
	};

	public static final CitizenMemoryType<MemoryOfBlockstate> BLOCKSTATE = new CitizenMemoryType<MemoryOfBlockstate>(
			GMFiles.rl("event"), (t, u) -> {
				try {
					return new MemoryOfBlockstate(t, u);
				} catch (CommandSyntaxException e) {
					return null;
				}
			}) {
		public ITextComponent display(CitizenMemory obj) {

			MemoryOfBlockstate mem = ((MemoryOfBlockstate) obj);

			return Translate.make("memory.blockstate", mem.getStoredState().getBlock().getNameTextComponent(),
					mem.getStoredPos().getX(), mem.getStoredPos().getY(), mem.getStoredPos().getZ(),
					mem.getStoredPos().getDimension().getRegistryName());
		}
	};

	public static final CitizenMemoryType<MemoryOfBlockRegion> BLOCK_REGION = new CitizenMemoryType<MemoryOfBlockRegion>(
			GMFiles.rl("block_region"), (t, u) -> {
				return new MemoryOfBlockRegion(t, u);
			}) {
		public ITextComponent display(CitizenMemory obj) {

			MemoryOfBlockRegion mem = ((MemoryOfBlockRegion) obj);

			return Translate.make("memory.block_region", mem.getDim().getRegistryName(), mem.getBlocks());
		}
	};

	public static final CitizenMemoryType<CauseEffectTheory> CAUSE_EFFECT = new CitizenMemoryType<CauseEffectTheory>(
			GMFiles.rl("cause_effect"), CauseEffectTheory::new) {
		@Override
		public ITextComponent display(CitizenMemory obj) {
			CauseEffectTheory theo = (CauseEffectTheory) obj;

			return Translate.make("memory.cause_effect", theo.getCause(), theo.getEffect(),
					theo.getCertainty().getDisplay());
		}
	};

	public final ResourceLocation regName;

	public final BiFunction<CitizenEntity, Dynamic<?>, T> deserializer;

	public final Function<T, Object[]> displayer;

	public CitizenMemoryType(ResourceLocation regName, BiFunction<CitizenEntity, Dynamic<?>, T> deserializer) {
		this(regName, deserializer, (e) -> new Object[] {});
	}

	public CitizenMemoryType(ResourceLocation regName, BiFunction<CitizenEntity, Dynamic<?>, T> deserializer,
			Function<T, Object[]> displayer) {
		this.regName = regName;
		this.deserializer = deserializer;
		this.displayer = displayer;
		TYPES.put(regName, this);
	}

	public static CitizenMemoryType<?> get(ResourceLocation rl) {
		return TYPES.get(rl);
	}

	public static Collection<CitizenMemoryType<?>> getMemoryTypes() {
		return TYPES.values();
	}

	public ITextComponent display(CitizenMemory obj) {
		return Translate.make("memory." + this.regName.getNamespace() + "." + this.regName.getPath(),
				displayer.apply((T) obj));
	}
}