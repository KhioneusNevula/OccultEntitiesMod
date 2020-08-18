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
			return Translate.make("deed." + gos.getDeed().getType().getName().getNamespace() + "."
					+ gos.getDeed().getType().getName().getPath(), gos.getDeed().getDataForDisplay(obj.owner));
		}
	};

	public static final CitizenMemoryType<MemoryOfOccurrence> EVENT = new CitizenMemoryType<MemoryOfOccurrence>(
			GMFiles.rl("event"), MemoryOfOccurrence::new) {
		@Override
		public ITextComponent display(CitizenMemory obj) {
			MemoryOfOccurrence gos = (MemoryOfOccurrence) obj;
			return Translate.make(
					"citizen.event." + gos.getEvent().getType().getName().getNamespace() + "."
							+ gos.getEvent().getType().getName().getPath(),
					gos.getEvent().getDataForDisplay(obj.owner));
		}
	};

	public static final CitizenMemoryType<MemoryOfBlockstate> BLOCKSTATE = new CitizenMemoryType<>(GMFiles.rl("event"),
			(t, u) -> {
				try {
					return new MemoryOfBlockstate(t, u);
				} catch (CommandSyntaxException e) {
					return null;
				}
			});

	public final ResourceLocation regName;

	public final BiFunction<CitizenEntity, Dynamic<?>, T> deserializer;

	public final Function<T, Object[]> displayer;

	public CitizenMemoryType(ResourceLocation regName, BiFunction<CitizenEntity, Dynamic<?>, T> deserializer) {
		this(regName, deserializer, (e) -> new Object[] { e.getDisplayText() });
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