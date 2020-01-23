package com.samaritans.rustic.alchemy;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AlchemySpell implements Comparable<AlchemySpell> {

	private SpellEffect effect;
	private int potencyLevel, durationLevel;
	
	public AlchemySpell(SpellEffect effect, int potencyLevel, int durationLevel) {
		this.effect = effect;
		this.potencyLevel = Math.max(potencyLevel, 0);
		this.durationLevel = Math.max(durationLevel, 0);
	}
	
	public AlchemySpell(SpellEffect effect) {
		this(effect, 0, 0);
	}
	
	public SpellEffect getSpellEffect() {
		return effect;
	}
	
	public AlchemySpell setSpellEffect(SpellEffect effect) {
		this.effect = effect;
		return this;
	}
	
	public int getPotencyLevel() {
		return potencyLevel;
	}
	
	public AlchemySpell setPotencyLevel(int potencyLevel) {
		this.potencyLevel = Math.max(potencyLevel, 0);
		return this;
	}
	
	public int getDurationLevel() {
		return durationLevel;
	}
	
	public AlchemySpell setDurationLevel(int durationLevel) {
		this.durationLevel = Math.max(durationLevel, 0);
		return this;
	}
	
	public boolean isEmpty() {
		return this.effect == null;
	}
	
	public ITextComponent getDisplayName() {
		if (this.effect == null) return new StringTextComponent("");
		return this.effect.getDisplayName(this);
	}
	
	@OnlyIn(Dist.CLIENT)
	public void addInformation(List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (!this.isEmpty())
			this.effect.addInformation(this, tooltip, flagIn);
	}
	
	public static AlchemySpell read(PacketBuffer buf) {
		if (buf.readBoolean())
			return new AlchemySpell(SpellEffect.read(buf), buf.readVarInt(), buf.readVarInt());
		return new AlchemySpell(null, 0, 0);
	}
	
	public void write(PacketBuffer buf) {
		if (this.isEmpty()) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			this.effect.write(buf);
			buf.writeVarInt(this.potencyLevel);
			buf.writeVarInt(this.durationLevel);
		}
	}
	
	public static AlchemySpell read(CompoundNBT nbt) {
		SpellEffect effect = SpellEffect.read(nbt);
		if (effect == null || nbt == null) return null;
		return new AlchemySpell(effect, nbt.getInt("potencyLevel"), nbt.getInt("durationLevel"));
	}
	
	public CompoundNBT write(CompoundNBT nbt) {
		if (effect != null) effect.write(nbt);
		nbt.putInt("potencyLevel", potencyLevel);
		nbt.putInt("durationLevel", durationLevel);
		return nbt;
	}
	
	public CompoundNBT toNBT() {
		return write(new CompoundNBT());
	}

	@Override
	public boolean equals(Object b) {
		if ((b instanceof AlchemySpell) && (b != null)) {
			AlchemySpell spell = (AlchemySpell) b;
			if (spell.isEmpty() && this.isEmpty()) return true;
			return spell.effect == this.effect &&
					spell.potencyLevel == this.potencyLevel &&
					spell.durationLevel == this.durationLevel;
		}
		return false;
	}
	
	@Override
	public int compareTo(AlchemySpell b) {
		if (this.isEmpty() && b.isEmpty()) return 0;
		if (this.isEmpty() && !b.isEmpty()) return -1;
		if (b.isEmpty()) return 1;
		// sort first by effect type
		int effectComp = this.effect.compareTo(b.effect);
		if (effectComp != 0) return effectComp;
		int potencyComp = this.potencyLevel - b.potencyLevel;
		int modifierComp = potencyComp + (this.durationLevel - b.durationLevel);
		// then sort by combined modifier level
		if (modifierComp != 0) return modifierComp;
		// then sort by potency level
		return potencyComp;
	}
	
	@Override
	public String toString() {
		return this.effect + " + " + this.potencyLevel + " * " + this.durationLevel;
	}
	
}
