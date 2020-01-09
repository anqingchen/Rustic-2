package com.samaritans.rustic.proxy;

import net.minecraftforge.api.distmarker.Dist;

public interface IProxy {
	
	public Dist getDist();
	
	default public boolean isClient() {
		return getDist().isClient();
	}
	default public boolean isDedicatedServer() {
		return getDist().isDedicatedServer();
	}
	
	/* 
	 * TODO add functions that can be called from client or server side,
	 * but with different implementations depending on the side
	 * (creating particles, etc.)
	 */
	
}
