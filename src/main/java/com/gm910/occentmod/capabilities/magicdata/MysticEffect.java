package com.gm910.occentmod.capabilities.magicdata;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.empires.EmpireData;
import com.gm910.occentmod.empires.gods.Deity;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class MysticEffect implements IDynamicSerializable {

	private MysticEffectType effectId;

	private UUID afflicted;

	private UUID owner;

	private ITextComponent displayText;

	private CompoundNBT data;

	public MysticEffect(MysticEffectType effectId, UUID afflicted, UUID owner, @Nullable CompoundNBT data) {
		this.effectId = effectId;
		this.afflicted = afflicted;
		this.owner = owner;
		this.data = data == null ? new CompoundNBT() : data;
	}

	public MysticEffect(Dynamic<?> dyn) {

		this.effectId = MysticEffectType.get(new ResourceLocation(dyn.get("effectid").asString("")));
		this.afflicted = UUID.fromString(dyn.get("afflicted").asString(""));
		if (dyn.get("owner").get().isPresent()) {
			this.owner = UUID.fromString(dyn.get("owner").asString(""));
		}
		this.displayText = ITextComponent.Serializer.fromJson(dyn.get("displaytext").asString(""));
		data = new CompoundNBT();
		try {
			this.data = JsonToNBT.getTagFromJson(dyn.get("data").asString(""));
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
	}

	public MysticEffectType getEffectId() {
		return effectId;
	}

	public UUID getAfflicted() {
		return afflicted;
	}

	public CompoundNBT getData() {
		return data;
	}

	public ITextComponent getDisplayText() {
		return displayText;
	}

	public ITextComponent makeDisplayText(World world) {

		displayText = this.effectId.getDisplayText(world, this);
		return getDisplayText();
	}

	public UUID getOwner() {
		return owner;
	}

	public Deity getOwnerAsDeity(MinecraftServer server) {
		return EmpireData.get(server).getAllDeities().stream().filter((e) -> e.getUuid().equals(this.owner)).findAny()
				.orElse(null);
	}

	public LivingEntity getOwnerAsLiving(MinecraftServer server) {
		return (LivingEntity) ServerPos.getEntityFromUUID(this.owner, server);
	}

	public Deity getAfflictedAsDeity(MinecraftServer server) {
		return EmpireData.get(server).getAllDeities().stream().filter((e) -> e.getUuid().equals(this.afflicted))
				.findAny().orElse(null);
	}

	public LivingEntity getAfflictedAsLiving(MinecraftServer server) {
		return (LivingEntity) ServerPos.getEntityFromUUID(this.afflicted, server);
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Map<T, T> map = new HashMap<>();
		map.put(ops.createString("effectid"), ops.createString(this.effectId.toString()));
		map.put(ops.createString("afflicted"), ops.createString(this.afflicted.toString()));
		if (this.owner != null)
			map.put(ops.createString("owner"), ops.createString(this.owner.toString()));
		map.put(ops.createString("displaytext"), ops.createString(ITextComponent.Serializer.toJson(this.displayText)));
		map.put(ops.createString("data"), ops.createString(this.data.getString()));
		return ops.createMap(map);
	}

}
