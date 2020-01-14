package com.samaritans.rustic.alchemy;

import net.minecraft.network.PacketBuffer;

public enum SpellModifier {
	
	POTENCY("potency"),
	DURATION("duration");
	
	
	private final String name;
	
	SpellModifier(String name) {
		this.name = name;
	}
	
	public String getTranslationKey() {
		return "alchemy.modifier." + name;
	}
	
	
	@Override
	public String toString() {
		return name;
	}
	
}