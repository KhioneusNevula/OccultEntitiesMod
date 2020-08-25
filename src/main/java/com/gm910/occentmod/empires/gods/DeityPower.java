package com.gm910.occentmod.empires.gods;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public abstract class DeityPower {

	private Set<Deity> deities = new HashSet<>();

	ResourceLocation resource;

	private Set<DeityElement> elements;

	public DeityPower(ResourceLocation loc, DeityElement... elements) {
		MinecraftForge.EVENT_BUS.register(this);
		this.resource = loc;
		this.elements = ImmutableSet.copyOf(elements);
		TYPES.put(loc, this);
	}

	public Set<Deity> getAllDeitiesWithPower() {
		return deities;
	}

	public Set<DeityElement> getElements() {
		return elements;
	}

	public void registerDeity(Deity d) {
		this.deities.add(d);
		d.addPower(this);
	}

	@SubscribeEvent
	public final void onEvent(Event e) {
		for (Deity deity : deities) {
			this.usePower(deity, e);
		}
	}

	public abstract void usePower(Deity owner, Event e);

	private static final Map<ResourceLocation, DeityPower> TYPES = new HashMap<>();

	public ResourceLocation getResource() {
		return resource;
	}

	public static DeityPower get(ResourceLocation rl) {
		return TYPES.get(rl);
	}

	public static Set<DeityPower> getForElement(DeityElement e) {
		Set<DeityPower> pow = Sets.newHashSet();
		for (DeityPower d : TYPES.values()) {
			if (d.elements.contains(e)) {
				pow.add(d);
			}
		}
		return pow;
	}

	public static Collection<DeityPower> getAll() {
		return TYPES.values();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + " " + this.resource;
	}

}
