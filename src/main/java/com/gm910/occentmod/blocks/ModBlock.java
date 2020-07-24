package com.gm910.occentmod.blocks;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.gm910.occentmod.api.util.NonNullMap;
import com.gm910.occentmod.init.BlockInit;
import com.gm910.occentmod.init.ItemInit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.RegistryObject;

public class ModBlock extends Block {
	
	public static class BlockRegistryObject {

		String name;
		Supplier<Block> bsup;
		boolean makeItem;
		Supplier<Item.Properties> iprops;
		
		/**
		 * 
		 * @param name of block in registries
		 * @param bproperties supplier for block to be made
		 */
		public BlockRegistryObject(String name, Supplier<Block> blocksup) {
			this.name = name;
			this.bsup = blocksup;
			this.makeItem = false;
		}
		
		/**
		 * if iprops is null will use default itemproperty supplier
		 * @param iprops
		 * @return
		 */
		public BlockRegistryObject makeItem(@Nullable Supplier<Item.Properties> iprops) {
			this.makeItem = true;
			this.iprops = iprops;
			return this;
		}
		
		
		/**
		 * Adds this block to BlockInit and ItemInit if makeItem is true, and returns the RegistryObject created
		 * @return
		 */
		public RegistryObject<Block> createRegistryObject() {
			RegistryObject<Block> reg = BlockInit.BLOCKS.register(name, bsup);
			
			if (makeItem) {
				ItemInit.ITEMS.register(name, () -> new BlockItem(reg.get(), iprops != null ? iprops.get() : new Item.Properties()));
			}
			
			return reg;
		}
		
		
	}
	
	private Map<Supplier<TileEntityType<?>>, BiPredicate<? super BlockState, ? super IBlockReader>> tileMap = new NonNullMap<Supplier<TileEntityType<?>>, BiPredicate<? super BlockState, ? super IBlockReader>>(() -> (a, b) -> true);

	/**
	 * If you want to instantiate an item as well, set itemName to the name of your block and iproperties to your item properties (or null if you want them to be default)
	 * @param properties
	 * @param itemName
	 * @param properties
	 */
	public ModBlock(Properties properties) {
		super(properties);
		
	}

	/**
	 * Adds a tile entity supplier to be created for the specified condition along with the block
	 * @param <T>
	 * @param sup tileentitytype supplier for the tile entity
	 * @param condition can be null, if null will evaluate to 'always true'. you MUST have a IBlockReader == null check, since the hasTileEntity method does not provide a IBlockReader
	 * @return
	 */
	public <T extends TileEntity> ModBlock addTileEntity(Supplier<TileEntityType<?>> sup, @Nullable BiPredicate<? super BlockState, ? super IBlockReader> condition) {
		
		this.tileMap.put(sup, condition == null ? (a, b) -> true : condition);
		return this;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		for (Supplier<TileEntityType<?>> sup : this.tileMap.keySet()) {
			if (tileMap.get(sup).test(state, null)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		for (Supplier<TileEntityType<?>> sup : this.tileMap.keySet()) {
			if (tileMap.get(sup).test(state, null)) {
				return sup.get().create();
			}
		}
		return null;
	}
	
}
