package com.samaritans.rustic2.client.model;

import com.samaritans.rustic2.Rustic2;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Rustic2.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModModels {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(FluidBottleModel.FluidBottleLoader.INSTANCE);
    }
}
