package com.gm910.occentmod.items;

import com.gm910.occentmod.world.DimensionData;
import com.gm910.occentmod.world.Warper;
import com.gm910.occentmod.world.mindrealm.MindDimensionData;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class MindMirrorItem extends ModItem {

	public MindMirrorItem() {
		super((new Item.Properties()).rarity(Rarity.RARE).group(ItemGroup.BREWING));

	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (worldIn.isRemote)
			return super.onItemRightClick(worldIn, playerIn, handIn);
		MindDimensionData dat = MindDimensionData.createMindWorld(playerIn);
		if (playerIn.dimension != DimensionData.get(worldIn.getServer()).getDimensionInfo(playerIn)
				.getDimensionType()) {
			Warper.teleportEntity(playerIn, dat.getWorld().dimension.getType(), new Vec3d(0, 200, 0));
		} else {
			Warper.teleportEntity(playerIn, DimensionType.OVERWORLD, new Vec3d(0, 200, 0));

		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

}
