package com.gm910.occentmod.entities.citizen.mind_and_traits.gossip;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class CitizenGossip implements IDynamicSerializable {

	private GossipType<?> type;

	protected CitizenEntity owner;

	public CitizenGossip(CitizenEntity owner, GossipType<?> type) {
		this.owner = owner;
		this.type = type;
	}

	public static CitizenGossip deserialize(CitizenEntity owner, Dynamic<?> dynamic) {
		ResourceLocation des = new ResourceLocation(dynamic.get("resource").asString(""));
		CitizenGossip kno = GossipType.get(des).deserializer.apply(owner, dynamic.get("data").get().get());
		return kno;
	}

	@Override
	public <T> T serialize(DynamicOps<T> op) {

		T str = op.createString(type.regName.toString());
		T dat = writeData(op);

		return op.createMap(ImmutableMap.of(op.createString("resource"), str, op.createString("data"), dat));
	}

	public abstract <T> T writeData(DynamicOps<T> ops);

	public ITextComponent getDisplayText() {
		return type.display(this);
	}

}
