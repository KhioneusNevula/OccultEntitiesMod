package com.gm910.occentmod.empires.gods.powers;

import java.util.Optional;

import com.gm910.occentmod.capabilities.magicdata.MagicData;
import com.gm910.occentmod.capabilities.magicdata.MysticEffect;
import com.gm910.occentmod.capabilities.magicdata.MysticEffectType;
import com.gm910.occentmod.empires.Empire;
import com.gm910.occentmod.empires.gods.Deity;
import com.gm910.occentmod.empires.gods.DeityElement;
import com.gm910.occentmod.empires.gods.DeityPower;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.Event;

public abstract class DeityEffectPower extends DeityPower {

	public DeityEffectPower(ResourceLocation loc, DeityElement... elements) {
		super(loc, elements);
	}

	public abstract CompoundNBT generateDataForMysticEffect(LivingEntity target, boolean isBlessing);

	public MysticEffect create(MysticEffectType type, LivingEntity target, Deity d, boolean blessing) {
		return new MysticEffect(type, target.getUniqueID(), d.getUuid(), generateDataForMysticEffect(target, blessing));
	}

	@Override
	public void usePower(Deity owner, Event event) {

		if (!(event instanceof LivingUpdateEvent))
			return;

		LivingEntity e = ((LivingUpdateEvent) event).getEntityLiving();
		Optional<MysticEffect> eff = MagicData.get(e).getEffects().stream().filter((el) -> {
			return el.getOwner().equals(owner.getUuid());
		}).findAny();
		if (!eff.isPresent())
			return;
		Empire em = owner.getEmpire();
		whileEffect(e, owner, em, eff.get());
	}

	public abstract void whileEffect(LivingEntity e, Deity owner, Empire em, MysticEffect actualEffect);

}
