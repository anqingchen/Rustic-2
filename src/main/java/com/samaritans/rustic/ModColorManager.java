package com.samaritans.rustic;

import com.samaritans.rustic.block.ModBlocks;
import com.samaritans.rustic.item.ModItems;
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
        minecraft.getItemColors().register(new FluidContainerColorer(), ModItems.FLUID_BOTTLE);
    }

    private static void registerBlockColors() {
        minecraft.getBlockColors().register(((blockState, iEnviromentBlockReader, blockPos, i) ->
                blockPos != null && iEnviromentBlockReader != null && i == 0 ? BiomeColors.getFoliageColor(iEnviromentBlockReader, blockPos) : FoliageColors.getDefault()), ModBlocks.LATTICE);
    }
}
