package com.gm910.occentmod.damage;

import com.gm910.occentmod.api.util.Translate;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;

public class MagicDamage extends EntityDamageSource {
	
	public static MagicDamage causeSpellDamage(LivingEntity from) {
		return new MagicDamage("spell", from);
	}
	
	public MagicDamage(String damageTypeIn, LivingEntity causer) {
		super(damageTypeIn, causer);
		setMagicDamage();
	}
	
	@Override
	public ITextComponent getDeathMessage(LivingEntity base) {
		
		return Translate.make("message.magicdeath." + (super.getTrueSource() == null ? "fromnone" : "fromwiz"), base.getDisplayName(), (getTrueSource() != null ? getTrueSource().getDisplayName() : null));
	}

}
