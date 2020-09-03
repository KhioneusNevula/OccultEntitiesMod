package com.gm910.occentmod.init;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.items.MemoryBook;
import com.gm910.occentmod.items.MindMirrorItem;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ItemInit {
	private ItemInit() {
	}

	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS,
			OccultEntities.MODID);

	public static final RegistryObject<Item> MIND_MIRROR = ITEMS.register("mind_mirror", () -> new MindMirrorItem());

	public static final RegistryObject<Item> MEMORY_BOOK = ITEMS.register("memory_book", () -> new MemoryBook());

	public static void registerISTERs() {

	}

}
