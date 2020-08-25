package com.gm910.occentmod.api.networking;

import javax.annotation.Nullable;

import com.gm910.occentmod.api.networking.messages.ModTask;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class TaskEvent extends Event implements Runnable {

	private final ModTask task;

	private ServerPlayerEntity source;

	/**
	 * If sender is server, source MUST BE null
	 * 
	 * @param task
	 * @param source
	 */
	public TaskEvent(ModTask task, @Nullable ServerPlayerEntity source) {
		this.task = task;
		this.source = source;
	}

	public ModTask getTask() {
		return task;
	}

	public void run() {
		this.task.run();
	}

	public ServerPlayerEntity getSource() {
		return source;
	}

	public boolean isSenderServer() {
		return source == null;
	}

	public void setSource(ServerPlayerEntity source) {
		this.source = source;
	}

	public boolean shouldRunByDefault() {
		return true;
	}
}
