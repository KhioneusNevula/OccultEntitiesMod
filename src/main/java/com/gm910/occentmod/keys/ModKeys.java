package com.gm910.occentmod.keys;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public enum ModKeys {

	RECAST
	;
	public KeyBinding binding() {
		return map.get(this);
	}
	
	public static final Map<ModKeys, KeyBinding> map = new HashMap<>();
	
	public static void firstinit() {
		
		map.put(RECAST, new KeyBinding("key.elkloria.recast", KeyEvent.VK_ENTER, "keycat.elkloria.wizard"));
	
	}
	
	public static void clientinit() {
		
		for (ModKeys key : map.keySet()) {
			ClientRegistry.registerKeyBinding(map.get(key));
		}
	}
}
