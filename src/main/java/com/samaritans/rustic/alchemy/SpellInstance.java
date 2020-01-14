package com.samaritans.rustic.alchemy;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class SpellInstance {
	
	private SpellEffect effect;
	private float castingStrength;
	private int potencyLevel = 0, durationLevel = 0;
	
	public SpellInstance(SpellEffect effect, float castingStrength, int potencyLevel, int durationLevel) {
		this.effect = effect;
		this.castingStrength = MathHelper.clamp(castingStrength, 0f, 1f);
		this.potencyLevel = Math.max(potencyLevel, 0);
		this.durationLevel = Math.max(durationLevel, 0);
	}
	
	public SpellEffect getSpellEffect() {
		return effect;
	}
	
	public float getCastingStrength() {
		return castingStrength;
	}
	
	public int getPotencyLevel() {
		return potencyLevel;
	}
	
	public int getDurationLevel() {
		return durationLevel;
	}
	
	public SpellInstance setCastingStrength(float castingStrength) {
		this.castingStrength = MathHelper.clamp(castingStrength, 0f, 1f);
		return this;
	}
	
	public SpellInstance setPotencyLevel(int potencyLevel) {
		this.potencyLevel = Math.max(potencyLevel, 0);
		return this;
	}
	
	public SpellInstance setDurationLevel(int durationLevel) {
		this.durationLevel = Math.max(durationLevel, 0);
		return this;
	}
	
	public void applyEffect(CastingContext context) {
		if (effect != null)
			effect.applyEffect(this, context);
	}
	public void applyEffect(RayTraceResult rayTraceResult, Entity sourceEntity, Entity caster, World world) {
		applyEffect(new CastingContext(rayTraceResult, sourceEntity, caster, world));
	}
	
	public static SpellInstance read(PacketBuffer buf) {
		return new SpellInstance(SpellEffect.read(buf), buf.readFloat(), buf.readByte(), buf.readByte());
	}
	
	public void write(PacketBuffer buf) {
		effect.write(buf);
		buf.writeFloat(castingStrength);
		buf.writeByte(potencyLevel);
		buf.writeByte(durationLevel);
	}
	
	public static SpellInstance read(CompoundNBT nbt) {
		SpellEffect effect = SpellEffect.read(nbt);
		if (effect == null || !nbt.contains("castingStrength", Constants.NBT.TAG_FLOAT))
			return null;
		return new SpellInstance(effect, nbt.getFloat("castingStrength"), nbt.getInt("potencyLevel"), nbt.getInt("durationLevel"));
	}

	public void write(CompoundNBT nbt) {
		effect.write(nbt);
		nbt.putFloat("castingStrength", castingStrength);
		nbt.putInt("potencyLevel", potencyLevel);
		nbt.putInt("durationLevel", durationLevel);
	}
	
	public CompoundNBT toNBT() {
		CompoundNBT nbt = new CompoundNBT();
		write(nbt);
		return nbt;
	}
	
}
