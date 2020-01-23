package com.samaritans.rustic.client.particles;

import com.samaritans.rustic.Rustic;
import com.samaritans.rustic.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Rustic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Rustic.MODID)
public class ModParticles {
	
	public static final ParticleType<SpellProjectileParticleData> POTION_SPELL_PROJECTILE = null;
	
	@SubscribeEvent
	public static void onRegisterParticleTypes(RegistryEvent.Register<ParticleType<?>> e) {
		e.getRegistry().registerAll(
				Util.setup(new SpellProjectileParticleData.Type(), "potion_spell_projectile")
		);
	}
	
	@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Rustic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ModParticleFactories {
		@SubscribeEvent
		public static void onRegisterParticleFactories(ParticleFactoryRegisterEvent e) {
			ParticleManager particles = Minecraft.getInstance().particles;
			
			particles.registerFactory(POTION_SPELL_PROJECTILE, SpellProjectileParticle.Factory::new);
		}
	}

}
