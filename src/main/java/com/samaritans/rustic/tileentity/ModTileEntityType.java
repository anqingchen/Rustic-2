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
                Util.setup(TileEntityType.Builder.create(PotTileEntity::new, ModBlocks.POT_0, ModBlocks.POT_1, ModBlocks.POT_2, ModBlocks.POT_3, ModBlocks.POT_4, ModBlocks.POT_5, ModBlocks.POT_6, ModBlocks.POT_7, ModBlocks.POT_8, ModBlocks.POT_9, ModBlocks.POT_10, ModBlocks.POT_11).build(null), "pot"),
                Util.setup(TileEntityType.Builder.create(CrushingTubTileEntity::new, ModBlocks.CRUSHING_TUB).build(null), "crushing_tub")
        );
    }
}
