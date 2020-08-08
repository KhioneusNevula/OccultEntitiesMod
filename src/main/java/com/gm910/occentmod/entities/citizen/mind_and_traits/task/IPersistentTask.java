package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.CitizenTask.Context;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;

public interface IPersistentTask extends IDynamicSerializable {

	public PersistentTaskType<?> getType();

	@Override
	default <T> T serialize(DynamicOps<T> ops) {
		CitizenTask task = (CitizenTask) this;
		T type = ops.createString(getType().rl.toString());
		T data = writeData(ops);
		T cons = ops.createList(task.getContexts().stream().map((m) -> ops.createString(m.toString())));
		T stat = ops.createBoolean(task.getStatus() == Task.Status.RUNNING);
		T time = ops.createLong(ModReflect.getField(Task.class, long.class, "status", "field_220385_b", task));
		return ops.createMap(ImmutableMap.of(ops.createString("type"), type, ops.createString("data"), data,
				ops.createString("contexts"), cons, ops.createString("running"), stat, ops.createString("time"), time));
	}

	@SuppressWarnings("unchecked")
	public static <T extends CitizenTask & IPersistentTask> T deserialize(Dynamic<?> d) {
		PersistentTaskType<?> type = PersistentTaskType.get(new ResourceLocation(d.get("type").asString("")));
		T get = (T) type.deserialize(d.get("data").get().get());
		get.setContext(d.get("contexts").asStream().map((e) -> Context.valueOf(e.asString("")))
				.collect(Collectors.toList()).toArray(new Context[0]));
		ModReflect.setField(Task.class, Task.Status.class, "status", "field_220384_a", get,
				d.get("running").asBoolean(false) ? Task.Status.RUNNING : Task.Status.STOPPED);
		ModReflect.setField(Task.class, long.class, "stopTime", "field_220385_b", get, d.get("time").asLong(0));
		return get;
	}

	public <T> T writeData(DynamicOps<T> ops);

}
