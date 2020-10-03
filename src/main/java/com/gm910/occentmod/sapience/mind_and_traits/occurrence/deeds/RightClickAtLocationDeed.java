package com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds;

import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceType;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;

public class RightClickAtLocationDeed extends ExistAtLocationDeed {

	public RightClickAtLocationDeed(SapientIdentity citizen, ServerPos location) {
		super(citizen, location);
		this.type = OccurrenceType.RIGHT_CLICK_AT_LOCATION;
	}

	public RightClickAtLocationDeed() {
		super();
		this.type = OccurrenceType.RIGHT_CLICK_AT_LOCATION;
	}

	@Override
	public boolean isSimilarTo(Occurrence other) {
		return other instanceof RightClickAtLocationDeed
				&& ((RightClickAtLocationDeed) other).getLocation().equals(this.getLocation());
	}

}
