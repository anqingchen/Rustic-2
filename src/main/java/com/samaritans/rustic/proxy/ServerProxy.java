package com.samaritans.rustic.proxy;

import net.minecraftforge.api.distmarker.Dist;

public class ServerProxy implements IProxy {
	
	@Override
	public Dist getDist() {
		return Dist.DEDICATED_SERVER;
	}

}
