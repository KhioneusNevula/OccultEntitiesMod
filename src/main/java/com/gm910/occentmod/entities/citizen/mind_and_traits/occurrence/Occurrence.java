package com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.api.util.IWorldTickable;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.CitizenInformation;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.CitizenTask;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.TickEvent.WorldTickEvent;

public abstract class Occurrence implements IDynamicSerializable, IWorldTickable {

	protected OccurrenceType<?> type;

	protected Vec3d position;

	protected int dimension;

	public Occurrence(OccurrenceType<?> type, Vec3d pos, int dim) {
		this.type = type;
		this.position = pos;
		this.dimension = dim;
	}

	public Occurrence(OccurrenceType<?> type) {
		this.type = type;
	}

	public Occurrence setDimension(int dimension) {
		this.dimension = dimension;
		return this;
	}

	public int getDimension() {
		return dimension;
	}

	public Occurrence setPosition(Vec3d pos) {
		this.position = pos;
		return this;
	}

	public Vec3d getPosition() {
		return position;
	}

	public void $readData(Dynamic<?> dyn) {

		this.type = OccurrenceType.get(new ResourceLocation(dyn.get("rl").asString("")));
		if (dyn.get("posX").get().isPresent()) {
			this.position = new Vec3d(dyn.get("posX").asDouble(0), dyn.get("posY").asDouble(0),
					dyn.get("posZ").asDouble(0));
		}
		this.readData(dyn.get("data").get().get());
	}

	public abstract void readData(Dynamic<?> dyn);

	public abstract <T> T writeData(DynamicOps<T> ops);

	public OccurrenceType<?> getType() {
		return type;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T dat = writeData(ops);
		T rl = ops.createString(this.type.getName().toString());

		Map<T, T> mapa = ImmutableMap.of(ops.createString("data"), dat, ops.createString("rl"), rl);
		if (this.position != null) {
			mapa.put(ops.createString("posX"), ops.createDouble(position.x));
			mapa.put(ops.createString("posY"), ops.createDouble(position.y));
			mapa.put(ops.createString("posZ"), ops.createDouble(position.z));

		}
		return ops.createMap(mapa);
	}

	public abstract Object[] getDataForDisplay(CitizenEntity en);

	public Set<CitizenTask> getPotentialWitnessReactions() {
		return new HashSet<>();
	}

	/**
	 * Affect the given citizen's mental state
	 * 
	 * @param e
	 */
	public void affectCitizen(CitizenInformation<CitizenEntity> e) {

	}

	/**
	 * Return a list of entities that would witness this occurrence. Do not
	 * manipulate the occurrence's fields in any way!
	 * 
	 * @param event
	 * @param gametime
	 * @param daytime
	 */
	public Set<CitizenEntity> propagate(WorldTickEvent event, long gametime, long daytime) {
		if (this.position != null) {
			Set<CitizenEntity> es = new HashSet<>();
			List<CitizenEntity> inBounds = event.world.getEntitiesWithinAABB(CitizenEntity.class,
					new AxisAlignedBB(new BlockPos(position)).grow(40));
			for (CitizenEntity e : inBounds) {
				if (this.canOccurrenceBeSeen(e)) {
					es.add(e);
				}
			}
		}
		return Sets.newHashSet();
	}

	public boolean canOccurrenceBeSeen(LivingEntity e) {

		Vec3d vec3d = new Vec3d(e.getPosX(), e.getPosYEye(), e.getPosZ());
		Vec3d vec3d1 = new Vec3d(position.x, position.y, position.z);

		return e.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.COLLIDER,
				RayTraceContext.FluidMode.NONE, e)).getType() == RayTraceResult.Type.MISS;

	}

	public void tick(WorldTickEvent event, long gameTime, long dayTime) {
		this.propagate(event, gameTime, dayTime);

	}

	public boolean shouldEnd() {
		return true;
	}

	public abstract OccurrenceEffect getEffect();

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " of type " + this.getType();
	}

	public boolean equalsWithPos(Object o) {
		if (!(o instanceof Occurrence))
			return false;
		Occurrence occ = (Occurrence) o;
		return occ.serialize(NBTDynamicOps.INSTANCE).equals(this.serialize(NBTDynamicOps.INSTANCE));
	}

	public abstract boolean isSimilarTo(Occurrence other);

	public boolean equals(Object oth) {
		return ((Occurrence) oth).writeData(NBTDynamicOps.INSTANCE).equals(this.writeData(NBTDynamicOps.INSTANCE));
	}

	public static <T extends Occurrence> T copy(T one) {
		T b = (T) one.getType().deserializeDat(GMNBT.makeDynamic(one.serialize(NBTDynamicOps.INSTANCE)));
		return b;
	}

	public boolean couldBeCauseOf(Occurrence other, long thisPerformanceTime, long otherPerformanceTime) {
		return otherPerformanceTime - thisPerformanceTime > 0 && otherPerformanceTime - thisPerformanceTime < 100;
	}

}
