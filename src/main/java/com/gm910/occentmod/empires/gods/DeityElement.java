package com.gm910.occentmod.empires.gods;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.gm910.occentmod.api.language.Translate;
import com.gm910.occentmod.util.GMFiles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class DeityElement {
	private static final Map<ResourceLocation, DeityElement> TYPES = new HashMap<>();

	public static final DeityElement DEATH = new DeityElement(GMFiles.rl("death"));
	public static final DeityElement FIRE = new DeityElement(GMFiles.rl("fire"));
	public static final DeityElement WATER = new DeityElement(GMFiles.rl("water"));
	public static final DeityElement SKY = new DeityElement(GMFiles.rl("sky"));
	public static final DeityElement NATURE = new DeityElement(GMFiles.rl("nature"));
	public static final DeityElement EARTH = new DeityElement(GMFiles.rl("earth"));
	public static final DeityElement LAVA = new DeityElement(GMFiles.rl("lava"));
	public static final DeityElement PLANES = new DeityElement(GMFiles.rl("planes"));
	public static final DeityElement CREATURES = new DeityElement(GMFiles.rl("creatures"));
	public static final DeityElement WAR = new DeityElement(GMFiles.rl("war"));
	public static final DeityElement SUN = new DeityElement(GMFiles.rl("sun"));
	public static final DeityElement ELIXIR = new DeityElement(GMFiles.rl("elixir"));
	public static final DeityElement ENCHANTMENT = new DeityElement(GMFiles.rl("enchantment"));
	public static final DeityElement LOVE = new DeityElement(GMFiles.rl("love"));
	public static final DeityElement ALCHEMY = new DeityElement(GMFiles.rl("alchemy"));

	private ResourceLocation resource;

	public DeityElement(ResourceLocation rl) {
		this.resource = rl;
		TYPES.put(rl, this);
	}

	public ResourceLocation getResource() {
		return resource;
	}

	public static DeityElement get(ResourceLocation rl) {
		return TYPES.get(rl);
	}

	public static Collection<DeityElement> getAll() {
		return TYPES.values();
	}

	public ITextComponent getDisplayName() {
		return Translate.make("deity.element." + resource.getNamespace() + "." + resource.getPath());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + " " + this.resource;
	}
}