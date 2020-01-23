package com.samaritans.rustic;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Rustic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSounds {

	public static final SoundEvent LUTE = createSoundEvent("lute");
	
	
	@SubscribeEvent
	public static void onRegisterSoundEvents(RegistryEvent.Register<SoundEvent> e) {
		IForgeRegistry<SoundEvent> registry = e.getRegistry();
		
		registry.register(LUTE);
	}
				
	public static SoundEvent createSoundEvent(String name) {
		ResourceLocation resLoc = new ResourceLocation(Rustic.MODID, name);
		return (new SoundEvent(resLoc)).setRegistryName(resLoc);
	}
			
}
