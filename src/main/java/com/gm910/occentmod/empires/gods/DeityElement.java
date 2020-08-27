package com.gm910.occentmod.empires.gods;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.language.Translate;
import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Gene;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Genetics;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.genetype.GeneType;
import com.gm910.occentmod.util.GMFiles;

import net.minecraft.entity.LivingEntity;
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
	public static final DeityElement PLANES = new DeityElement(GMFiles.rl("planes"));
	public static final DeityElement CREATURES = new DeityElement(GMFiles.rl("creatures"));
	public static final DeityElement WEAPONRY = new DeityElement(GMFiles.rl("weaponry"));
	public static final DeityElement SUN = new DeityElement(GMFiles.rl("sun"));
	public static final DeityElement ELIXIR = new DeityElement(GMFiles.rl("elixir"));
	public static final DeityElement LOVE = new DeityElement(GMFiles.rl("love"));
	public static final DeityElement ALCHEMY = new DeityElement(GMFiles.rl("alchemy"));
	public static final DeityElement RITUALISM = new DeityElement(GMFiles.rl("ritualism"));

	private ResourceLocation resource;

	private Map<GeneType<?, ? extends LivingEntity>, Function<Genetics<? extends LivingEntity>, Gene<?>>> producers = new HashMap<>();

	public DeityElement(ResourceLocation rl) {
		this.resource = rl;
		TYPES.put(rl, this);
	}

	public DeityElement addGene(GeneType<?, ?> type, Function<Genetics<?>, Gene<?>> producer) {

		producers.put(type, producer);
		return this;
	}

	public <T, E extends LivingEntity> Gene<T> makeGene(GeneType<T, E> type, Genetics<E> entity) {
		return (Gene<T>) producers.getOrDefault(type, (e) -> null).apply(entity);
	}

	public <E extends LivingEntity> void initGenesFor(Genetics<E> en) {
		for (GeneType<?, ? super E> tipa : this.producers.keySet().stream()
				.filter((e) -> ModReflect.<GeneType<?, ? super E>>instanceOf(e, GeneType.class)
						&& e.getValueType().isAssignableFrom(en.getOwnerClass()))
				.map((m) -> (GeneType<?, ? super E>) m).collect(Collectors.toSet())) {
			en.setGene(tipa, producers.get(tipa).apply(en));
		}
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