package com.gm910.occentmod.capabilities;

import net.minecraft.entity.Entity;

public interface IModCapability<T> {

	public T $getOwner();
	
	public void $setOwner(T e);
}
