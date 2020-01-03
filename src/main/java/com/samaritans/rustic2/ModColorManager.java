package com.samaritans.rustic2;

import com.samaritans.rustic2.block.ModBlocks;
import com.samaritans.rustic2.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.client.FluidContainerColorer;

public class ModColorManager {
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static void register() {
        registerItemColors();
        registerBlockColors();
    }

    private static void registerItemColors() {
        minecraft.getItemColors().register(new FluidContainerColorer(), ModItems.FLUID_BOTTLE.get());
    }

    private static void registerBlockColors() {
        minecraft.getBlockColors().register(((blockState, iEnviromentBlockReader, blockPos, i) ->
                blockPos != null && iEnviromentBlockReader != null && i == 0 ? BiomeColors.getFoliageColor(iEnviromentBlockReader, blockPos) : FoliageColors.getDefault()), ModBlocks.lattice);
    }
}
