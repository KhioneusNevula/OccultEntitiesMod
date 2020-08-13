package com.gm910.occentmod.capabilities.formshifting;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.capabilities.CapabilityProvider;
import com.gm910.occentmod.entities.citizen.mind_and_traits.BodyForm;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class Formshift implements INBTSerializable<CompoundNBT> {

	public static final ResourceLocation LOC = new ResourceLocation(OccultEntities.MODID, "shape_form");

	private BodyForm trueForm = new BodyForm();
	private BodyForm form = trueForm;

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT comp = new CompoundNBT();
		comp.putUniqueId("Form", form.getFormId());
		comp.putUniqueId("True", trueForm.getFormId());
		return comp;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.form = new BodyForm(nbt.getUniqueId("Form"));
		this.trueForm = new BodyForm(nbt.getUniqueId("True"));
	}

	public BodyForm getForm() {
		return form;
	}

	public BodyForm getTrueForm() {
		return trueForm;
	}

	public void changeForm(BodyForm form) {
		this.form = form;
	}

	public void revertForm() {
		this.form = trueForm;
	}

	public void recreateTrueForm(boolean changePhysicalFormToMatch) {
		this.trueForm = new BodyForm();
		if (changePhysicalFormToMatch) {
			this.form = trueForm;
		}
	}

	public void reincarnateFormAs(BodyForm form, boolean changePhysicalFormToMatch) {
		this.trueForm = form;
		if (changePhysicalFormToMatch)
			this.form = trueForm;
	}

	public static Formshift get(LivingEntity player) {
		return player.getCapability(CapabilityProvider.FORM).orElse(null);
	}

}
