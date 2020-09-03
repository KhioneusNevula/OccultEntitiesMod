package com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds;

import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceEffect;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceType;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;

public class ExistAtLocationDeed extends SapientDeed {

	private ServerPos location;

	public ExistAtLocationDeed(SapientIdentity citizen, ServerPos location) {
		super(OccurrenceType.EXIST_AT_LOCATION, citizen);
		this.location = location;
	}

	public ExistAtLocationDeed() {
		super(OccurrenceType.EXIST_AT_LOCATION);
	}

	public ServerPos getLocation() {
		return location;
	}

	public void setLocation(ServerPos location) {
		this.location = location;
	}

	@Override
	public void readAdditionalData(Dynamic<?> dyn) {
		this.location = ServerPos.deserialize(dyn);
	}

	@Override
	public <T> T writeAdditionalData(DynamicOps<T> ops) {
		return location.serialize(ops);
	}

	@Override
	public Object[] getDataForDisplay(LivingEntity en) {
		return new Object[] { location };
	}

	@Override
	public OccurrenceEffect getEffect() {
		return new OccurrenceEffect(ImmutableMap.of());
	}

	@Override
	public boolean isSimilarTo(Occurrence other) {
		return other instanceof ExistAtLocationDeed
				&& ((ExistAtLocationDeed) other).getLocation().distanceSq(this.location) <= 4;
	}

}
