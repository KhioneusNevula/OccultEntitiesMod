package com.gm910.occentmod.entities.citizen.mind_and_traits.gossip;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.gm910.occentmod.api.util.Translate;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.util.GMFiles;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GossipType<T extends CitizenGossip> {

	private static final Map<ResourceLocation, GossipType<?>> TYPES = new HashMap<>();

	public static final GossipType<GossipAboutDeed> DEED = new GossipType<GossipAboutDeed>(GMFiles.rl("deed"),
			GossipAboutDeed::new) {
		@Override
		public ITextComponent display(CitizenGossip obj) {
			GossipAboutDeed gos = (GossipAboutDeed) obj;
			return Translate.make("deed." + gos.getDeed().getType().getName().getNamespace() + "."
					+ gos.getDeed().getType().getName().getPath(), gos.getDeed().getDataForDisplay(obj.owner));
		}
	};

	public final ResourceLocation regName;

	public final BiFunction<CitizenEntity, Dynamic<?>, T> deserializer;

	public final Function<T, Object[]> displayer;

	public GossipType(ResourceLocation regName, BiFunction<CitizenEntity, Dynamic<?>, T> deserializer) {
		this(regName, deserializer, (e) -> new Object[] { e.getDisplayText() });
	}

	public GossipType(ResourceLocation regName, BiFunction<CitizenEntity, Dynamic<?>, T> deserializer,
			Function<T, Object[]> displayer) {
		this.regName = regName;
		this.deserializer = deserializer;
		this.displayer = displayer;
		TYPES.put(regName, this);
	}

	public static GossipType<?> get(ResourceLocation rl) {
		return TYPES.get(rl);
	}

	public static Collection<GossipType<?>> getGossipTypes() {
		return TYPES.values();
	}

	public ITextComponent display(CitizenGossip obj) {
		return Translate.make("gossip." + this.regName.getNamespace() + "." + this.regName.getPath(),
				displayer.apply((T) obj));
	}
}