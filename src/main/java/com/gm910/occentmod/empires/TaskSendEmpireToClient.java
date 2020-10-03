package com.gm910.occentmod.empires;

import com.gm910.occentmod.api.networking.messages.ModTask;
import com.gm910.occentmod.api.util.GMNBT;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;

public class TaskSendEmpireToClient extends ModTask {

	public EmpireInfo info;

	public TaskSendEmpireToClient(Empire emp) {
		this.info = new EmpireInfo(emp);
	}

	public TaskSendEmpireToClient() {
	}

	@Override
	public void run() {
		EmpireInfo.clientSideEmpireInfo.removeIf((e) -> e.getEmpireId().equals(info.getEmpireId()));
		EmpireInfo.clientSideEmpireInfo.add(info);
	}

	@Override
	public CompoundNBT write() {
		return ((CompoundNBT) info.serialize(NBTDynamicOps.INSTANCE));
	}

	@Override
	public void read(CompoundNBT nbt) {
		this.info = new EmpireInfo().deserialize(GMNBT.makeDynamic(nbt));
	}

	@Override
	public String toString() {
		return super.toString() + ": " + this.info;
	}

}
