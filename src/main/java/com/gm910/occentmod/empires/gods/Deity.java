package com.gm910.occentmod.empires.gods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.gm910.occentmod.api.language.NamePhonemicHelper.PhonemeWord;
import com.gm910.occentmod.capabilities.GMCapabilityUser;
import com.gm910.occentmod.empires.Empire;
import com.gm910.occentmod.empires.gods.citinfo.DeityInformation;
import com.gm910.occentmod.empires.gods.citinfo.DivineInventory;
import com.gm910.occentmod.init.EntityInit;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.HandSide;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class Deity extends LivingEntity implements IDynamicSerializable {

	private Set<DeityElement> elements = new HashSet<>();
	private Empire empire;
	private Set<DeityPower> powers = new HashSet<>();// TODO
	private PhonemeWord name;
	private DeityInformation info;
	public final DivineInventory inventory = new DivineInventory(50);

	public Deity(Empire e, PhonemeWord name, Set<DeityElement> elements) {
		this(EntityInit.DEITY_DUMMY.get(), e.getCenterWorld());
		empire = e;
		this.elements.addAll(elements);
		this.name = name;
		this.info = new DeityInformation();
	}

	public Deity(EntityType<Deity> t, World world) {
		super(t, world);
		this.dead = true;
	}

	public Deity(Empire e, Dynamic<?> d) {
		this(e, new PhonemeWord(d.get("name").get().get()), Sets.newHashSet());
		empire = e;
		this.elements.addAll(d.get("elements").asList((m) -> DeityElement.get(new ResourceLocation(m.asString("")))));
		if (d.get("caps").get().isPresent()) {
			try {
				this.deserializeCaps(JsonToNBT.getTagFromJson(d.get("caps").asString("")));
			} catch (CommandSyntaxException e1) {
				e1.printStackTrace();
			}
		}
		this.inventory.deserialize(d.get("inventory").get().get());
		this.info.deserialize(d.get("info").get().get());
	}

	public void addPower(DeityPower power) {
		this.powers.add(power);
		// TODO
	}

	public UUID getUuid() {
		return getUniqueID();
	}

	public Empire getEmpire() {
		return empire;
	}

	public Set<DeityElement> getElements() {
		return new HashSet<>(elements);
	}

	public PhonemeWord getPhonemicName() {
		return name;
	}

	public String getNameString() {
		return this.name.toString();
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Map<T, T> map = new HashMap<>();
		map.put(ops.createString("elements"),
				ops.createList(elements.stream().map((e) -> ops.createString(e.getResource().toString()))));
		map.put(ops.createString("name"), this.name.serialize(ops));
		CompoundNBT capa = this.serializeCaps();
		if (capa != null) {
			map.put(ops.createString("caps"), ops.createString(capa.getString()));
		}
		map.put(ops.createString("inventory"), this.inventory.serialize(ops));
		map.put(ops.createString("info"), this.info.serialize(ops));
		return ops.createMap(map);
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return Lists.newArrayList();
	}

	@Override
	public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {

	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction nu) {
		if (cap.equals(GMCapabilityUser.CITIZEN_INFO)) {
			// TODO
			return LazyOptional.of(() -> info).cast();
		}
		return super.getCapability(cap);
	}

	@Override
	public HandSide getPrimaryHand() {
		return HandSide.RIGHT;
	}
}
