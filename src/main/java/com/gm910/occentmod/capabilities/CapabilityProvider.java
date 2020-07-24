package com.gm910.occentmod.capabilities;

import java.util.ArrayList;
import java.util.List;

import com.gm910.occentmod.capabilities.wizardcap.IWizard;
import com.gm910.occentmod.capabilities.wizardcap.Wizard;
import com.gm910.occentmod.capabilities.wizardcap.WizardStorage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class CapabilityProvider<T, K> implements ICapabilitySerializable<CompoundNBT> {
	

	@CapabilityInject(IWizard.class)
	public static Capability<IWizard> WIZARD = null;
	
	
	private Capability<T> capability;
	
	private K owner;
	
	private T instance;
	
	public CapabilityProvider(Capability<T> capability, K owner) {
		this.capability = capability;
		this.owner = owner;
		this.instance = capability.getDefaultInstance();
		if (instance instanceof IModCapability) {
			((IModCapability<K>) instance).$setOwner(owner);
		}
	}
	
	public K getOwner() {
		return owner;
	}
	
	public T getInstance() {
		
		return instance;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return this.capability.orEmpty(cap, LazyOptional.of(() -> instance));
	}

	@Override
	public CompoundNBT serializeNBT() {
		return (CompoundNBT) this.capability.getStorage().writeNBT(capability, instance, null);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.capability.getStorage().readNBT(capability, instance, null, nbt);
	}
	
	public static void preInit() {
		Capability<IWizard> cap = CapabilityProvider.WIZARD;
		
		CapabilityManager.INSTANCE.register(IWizard.class, new WizardStorage(), () -> {
			return new Wizard();
		});
		System.out.println("THE VALUE OF CP WIZ IS NOW " + CapabilityProvider.WIZARD);
	}
	
	@SubscribeEvent
	public static void attachEn(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof PlayerEntity) {
			event.addCapability(Wizard.LOC, new CapabilityProvider<IWizard, LivingEntity>(CapabilityProvider.WIZARD, (LivingEntity) event.getObject()));
		}
	}
	
}
