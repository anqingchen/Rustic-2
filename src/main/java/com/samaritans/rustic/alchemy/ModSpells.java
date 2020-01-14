package com.samaritans.rustic.alchemy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.samaritans.rustic.Rustic;

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
	
	@SubscribeEvent
	public static void onRegisterSpellEffects(RegistryEvent.Register<SpellEffect> event) {
		IForgeRegistry<SpellEffect> registry = event.getRegistry();
		
		// TODO register SpellEffects
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
