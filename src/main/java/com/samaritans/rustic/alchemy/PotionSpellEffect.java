package com.samaritans.rustic.alchemy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

public class PotionSpellEffect extends SpellEffect {

	public final Effect potionType;
	private final int maxPotency;
	private final int[] durations;
	
	PotionSpellEffect(Effect potionType, int maxPotency, int baseDuration, int... boostedDurations) {
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
	}
	
	@Override
	public void applyEffect(SpellInstance spell, CastingContext context) {
		Entity targetEntity = context.getTargetEntity();
		if (targetEntity != null && targetEntity instanceof LivingEntity) {
			LivingEntity target = (LivingEntity) targetEntity;
			if (potionType.isInstant()) {
				potionType.affectEntity(context.sourceEntity, context.caster, target, spell.getPotencyLevel(), spell.getCastingStrength());
			} else {
				int durationLevel = spell.getDurationLevel();
				int duration = (durationLevel < durations.length) ? durations[durationLevel] : durations[durations.length - 1];
				duration = (int) (duration * spell.getCastingStrength());
				target.addPotionEffect(new EffectInstance(potionType, duration, spell.getPotencyLevel(), false, true));
			}
		}
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
	
	public int getBaseDuration() {
		return durations[0];
	}

}
