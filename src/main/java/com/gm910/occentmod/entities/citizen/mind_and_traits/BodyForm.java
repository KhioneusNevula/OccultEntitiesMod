package com.gm910.occentmod.entities.citizen.mind_and_traits;

import java.util.UUID;

import net.minecraft.util.math.MathHelper;

/**
 * Simply represents the concept of a unique appearance, since every citizen and
 * player will have a unique appearance but people will be capable of switching
 * appearances
 * 
 * @author borah
 *
 */
public class BodyForm {

	private UUID formId;

	public BodyForm() {
		this.formId = MathHelper.getRandomUUID();
	}

	public BodyForm(UUID basis) {
		this.formId = basis;
	}

	public UUID getFormId() {
		return formId;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return ((BodyForm) obj).formId.equals(formId);
	}

}
