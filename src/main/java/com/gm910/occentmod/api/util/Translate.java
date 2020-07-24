package com.gm910.occentmod.api.util;

import net.minecraft.util.text.TranslationTextComponent;

public class Translate {

	public static String translate(String key, Object...args) {
		return (new TranslationTextComponent(key, args)).getFormattedText();
	}
	
	/**
	 * If I'm too lazy to use the constructor for whatever reason...
	 * @param key
	 * @return
	 */
	public static TranslationTextComponent make(String key, Object...args) {
		return new TranslationTextComponent(key, args);
	}
}
