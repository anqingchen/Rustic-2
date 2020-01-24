package com.samaritans.rustic.alchemy;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PotionSpellEffect extends SpellEffect {

	public final Effect potionType;
	protected final int maxPotency;
	protected final int[] durations;
	protected final boolean usePotionEffectName;
	
	PotionSpellEffect(Effect potionType, boolean usePotionEffectName, int maxPotency, int baseDuration, int... boostedDurations) {
		this.potionType = potionType;
		this.maxPotency = maxPotency;
		if (potionType.isInstant()) {
			this.durations = new int[] { 0 };
		} else {
			this.durations = new int[boostedDurations.length + 1];
			this.durations[0] = baseDuration;
			for (int i = 0; i < boostedDurations.length; i++)
				this.durations[i + 1] = boostedDurations[i];
		}
		this.usePotionEffectName = usePotionEffectName;
	}
	
	PotionSpellEffect(Effect potionType, int maxPotency, int baseDuration, int... boostedDurations) {
		this(potionType, true, maxPotency, baseDuration, boostedDurations);
	}
	
	@Override
	public boolean applyEffect(SpellInstance spell, CastingContext context) {
		if (context.world.isRemote) return false;
		Entity targetEntity = context.getTargetEntity();
		if (targetEntity != null && targetEntity instanceof LivingEntity) {
			LivingEntity target = (LivingEntity) targetEntity;
			if (potionType.isInstant()) {
				potionType.affectEntity(context.sourceEntity, context.caster, target, spell.getPotencyLevel(), spell.getCastingStrength());
			} else {
				int durationLevel = spell.getDurationLevel();
				int duration = (durationLevel < durations.length) ? durations[durationLevel] : durations[durations.length - 1];
				// TODO change how duration scales with potency?
				duration = (int) (duration * spell.getCastingStrength() / (spell.getPotencyLevel() + 0.8f));
				target.addPotionEffect(new EffectInstance(potionType, duration, spell.getPotencyLevel(), false, true));
			}
			return true;
		}
		return false;
	}

	@Override
	public int getMaxModifierLevel(SpellModifier modifier) {
		switch (modifier) {
		case POTENCY:
			return maxPotency;
		case DURATION:
			return durations.length - 1;
		}
		return 0;
	}

	@Override
	public Category getCategory() {
		switch (potionType.getEffectType()) {
		case BENEFICIAL:
			return Category.BENEFICIAL;
		case HARMFUL:
			return Category.HARMFUL;
		default:
			return Category.NEUTRAL;
		}
	}
	
	@Override
	public int getColor() {
		return potionType.getLiquidColor();
	}
	
	@Override
	public RayTraceContext.BlockMode getRayTraceBlockMode() {
		return RayTraceContext.BlockMode.COLLIDER;
	}
	
	@Override
	public int getAutoCastingCooldown(int potencyLevel, int durationLevel) {
		return 20 * 15; // TODO balance (mainly for instant healing effect)
	}
	
	@Override
	public boolean shouldAutoCast(@Nonnull LivingEntity entity) {
		EffectInstance activeEffect = entity.getActivePotionEffect(this.potionType);
		if (activeEffect != null) {
			int timeThreshold = (this.potionType == Effects.NIGHT_VISION || this.potionType == Effects.NAUSEA) ? 200 : 50;
			return activeEffect.getDuration() <= timeThreshold;
		} else {
			return true;
		}
	}
	
	@Override
	public String getTranslationKey() {
		return (this.usePotionEffectName) ? this.potionType.getName() : super.getTranslationKey();
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return (this.usePotionEffectName) ? this.potionType.getDisplayName() : super.getDisplayName();
	}
	
	@Override
	public ITextComponent getDisplayName(AlchemySpell spell) {
		return (this.usePotionEffectName) ? this.potionType.getDisplayName() : super.getDisplayName(spell);
	}
	
	public int getBaseDuration() {
		return durations[0];
	}

}
