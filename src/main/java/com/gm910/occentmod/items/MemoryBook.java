package com.gm910.occentmod.items;

import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.sapience.mind_and_traits.memory.Memories;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.Memory;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class MemoryBook extends WrittenBookItem {

	public MemoryBook() {
		super(new Item.Properties());
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
		if (!target.world.isRemote && SapientInfo.isSapient(target)) {
			SapientInfo<?> info = SapientInfo.get(target);
			if (info.getKnowledge() != null) {
				CompoundNBT nbt = stack.getOrCreateTag();
				ListNBT pages = new ListNBT();
				nbt.put("pages", pages);
				Memories<?> mem = info.getKnowledge();
				for (Memory<?> memoria : mem.getKnowledge()) {
					if (memoria.getDisplayText() == null)
						continue;
					pages.add(StringNBT.valueOf(memoria.getDisplayText().getFormattedText()));
				}
				System.out.println(pages);
			}
		}

		return super.itemInteractionForEntity(stack, playerIn, target, hand);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		stack.getOrCreateTag().put("pages", new ListNBT());
		return super.initCapabilities(stack, nbt);
	}

}
