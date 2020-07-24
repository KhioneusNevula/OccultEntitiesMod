package com.gm910.occentmod.api.events;

import net.minecraftforge.eventbus.api.Event;

public class ServerKeyEvent extends Event {

	public final int key;
	
	public ServerKeyEvent(int key) {
		this.key = key;
	}
	
	public int getKey() {
		return key;
	}

}
