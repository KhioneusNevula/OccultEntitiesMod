package com.gm910.occentmod.sapience.mind_and_traits.task;

import java.util.UUID;

import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.api.util.ServerPos;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.util.math.IPosWrapper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class SapientWalkTarget extends WalkTarget implements IDynamicSerializable {

	private Necessity necessity;

	public SapientWalkTarget(BlockPos targetIn, float speedIn, int distanceIn, Necessity necessity) {
		super(targetIn, speedIn, distanceIn);
		this.necessity = necessity;
	}

	public SapientWalkTarget(Vec3d targetIn, float speedIn, int distanceIn, Necessity necessity) {
		super(targetIn, speedIn, distanceIn);
		this.necessity = necessity;
	}

	public SapientWalkTarget(IPosWrapper targetIn, float speedIn, int distanceIn, Necessity necessity) {
		super(targetIn, speedIn, distanceIn);
		this.necessity = necessity;
	}

	public SapientWalkTarget(WalkTarget from, Necessity necessity) {
		this(from.getTarget(), from.getSpeed(), from.getDistance(), necessity);
	}

	public SapientWalkTarget(ServerWorld worldIn, Dynamic<?> dyn) {
		this(dyn.get("is_entity").asBoolean(false)
				? new EntityPosWrapper(ServerPos.getEntityFromUUID(UUID.fromString(dyn.get("location").asString("")),
						worldIn.getServer()))
				: new BlockPosWrapper(new BlockPos(ServerPos.deserializeVec3d(dyn.get("location").get().get()))),
				dyn.get("speed").asFloat(0), dyn.get("distance").asInt(0),
				Necessity.values()[dyn.get("necessity").asInt(0)]);
	}

	public Necessity getNecessity() {
		return necessity;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Entity targ = this.getTarget() instanceof EntityPosWrapper
				? ModReflect.getField(EntityPosWrapper.class, Entity.class, "entity", "field_220611_a",
						this.getTarget())
				: null;
		T p = targ != null ? ops.createString(targ.getCachedUniqueIdString())
				: ServerPos.serializeVec3d(this.getTarget().getPos(), ops);
		T b = ops.createBoolean(targ != null);
		T d = ops.createInt(getDistance());
		T s = ops.createFloat(getSpeed());
		return ops.createMap(ImmutableMap.of(ops.createString("location"), p, ops.createString("is_entity"), b,
				ops.createString("distance"), d, ops.createString("speed"), s, ops.createString("necessity"),
				ops.createInt(necessity.ordinal())));
	}

}
