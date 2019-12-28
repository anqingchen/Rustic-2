package com.samaritans.rustic2.tileentity;

import com.samaritans.rustic2.Rustic2;
import com.samaritans.rustic2.Util;
import com.samaritans.rustic2.block.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Rustic2.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Rustic2.MODID)
public class ModTileEntityType {
    public static final TileEntityType<CabinetTileEntity> CABINET = null;
    public static final TileEntityType<PotTileEntity> POT = null;

    @SubscribeEvent
    public static void onRegisterBlockItems(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                Util.setup(TileEntityType.Builder.create(CabinetTileEntity::new, ModBlocks.acacia_cabinet, ModBlocks.birch_cabinet, ModBlocks.dark_oak_cabinet, ModBlocks.jungle_cabinet, ModBlocks.oak_cabinet, ModBlocks.spruce_cabinet).build(null), "cabinet"),
                Util.setup(TileEntityType.Builder.create(PotTileEntity::new, ModBlocks.pot).build(null), "pot")
        );
    }
}
