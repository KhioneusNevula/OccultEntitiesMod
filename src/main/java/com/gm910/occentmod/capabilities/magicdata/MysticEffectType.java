package com.gm910.occentmod.capabilities.magicdata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.gm910.occentmod.util.GMFiles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class MysticEffectType {
	private static final Map<ResourceLocation, MysticEffectType> TYPES = new HashMap<>();

	public static final MysticEffectType BLESSING = new MysticEffectType(GMFiles.rl("blessing")) {
		@Override
		public ITextComponent getDisplayText(World type, MysticEffect from) {
			if (!(type instanceof ServerWorld))
				return null;
			return null;
		}
	};

	private ResourceLocation resource;

	public MysticEffectType(ResourceLocation rl) {
		this.resource = rl;
		TYPES.put(rl, this);
	}

	public ResourceLocation getResource() {
		return resource;
	}

	public static MysticEffectType get(ResourceLocation rl) {
		return TYPES.get(rl);
	}

	public static Collection<MysticEffectType> getAll() {
		return TYPES.values();
	}

	public abstract ITextComponent getDisplayText(World type, MysticEffect from);

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + this.resource;
	}
}