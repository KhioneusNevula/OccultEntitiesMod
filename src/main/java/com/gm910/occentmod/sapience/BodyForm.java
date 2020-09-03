package com.gm910.occentmod.sapience;

import java.util.UUID;

import net.minecraft.entity.Entity;

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

	public BodyForm(Entity e) {
		this.formId = e.getUniqueID();
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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + " id " + formId;
	}

}
