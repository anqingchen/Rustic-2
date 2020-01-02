package com.samaritans.rustic2.entity;

import com.samaritans.rustic2.Rustic;
import com.samaritans.rustic2.block.ChairBlock;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Rustic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Rustic.MODID)
public class ModEntities {
    public static final EntityType<ChairBlock.ChairEntity> chair = null;

    @SubscribeEvent
    public static void onEntitiesRegistry(final RegistryEvent.Register<EntityType<?>> entityRegistryEvent) {
        entityRegistryEvent.getRegistry().registerAll(
                EntityType.Builder.create(ChairBlock.ChairEntity::new, EntityClassification.MISC).size(0, 0)
                        .setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(40)
                        .build("chair").setRegistryName(Rustic.MODID, "chair")
        );
    }
}
