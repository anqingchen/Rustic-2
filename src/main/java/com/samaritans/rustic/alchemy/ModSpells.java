package com.samaritans.rustic.alchemy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.samaritans.rustic.Rustic;
import com.samaritans.rustic.Util;

import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = Rustic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Rustic.MODID)
public class ModSpells {
	
	public static final SpellEffect POISON = null;
	public static final SpellEffect REGENERATION = null;
	public static final SpellEffect WITHER = null;
	public static final SpellEffect INSTANT_DAMAGE = null;
	public static final SpellEffect INSTANT_HEALTH = null;
	public static final SpellEffect IGNITE = null;
	
	@SubscribeEvent
	public static void onRegisterSpellEffects(RegistryEvent.Register<SpellEffect> event) {
		IForgeRegistry<SpellEffect> registry = event.getRegistry();
		
		registry.registerAll(
				Util.setup(new PotionSpellEffect(Effects.POISON, 1, 600, 1200, 1800), "poison"),
				Util.setup(new PotionSpellEffect(Effects.REGENERATION, 1, 600, 1200, 1800), "regeneration"),
				Util.setup(new PotionSpellEffect(Effects.WITHER, 1, 600, 1200, 1800), "wither"),
				Util.setup(new PotionSpellEffect(Effects.INSTANT_DAMAGE, 1, 0), "instant_damage"),
				Util.setup(new PotionSpellEffect(Effects.INSTANT_HEALTH, 1, 0), "instant_health"),
				Util.setup(new IgniteSpellEffect(), "ignite")
		);
	}
	
	public static ArrayList<AlchemySpell> getAllAlchemySpells() {
		ArrayList<AlchemySpell> spells = new ArrayList<>();
		for (SpellEffect effect : SpellEffect.REGISTRY.getValues()) {
			effect.fillAlchemySpellList(spells);
		}
		return spells;
	}
	
	@SubscribeEvent
	public static void createRegistries(RegistryEvent.NewRegistry event) throws Exception {
		IForgeRegistry<SpellEffect> spellEffectRegistry = new RegistryBuilder<SpellEffect>()
				.setType(SpellEffect.class)
				.setName(new ResourceLocation(Rustic.MODID, "spell_effects"))
				/*.set(key -> new SpellEffect() {
					@Override public void applyEffect(SpellInstance spell, CastingContext context) {}
					@Override public int getMaxModifierLevel(SpellModifier modifier) { return 0; }
					@Override public Category getCategory() { return Category.UTILITY; }
				}.setRegistryName(key))*/
				.create();
		
		/* Set SpellEffect.REGISTRY (using reflection because I wanted it to be a public static field,
		 * but not making it final made me uncomfortable
		 */
		Field spellEffectRegField = SpellEffect.class.getDeclaredField("REGISTRY");
		if (spellEffectRegField != null) {
			spellEffectRegField.setAccessible(true);
			int fieldModifiers = spellEffectRegField.getModifiers();
			if (Modifier.isFinal(fieldModifiers)) {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(spellEffectRegField, fieldModifiers & ~Modifier.FINAL);
			}
			spellEffectRegField.set(null, spellEffectRegistry);
		}
		SpellEffect.REGISTRY.isEmpty(); // Just here to throw an Exception if SpellEffect.REGISTRY is null
	}
	
}
