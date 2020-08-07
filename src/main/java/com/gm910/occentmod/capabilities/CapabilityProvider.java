package com.gm910.occentmod.capabilities;

import com.gm910.occentmod.capabilities.rooms.RoomManager;
import com.gm910.occentmod.capabilities.rooms.RoomStorage;
import com.gm910.occentmod.capabilities.speciallocs.SpecialLocationManager;
import com.gm910.occentmod.capabilities.speciallocs.SpecialLocationStorage;
import com.gm910.occentmod.capabilities.wizardcap.IWizard;
import com.gm910.occentmod.capabilities.wizardcap.Wizard;
import com.gm910.occentmod.capabilities.wizardcap.WizardStorage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
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

	@CapabilityInject(SpecialLocationManager.class)
	public static Capability<SpecialLocationManager> SPECIAL_LOCS = null;

	@CapabilityInject(RoomManager.class)
	public static Capability<RoomManager> ROOMS = null;

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

		CapabilityManager.INSTANCE.register(IWizard.class, new WizardStorage(), () -> {
			return new Wizard();
		});
		System.out.println("THE VALUE OF CP WIZ IS NOW " + CapabilityProvider.WIZARD);

		CapabilityManager.INSTANCE.register(SpecialLocationManager.class, new SpecialLocationStorage(), () -> {
			return new SpecialLocationManager();
		});
		System.out.println("THE VALUE OF CP SPECIAL_LOCS IS NOW " + CapabilityProvider.SPECIAL_LOCS);

		CapabilityManager.INSTANCE.register(RoomManager.class, new RoomStorage(), () -> {
			return new RoomManager();
		});
		System.out.println("THE VALUE OF CP rooms IS NOW " + CapabilityProvider.ROOMS);
	}

	@SubscribeEvent
	public static void attachEn(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof PlayerEntity) {
			event.addCapability(Wizard.LOC, new CapabilityProvider<IWizard, LivingEntity>(CapabilityProvider.WIZARD,
					(LivingEntity) event.getObject()));
		}
	}

	@SubscribeEvent
	public static void attachCh(AttachCapabilitiesEvent<World> event) {
		World world = event.getObject();
		if (!(world.isRemote)) {
			event.addCapability(SpecialLocationManager.LOC, new CapabilityProvider<SpecialLocationManager, World>(
					CapabilityProvider.SPECIAL_LOCS, event.getObject()));
			event.addCapability(RoomManager.LOC,
					new CapabilityProvider<RoomManager, World>(CapabilityProvider.ROOMS, event.getObject()));
		}

	}

}
