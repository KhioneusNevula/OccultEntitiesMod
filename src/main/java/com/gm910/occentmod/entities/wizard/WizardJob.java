package com.gm910.occentmod.entities.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.TriPredicate;

public enum WizardJob {
	JOBLESS("jobless", WizardPOIS.JOBLESS_POI.get(), false), SUMMONER("summoner", WizardPOIS.SUMMONER_POI.get(), true),
	CUPID("cupid", WizardPOIS.CUPID_POI.get(), true, ImmutableSet.of(),
			ImmutableSet.of(Ingredient.fromItems(Items.HONEY_BOTTLE), Ingredient.fromItems(Items.BONE_MEAL),
					Ingredient.fromItems(Items.EGG)));

	public final String name;
	public final PointOfInterestType poi;
	public final boolean canBattle;
	private ImmutableSet<Ingredient> items;
	private ImmutableSet<TriPredicate<ServerWorld, WizardEntity, BlockPos>> secondaries;

	private WizardJob(String name, PointOfInterestType poi, boolean canBattle,
			ImmutableSet<TriPredicate<ServerWorld, WizardEntity, BlockPos>> secondaries,
			ImmutableSet<Ingredient> items) {
		this.name = name;
		this.poi = poi;
		this.canBattle = canBattle;
		this.items = items;
		this.secondaries = secondaries;
	}

	private WizardJob setItems(ImmutableSet<Ingredient> items) {
		this.items = items;
		return this;
	}

	private WizardJob setSecondaries(ImmutableSet<TriPredicate<ServerWorld, WizardEntity, BlockPos>> items) {
		this.secondaries = items;
		return this;
	}

	public static ImmutableSet<TriPredicate<ServerWorld, WizardEntity, BlockPos>> getSecondariesAsBlockStates(
			ImmutableSet<BlockState> items) {
		Stream<TriPredicate<ServerWorld, WizardEntity, BlockPos>> stream = items.stream().map((b) -> {
			return (world, entity, pos) -> {
				return world.getBlockState(pos).equals(b);
			};
		});
		return ImmutableSet.copyOf(stream.collect(Collectors.toList()));
	}

	public static ImmutableSet<TriPredicate<ServerWorld, WizardEntity, BlockPos>> getSecondariesAsBlocks(
			ImmutableSet<Block> items) {

		List<BlockState> states = new ArrayList<>();
		items.forEach((bl) -> {
			states.addAll(ImmutableSet.copyOf(bl.getStateContainer().getValidStates()));
		});
		return getSecondariesAsBlockStates(ImmutableSet.copyOf(states));
	}

	public ImmutableSet<Ingredient> getItems() {
		return items;
	}

	public ImmutableSet<TriPredicate<ServerWorld, WizardEntity, BlockPos>> getSecondaries() {
		return secondaries;
	}

	public boolean testSecondaryPosition(ServerWorld world, WizardEntity en, BlockPos pos) {
		for (TriPredicate<ServerWorld, WizardEntity, BlockPos> tripred : secondaries) {
			if (tripred.test(world, en, pos)) {
				return true;
			}
		}
		return false;
	}

	private WizardJob(String name, PointOfInterestType poi, boolean canBattle) {
		this(name, poi, canBattle, ImmutableSet.of(), ImmutableSet.of());
	}

}
