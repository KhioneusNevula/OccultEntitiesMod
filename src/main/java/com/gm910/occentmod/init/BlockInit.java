package com.gm910.occentmod.init;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.blocks.MindJellyBlock;
import com.gm910.occentmod.blocks.ModBlock.BlockRegistryObject;
import com.gm910.occentmod.blocks.ThroneBlock;
import com.gm910.occentmod.blocks.vaettrblocks.BrennisteinvaettrBlock;
import com.gm910.occentmod.blocks.vaettrblocks.EndisteinvaettrBlock;
import com.gm910.occentmod.blocks.vaettrblocks.LandvaettrBlock;
import com.gm910.occentmod.blocks.vaettrblocks.StormvaettrBlock;
import com.gm910.occentmod.blocks.worldcontroller.WorldControllerBlock;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class BlockInit {
	private BlockInit() {
	}

	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS,
			OccultEntities.MODID);

	public static final RegistryObject<Block> LANDVAETTR = (new BlockRegistryObject("landvaettr_block",
			() -> new LandvaettrBlock())).makeItem(() -> new Item.Properties().group(ItemGroup.MISC))
					.createRegistryObject();

	public static final RegistryObject<Block> STORMVAETTR = (new BlockRegistryObject("stormvaettr_block",
			() -> new StormvaettrBlock())).makeItem(() -> new Item.Properties().group(ItemGroup.MISC))
					.createRegistryObject();

	public static final RegistryObject<Block> BRENNISTEINVAETTR = (new BlockRegistryObject("brennisteinvaettr_block",
			() -> new BrennisteinvaettrBlock())).makeItem(() -> new Item.Properties().group(ItemGroup.MISC))
					.createRegistryObject();

	public static final RegistryObject<Block> ENDISTEINVAETTR = (new BlockRegistryObject("endisteinvaettr_block",
			() -> new EndisteinvaettrBlock())).makeItem(() -> new Item.Properties().group(ItemGroup.MISC))
					.createRegistryObject();

	public static final RegistryObject<Block> THRONE = (new BlockRegistryObject("throne", () -> new ThroneBlock()))
			.makeItem(() -> new Item.Properties().group(ItemGroup.DECORATIONS)).createRegistryObject();

	public static final RegistryObject<Block> MIND_JELLY = (new BlockRegistryObject("mind_jelly",
			() -> new MindJellyBlock())).makeItem(() -> new Item.Properties().group(ItemGroup.DECORATIONS))
					.createRegistryObject();

	public static final RegistryObject<Block> WORLD_CONTROLLER = (new BlockRegistryObject("world_controller",
			() -> new WorldControllerBlock())).createRegistryObject();

}
