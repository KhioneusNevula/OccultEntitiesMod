package com.gm910.occentmod.api.networking.messages;

import com.gm910.occentmod.api.networking.TaskEvent;
import com.gm910.occentmod.api.util.ModReflect;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

/**
 * ALL MUST HAVE A NOARGS CONSTRUCTOR OR A SINGLE-STRING CONSTRUCTOR accepting
 * the data it stores itself as
 * 
 * @author borah
 *
 */
public abstract class ModTask implements Runnable {

	// private static final String separator = ((char)1)+"}}{{"+ ((char)0);

	/**
	 * Used to check logical sides, mostly
	 */
	private World worldRef;

	/**
	 * Player that sent the event, or null if from server
	 */
	private ServerPlayerEntity sender = null;

	public abstract void run();

	public TaskEvent createEvent() {
		return new TaskEvent(this, null);
	}

	public static ModTask getFromNBT(CompoundNBT nbt) {
		Class<? extends ModTask> clazz = ModTask.class;
		String classstring = nbt.getString("Class");
		CompoundNBT data = nbt.getCompound("Data");
		try {
			clazz = (Class<? extends ModTask>) Class.forName(classstring);
		} catch (ClassNotFoundException e) {
			return null;
		}
		ModTask tasque = ModReflect.construct(clazz);
		if (tasque == null)
			ModReflect.construct(clazz, data);
		if (tasque != null) {
			tasque.read(data);
		}

		return tasque;
	}

	public static CompoundNBT writeToNBT(ModTask towrite) {
		return towrite.$write();
	}

	public final CompoundNBT $write() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("Class", this.getClass().getName());
		nbt.put("Data", this.write());
		return nbt;
	}

	public CompoundNBT write() {
		return new CompoundNBT();
	}

	protected void read(CompoundNBT nbt) {

	}

	public World getWorldRef() {
		return worldRef;
	}

	public void setWorldRef(World worldRef) {
		this.worldRef = worldRef;
	}

	public ServerPlayerEntity getSender() {
		return sender;
	}

	public void setSender(ServerPlayerEntity sender) {
		this.sender = sender;
	}

	/**
	 * Whether it can be called on the logical client, default true
	 * 
	 * @return
	 */
	public boolean isLClient() {
		return true;
	}

	/**
	 * Whether it can be called on logical server, default true
	 * 
	 * @return
	 */
	public boolean isLServer() {
		return true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName();
	}

}
