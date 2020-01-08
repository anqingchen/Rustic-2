package com.samaritans.rustic.world.feature;

import com.samaritans.rustic.Rustic;
import com.samaritans.rustic.Util;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Rustic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Rustic.MODID)
public class ModFeatures {
    public static final Feature<NoFeatureConfig> IRONWOOD_TREE = null;
    public static final Feature<NoFeatureConfig> YEW_TREE = null;

    @SubscribeEvent
    public static void onRegisterFeatures(final RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().registerAll(
                Util.setup(new IronwoodTreeFeature(NoFeatureConfig::deserialize, true), "ironwood_tree"),
                Util.setup(new YewTreeFeature(NoFeatureConfig::deserialize, true), "yew_tree")
        );
    }
}
