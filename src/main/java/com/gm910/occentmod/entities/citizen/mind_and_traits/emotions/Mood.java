package com.gm910.occentmod.entities.citizen.mind_and_traits.emotions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.Translate;
import com.gm910.occentmod.util.GMFiles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class Mood {

	private static final Map<ResourceLocation, Mood> TYPES = new HashMap<>();

	public static final Mood APOCALYPTIC = new Mood(GMFiles.rl("deity_apocalyptic"), true);
	public static final Mood PROSPEROUS = new Mood(GMFiles.rl("deity_prosperous"), true);
	public static final Mood PROTECTIVE = new Mood(GMFiles.rl("deity_protective"), true);

	private ResourceLocation rl;

	private boolean forDeities;

	public Mood(ResourceLocation rl, boolean forDeities) {
		this.rl = rl;
		TYPES.put(rl, this);
		this.forDeities = forDeities;
	}

	public boolean isForDeities() {
		return forDeities;
	}

	public ResourceLocation getRL() {
		return rl;
	}

	public ITextComponent getDisplayText() {
		return Translate.make("mood." + this.rl.getNamespace() + "." + this.rl.getPath());
	}

	public static Mood get(ResourceLocation rl) {
		return TYPES.get(rl);
	}

	public static Collection<Mood> getTypes(boolean forDeities) {
		return TYPES.values().stream().filter((e) -> e.forDeities == forDeities).collect(Collectors.toSet());
	}

	public static Collection<Mood> getTypes() {
		return TYPES.values();
	}

}
