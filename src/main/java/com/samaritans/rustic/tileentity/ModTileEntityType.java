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
                Util.setup(TileEntityType.Builder.create(CabinetTileEntity::new, ModBlocks.acacia_cabinet, ModBlocks.birch_cabinet, ModBlocks.dark_oak_cabinet, ModBlocks.jungle_cabinet, ModBlocks.oak_cabinet, ModBlocks.spruce_cabinet, ModBlocks.ironwood_cabinet).build(null), "cabinet"),
                Util.setup(TileEntityType.Builder.create(PotTileEntity::new, ModBlocks.pot0, ModBlocks.pot1, ModBlocks.pot2, ModBlocks.pot3, ModBlocks.pot4, ModBlocks.pot5, ModBlocks.pot6, ModBlocks.pot7, ModBlocks.pot8, ModBlocks.pot9, ModBlocks.pot10, ModBlocks.pot11).build(null), "pot"),
                Util.setup(TileEntityType.Builder.create(CrushingTubTileEntity::new, ModBlocks.crushing_tub).build(null), "crushing_tub")
        );
    }
}
