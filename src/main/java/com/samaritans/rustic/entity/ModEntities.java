package com.samaritans.rustic.entity;

import com.samaritans.rustic.Rustic;
import com.samaritans.rustic.block.ChairBlock;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Rustic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Rustic.MODID)
public class ModEntities {
    public static final EntityType<ChairBlock.ChairEntity> CHAIR = null;
    public static final EntityType<LuteSpellEntity> LUTE_SPELL = null;

    @SubscribeEvent
    public static void onEntitiesRegistry(final RegistryEvent.Register<EntityType<?>> entityRegistryEvent) {
    	entityRegistryEvent.getRegistry().registerAll(
    			EntityType.Builder.create(ChairBlock.ChairEntity::new, EntityClassification.MISC).size(0, 0)
    					.setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(40)
    					.build("chair").setRegistryName(Rustic.MODID, "chair"),
    			EntityType.Builder.<LuteSpellEntity>create(LuteSpellEntity::new, EntityClassification.MISC).size(0, 0)
    					.setShouldReceiveVelocityUpdates(true).setTrackingRange(96).setUpdateInterval(1).immuneToFire().disableSummoning()
    					.build("lute_spell").setRegistryName(Rustic.MODID, "lute_spell")
    	);
    }
}
