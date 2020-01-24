package com.samaritans.rustic.alchemy;

import java.util.List;

import javax.annotation.Nonnull;

import com.samaritans.rustic.client.particles.SpellProjectileParticleData;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

public abstract class SpellEffect extends ForgeRegistryEntry<SpellEffect> implements Comparable<SpellEffect> {
	
	public static final IForgeRegistry<SpellEffect> REGISTRY = null;
	
	
	private String translationKey = null;
	
	public abstract boolean applyEffect(SpellInstance spell, CastingContext context);
	
	public abstract int getMaxModifierLevel(SpellModifier modifier);
	
	// returns true if the modifier can be applied to this effect
	public boolean canHaveModifier(SpellModifier modifier) {
		return getMaxModifierLevel(modifier) > 0;
	}
	
	public abstract Category getCategory();
	
	public abstract int getColor();
	
	@OnlyIn(Dist.CLIENT)
	public void spawnProjectileParticle(Entity projectile, double x, double y, double z, double velX, double velY, double velZ) {
		projectile.world.addParticle(new SpellProjectileParticleData(this.getColor()), true, x, y, z, velX, velY, velZ);
	}
	
	public RayTraceContext.BlockMode getRayTraceBlockMode() {
		return RayTraceContext.BlockMode.OUTLINE;
	}
	
	public RayTraceContext.FluidMode getRayTraceFluidMode() {
		return RayTraceContext.FluidMode.NONE;
	}
	
	public abstract int getAutoCastingCooldown(int potencyLevel, int durationLevel);
	
	public abstract boolean shouldAutoCast(@Nonnull LivingEntity entity);
	
	protected String getDefaultTranslationKey() {
		if (this.translationKey == null)
			this.translationKey = net.minecraft.util.Util.makeTranslationKey("spell_effect", this.getRegistryName());
		return this.translationKey;
	}
	
	public String getTranslationKey() {
		return this.getDefaultTranslationKey();
	}
	
	public String getTranslationKey(AlchemySpell spell) {
		return this.getTranslationKey();
	}
	
	// TODO remove?
	protected String getEffectName() {
		return this.getRegistryName().getPath();
	}
	
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(this.getTranslationKey());
	}
	
	public ITextComponent getDisplayName(AlchemySpell spell) {
		return new TranslationTextComponent(this.getTranslationKey(spell));
	}
	
	@OnlyIn(Dist.CLIENT)
	public void addInformation(AlchemySpell spell, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		final int potencyLvl = spell.getPotencyLevel();
		final int durationLvl = spell.getDurationLevel();
		if (potencyLvl > 0) {
			ITextComponent potencyText = new TranslationTextComponent("spell_modifier.rustic.potency");
			if (potencyLvl > 10) potencyText.appendText(" " + potencyLvl);
			else potencyText.appendText(" ").appendSibling(new TranslationTextComponent("numeral.rustic." + potencyLvl));
			tooltip.add(potencyText.applyTextStyle(TextFormatting.DARK_AQUA));
		}
		if (durationLvl > 0) {
			ITextComponent durationText = new TranslationTextComponent("spell_modifier.rustic.duration");
			if (durationLvl > 10) durationText.appendText(" " + durationLvl);
			else durationText.appendText(" ").appendSibling(new TranslationTextComponent("numeral.rustic." + durationLvl));
			tooltip.add(durationText.applyTextStyle(TextFormatting.DARK_AQUA));
		}
	}
	
	public void fillAlchemySpellList(List<AlchemySpell> spells) {
		spells.add(new AlchemySpell(this, 0, 0));
		if (this.getMaxModifierLevel(SpellModifier.POTENCY) > 0 || this.getMaxModifierLevel(SpellModifier.DURATION) > 0) {
			spells.add(new AlchemySpell(this, this.getMaxModifierLevel(SpellModifier.POTENCY), this.getMaxModifierLevel(SpellModifier.DURATION)));
		}
	}
	
	public static SpellEffect read(PacketBuffer buf) {
		//return buf.<SpellEffect>readRegistryId();
		return buf.<SpellEffect>readRegistryIdSafe(SpellEffect.class);
	}
	
	public void write(PacketBuffer buf) {
		buf.writeRegistryId(this);
	}
	
	public static SpellEffect read(CompoundNBT nbt) {
		if (nbt != null && nbt.contains("spell_effect", Constants.NBT.TAG_STRING)) {
			ResourceLocation id = new ResourceLocation(nbt.getString("spell_effect"));
			return REGISTRY.getValue(id);
		}
		return null;
	}
	
	public CompoundNBT write(CompoundNBT nbt) {
		if (this.getRegistryName() != null)
			nbt.putString("spell_effect", this.getRegistryName().toString());
		return nbt;
	}
	
	@Override
	public String toString() {
		return this.getRegistryName().getPath();
	}
	
	@Override
	public int compareTo(SpellEffect b) {
		if (this == b) return 0;
		return getRegistryName().compareTo(b.getRegistryName());
	}
	
		
	public static enum Category {
		BENEFICIAL(),
		HARMFUL(),
		NEUTRAL();
	}
	
}
