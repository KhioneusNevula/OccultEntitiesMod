package com.gm910.occentmod.entities.citizen.mind_and_traits.emotions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.gm910.occentmod.api.util.Translate;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class Mood {

	private static final Map<ResourceLocation, Mood> TYPES = new HashMap<>();

	private ResourceLocation rl;

	public Mood(ResourceLocation rl) {
		this.rl = rl;
		TYPES.put(rl, this);
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

	public static Collection<Mood> getTypes() {
		return TYPES.values();
	}

}
