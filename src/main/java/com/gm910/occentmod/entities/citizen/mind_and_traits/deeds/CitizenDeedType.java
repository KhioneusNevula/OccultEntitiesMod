package com.gm910.occentmod.entities.citizen.mind_and_traits.deeds;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.gm910.occentmod.util.GMFiles;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.ResourceLocation;

public class CitizenDeedType<T extends CitizenDeed> {

	private ResourceLocation name;
	private static final Map<ResourceLocation, CitizenDeedType<?>> DEEDS = new HashMap<>();

	public static final CitizenDeedType<MurderDeed> MURDER = new CitizenDeedType<>(GMFiles.rl("murder"),
			MurderDeed::new);

	private Function<CitizenIdentity, CitizenDeed> supplier;

	/**
	 * 
	 * @param name
	 * @param func returns a blank citizen deed to deserialize
	 */
	public CitizenDeedType(ResourceLocation name, Function<CitizenIdentity, CitizenDeed> func) {
		this.name = name;
		this.supplier = func;
		DEEDS.put(name, this);
	}

	public ResourceLocation getName() {
		return name;
	}

	public static CitizenDeed deserialize(Dynamic<?> dyn) {
		CitizenDeedType<?> type = get(new ResourceLocation(dyn.get("rl").asString("")));
		return type.deserializeDat(dyn);
	}

	public CitizenDeed deserializeDat(Dynamic<?> dyn) {
		CitizenDeed deed = supplier.apply(new CitizenIdentity(dyn.get("id").get().get()));
		deed.$readData(dyn);
		return deed;
	}

	public static CitizenDeedType<?> get(ResourceLocation name) {
		return DEEDS.get(name);
	}

	public static Collection<CitizenDeedType<?>> getTypes() {
		return DEEDS.values();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " named " + this.name;
	}

}
