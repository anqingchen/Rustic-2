package com.samaritans.rustic.tileentity;

import com.samaritans.rustic.Rustic;
import com.samaritans.rustic.Util;
import com.samaritans.rustic.block.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Rustic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Rustic.MODID)
public class ModTileEntityType {
    public static final TileEntityType<CabinetTileEntity> CABINET = null;
    public static final TileEntityType<PotTileEntity> POT = null;
    public static final TileEntityType<CrushingTubTileEntity> CRUSHING_TUB = null;

    @SubscribeEvent
    public static void onRegisterBlockItems(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                Util.setup(TileEntityType.Builder.create(CabinetTileEntity::new, ModBlocks.ACACIA_CABINET, ModBlocks.BIRCH_CABINET, ModBlocks.DARK_OAK_CABINET, ModBlocks.JUNGLE_CABINET, ModBlocks.OAK_CABINET, ModBlocks.SPRUCE_CABINET, ModBlocks.IRONWOOD_CABINET).build(null), "cabinet"),
                Util.setup(TileEntityType.Builder.create(PotTileEntity::new, ModBlocks.POT0, ModBlocks.POT1, ModBlocks.POT2, ModBlocks.POT3, ModBlocks.POT4, ModBlocks.POT5, ModBlocks.POT6, ModBlocks.POT7, ModBlocks.POT8, ModBlocks.POT9, ModBlocks.POT10, ModBlocks.POT11).build(null), "pot"),
                Util.setup(TileEntityType.Builder.create(CrushingTubTileEntity::new, ModBlocks.CRUSHING_TUB).build(null), "crushing_tub")
        );
    }
}
