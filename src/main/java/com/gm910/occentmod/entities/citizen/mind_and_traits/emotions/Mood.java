package com.gm910.occentmod.entities.citizen.mind_and_traits.emotions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.language.Translate;
import com.gm910.occentmod.empires.gods.Deity;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.util.GMFiles;
import com.google.common.collect.Sets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class Mood<E extends LivingEntity> {

	private static final Map<ResourceLocation, Mood<? extends LivingEntity>> TYPES = new HashMap<>();

	public static final Mood<Deity> APOCALYPTIC = new Mood<>(GMFiles.rl("deity_apocalyptic"), Deity.class);
	public static final Mood<Deity> PROSPEROUS = new Mood<>(GMFiles.rl("deity_prosperous"), Deity.class);
	public static final Mood<Deity> PROTECTIVE = new Mood<>(GMFiles.rl("deity_protective"), Deity.class);
	public static final Mood<LivingEntity> CREATIVE = new Mood<>(GMFiles.rl("creative"), LivingEntity.class);

	private ResourceLocation rl;

	private Set<Class<? extends E>> entityClass;

	@SafeVarargs
	public Mood(ResourceLocation rl, Class<? extends E>... entityClass) {
		this.rl = rl;
		this.entityClass = Sets.newHashSet(entityClass);
		TYPES.put(rl, this);
	}

	public boolean isForDeities() {
		return this.entityClass.stream().anyMatch((e) -> e.isAssignableFrom(Deity.class));
	}

	public boolean isForCitizens() {
		return this.entityClass.stream().anyMatch((e) -> e.isAssignableFrom(CitizenEntity.class));
	}

	public boolean isForClass(Class<?> clazz) {

		return this.entityClass.stream().anyMatch((e) -> e.isAssignableFrom(clazz));
	}

	public ResourceLocation getRL() {
		return rl;
	}

	public ITextComponent getDisplayText() {
		return Translate.make("mood." + this.rl.getNamespace() + "." + this.rl.getPath());
	}

	public static <E extends LivingEntity> Mood<E> get(ResourceLocation rl) {
		return (Mood<E>) TYPES.get(rl);
	}

	public static <M extends LivingEntity> Collection<Mood<?>> getTypes(Class<M> subclass) {
		return TYPES.values().stream().filter((e) -> e.isForClass(subclass)).collect(Collectors.toSet());
	}

	public Set<Class<? extends E>> getEntityClasses() {
		return entityClass;
	}

	public static Collection<Mood<?>> getTypes() {
		return TYPES.values();
	}

}
