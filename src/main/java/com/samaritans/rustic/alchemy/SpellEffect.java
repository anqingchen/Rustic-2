package com.samaritans.rustic.alchemy;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

public abstract class SpellEffect extends ForgeRegistryEntry<SpellEffect> {
	
	public static final IForgeRegistry<SpellEffect> REGISTRY = null;
	
	
	public abstract void applyEffect(SpellInstance spell, CastingContext context);
	
	public abstract int getMaxModifierLevel(SpellModifier modifier);
	
	// returns true if the modifier can be applied to this effect
	public boolean canHaveModifier(SpellModifier modifier) {
		return getMaxModifierLevel(modifier) > 0;
	}
	
	public abstract Category getCategory();
	
	public abstract int getColor();
	
	public static SpellEffect read(PacketBuffer buf) {
		//return buf.<SpellEffect>readRegistryId();
		return buf.<SpellEffect>readRegistryIdSafe(SpellEffect.class);
	}
	
	public void write(PacketBuffer buf) {
		buf.writeRegistryId(this);
	}
	
	public static SpellEffect read(CompoundNBT nbt) {
		if (nbt.contains("spell_effect", Constants.NBT.TAG_STRING)) {
			ResourceLocation id = new ResourceLocation(nbt.getString("spell_effect"));
			return REGISTRY.getValue(id);
		}
		return null;
	}
	
	public void write(CompoundNBT nbt) {
		if (this.getRegistryName() != null)
			nbt.putString("spell_effect", this.getRegistryName().toString());
	}
	
		
	public static enum Category {
		BENEFICIAL(),
		HARMFUL(),
		NEUTRAL();
	}
	
}
