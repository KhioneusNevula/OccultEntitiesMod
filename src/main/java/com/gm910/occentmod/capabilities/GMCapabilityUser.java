package com.gm910.occentmod.capabilities;

import com.gm910.occentmod.capabilities.citizeninfo.CitinfoStorage;
import com.gm910.occentmod.capabilities.citizeninfo.CitizenInfo;
import com.gm910.occentmod.capabilities.citizeninfo.PlayerCitizenInformation;
import com.gm910.occentmod.capabilities.formshifting.FormStorage;
import com.gm910.occentmod.capabilities.formshifting.Formshift;
import com.gm910.occentmod.capabilities.magicdata.MagicData;
import com.gm910.occentmod.capabilities.magicdata.WizardStorage;
import com.gm910.occentmod.capabilities.mental_inventory.MindInventory;
import com.gm910.occentmod.capabilities.mental_inventory.MindInventoryStorage;
import com.gm910.occentmod.capabilities.rooms.RoomManager;
import com.gm910.occentmod.capabilities.rooms.RoomStorage;
import com.gm910.occentmod.capabilities.speciallocs.SpecialLocationManager;
import com.gm910.occentmod.capabilities.speciallocs.SpecialLocationStorage;

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
public class GMCapabilityUser<T, K> implements ICapabilitySerializable<CompoundNBT> {

	@CapabilityInject(MagicData.class)
	public static Capability<MagicData> MAGIC_DATA = null;

	@CapabilityInject(CitizenInfo.class)
	public static Capability<CitizenInfo<? extends LivingEntity>> CITIZEN_INFO = null;

	@CapabilityInject(SpecialLocationManager.class)
	public static Capability<SpecialLocationManager> SPECIAL_LOCS = null;

	@CapabilityInject(RoomManager.class)
	public static Capability<RoomManager> ROOMS = null;

	@CapabilityInject(MindInventory.class)
	public static Capability<MindInventory> MIND_INVENTORY = null;

	@CapabilityInject(Formshift.class)
	public static Capability<Formshift> FORM = null;

	private Capability<T> capability;

	private K owner;

	private T instance;

	public GMCapabilityUser(Capability<T> capability, K owner) {
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

		CapabilityManager.INSTANCE.register(MagicData.class, new WizardStorage(), () -> {
			return new MagicData();
		});
		System.out.println("THE VALUE OF CP WIZ IS NOW " + GMCapabilityUser.MAGIC_DATA);
		CapabilityManager.INSTANCE.register(CitizenInfo.class, new CitinfoStorage(), () -> {
			return new PlayerCitizenInformation();
		});
		System.out.println("THE VALUE OF CP info IS NOW " + GMCapabilityUser.CITIZEN_INFO);

		CapabilityManager.INSTANCE.register(SpecialLocationManager.class, new SpecialLocationStorage(), () -> {
			return new SpecialLocationManager();
		});
		System.out.println("THE VALUE OF CP SPECIAL_LOCS IS NOW " + GMCapabilityUser.SPECIAL_LOCS);

		CapabilityManager.INSTANCE.register(RoomManager.class, new RoomStorage(), () -> {
			return new RoomManager();
		});
		System.out.println("THE VALUE OF CP rooms IS NOW " + GMCapabilityUser.ROOMS);

		CapabilityManager.INSTANCE.register(MindInventory.class, new MindInventoryStorage(), () -> {
			return new MindInventory();
		});
		System.out.println("THE VALUE OF CP inventory IS NOW " + GMCapabilityUser.MIND_INVENTORY);
		CapabilityManager.INSTANCE.register(Formshift.class, new FormStorage(), () -> {
			return new Formshift();
		});
		System.out.println("THE VALUE OF CP form IS NOW " + GMCapabilityUser.FORM);
	}

	@SubscribeEvent
	public static void attachEn(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof PlayerEntity) {
			event.addCapability(MindInventory.LOC, new GMCapabilityUser<MindInventory, PlayerEntity>(
					GMCapabilityUser.MIND_INVENTORY, (PlayerEntity) event.getObject()));
			event.addCapability(CitizenInfo.LOC,
					new GMCapabilityUser<CitizenInfo<? extends LivingEntity>, PlayerEntity>(
							GMCapabilityUser.CITIZEN_INFO, (PlayerEntity) event.getObject()));
		}
		if (event.getObject() instanceof LivingEntity) {
			event.addCapability(MagicData.LOC, new GMCapabilityUser<MagicData, LivingEntity>(
					GMCapabilityUser.MAGIC_DATA, (LivingEntity) event.getObject()));
			event.addCapability(Formshift.LOC, new GMCapabilityUser<Formshift, LivingEntity>(GMCapabilityUser.FORM,
					(LivingEntity) event.getObject()));

		}
	}

	@SubscribeEvent
	public static void attachCh(AttachCapabilitiesEvent<World> event) {
		World world = event.getObject();
		if (!(world.isRemote)) {
			event.addCapability(SpecialLocationManager.LOC, new GMCapabilityUser<SpecialLocationManager, World>(
					GMCapabilityUser.SPECIAL_LOCS, event.getObject()));
			event.addCapability(RoomManager.LOC,
					new GMCapabilityUser<RoomManager, World>(GMCapabilityUser.ROOMS, event.getObject()));
		}

	}

}
