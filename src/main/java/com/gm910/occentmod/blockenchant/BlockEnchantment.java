package com.gm910.occentmod.blockenchant;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.gm910.occentmod.api.util.IWorldTickable;
import com.gm910.occentmod.api.util.NonNullMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.IDynamicSerializable;
import net.minecraftforge.event.TickEvent.WorldTickEvent;

public abstract class BlockEnchantment implements IDynamicSerializable, IWorldTickable {

	public static Map<String, BlockEnchantmentType<?>> DESERIALIZERS = new NonNullMap<>(() -> null);

	public static class BlockEnchantmentType<T extends BlockEnchantment> {
		public final Function<CompoundNBT, BlockEnchantment> deserializer;
		public final Supplier<T> supplier;
		public final String id;

		public BlockEnchantmentType(String id, Function<CompoundNBT, BlockEnchantment> deserializer,
				Supplier<T> supplier) {
			this.deserializer = deserializer;
			this.supplier = supplier;
			this.id = id;
		}
	}

	private BlockEnchantmentType<?> type;

	public BlockEnchantment(BlockEnchantmentType<?> type) {
		this.type = type;
	}

	public String getID() {
		return type.id;
	}

	public BlockEnchantmentType<?> getType() {
		return type;
	}

	@Override
	public <T> T serialize(DynamicOps<T> o) {
		Map<T, T> map = new HashMap<>();
		T str = o.createString(getID());
		T data = o.createString(serialize().getString());
		map.put(str, data);
		return o.createMap(map);
	}

	public abstract CompoundNBT serialize();

	public static BlockEnchantment deserialize(Dynamic<?> dynamic) {

		Map<String, String> dynamap = dynamic.asMap((d) -> d.asString(""), (d) -> d.asString(""));

		try {
			return DESERIALIZERS.get(dynamap.keySet().stream().findAny().orElseGet(() -> "")).deserializer
					.apply(JsonToNBT.getTagFromJson(dynamap.values().stream().findAny().orElseGet(() -> "")));
		} catch (CommandSyntaxException | NullPointerException e) {
			return null;
		}
	}

	@Override
	public void tick(WorldTickEvent event, long gameTime, long dayTime) {

	}

}
