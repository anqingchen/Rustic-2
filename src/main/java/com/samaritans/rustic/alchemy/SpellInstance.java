package com.samaritans.rustic.alchemy;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class SpellInstance {
	
	private AlchemySpell spell;
	private float castingStrength;
	
	public SpellInstance(SpellEffect effect, int potencyLevel, int durationLevel, float castingStrength) {
		this(new AlchemySpell(effect, potencyLevel, durationLevel), castingStrength);
	}
	public SpellInstance(AlchemySpell spell, float castingStrength) {
		this.spell = spell;
		this.castingStrength = MathHelper.clamp(castingStrength, 0f, 1f);
	}
	
	public AlchemySpell getSpell() {
		return this.spell;
	}
	
	public SpellEffect getSpellEffect() {
		return (this.spell != null) ? this.spell.getSpellEffect() : null;
	}
	
	public float getCastingStrength() {
		return castingStrength;
	}
	
	public int getPotencyLevel() {
		return (this.spell != null) ? this.spell.getPotencyLevel() : 0;
	}
	
	public int getDurationLevel() {
		return (this.spell != null) ? this.spell.getDurationLevel() : 0;
	}
	
	public SpellInstance setCastingStrength(float castingStrength) {
		this.castingStrength = MathHelper.clamp(castingStrength, 0f, 1f);
		return this;
	}
	
	public SpellInstance setPotencyLevel(int potencyLevel) {
		if (this.spell != null) this.spell.setPotencyLevel(potencyLevel);
		return this;
	}
	
	public SpellInstance setDurationLevel(int durationLevel) {
		if (this.spell != null) this.spell.setDurationLevel(durationLevel);
		return this;
	}
	
	public boolean applyEffect(CastingContext context) {
		if (this.spell != null && !this.spell.isEmpty())
			return spell.getSpellEffect().applyEffect(this, context);
		return false;
	}
	public boolean applyEffect(RayTraceResult rayTraceResult, Entity sourceEntity, Entity caster, World world) {
		return applyEffect(new CastingContext(rayTraceResult, sourceEntity, caster, world));
	}
	
	public static SpellInstance read(PacketBuffer buf) {
		return new SpellInstance(AlchemySpell.read(buf), buf.readFloat());
	}
	
	public void write(PacketBuffer buf) {
		AlchemySpell spell = (this.spell != null) ? this.spell : new AlchemySpell(null, 0, 0);
		spell.write(buf);
		buf.writeFloat(castingStrength);
	}
	
	public static SpellInstance read(CompoundNBT nbt) {
		AlchemySpell spell = AlchemySpell.read(nbt);
		if (spell == null || spell.isEmpty() || nbt == null || !nbt.contains("castingStrength", Constants.NBT.TAG_FLOAT))
			return null;
		return new SpellInstance(spell, nbt.getFloat("castingStrength"));
	}

	public CompoundNBT write(CompoundNBT nbt) {
		spell.write(nbt);
		nbt.putFloat("castingStrength", castingStrength);
		return nbt;
	}
	
	public CompoundNBT toNBT() {
		return write(new CompoundNBT());
	}
	
}
