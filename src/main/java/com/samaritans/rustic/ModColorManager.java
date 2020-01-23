package com.samaritans.rustic;

import com.samaritans.rustic.alchemy.AlchemySpell;
import com.samaritans.rustic.alchemy.ISpellCatalystItem;
import com.samaritans.rustic.block.ModBlocks;
import com.samaritans.rustic.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
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
    	ItemColors itemColors = minecraft.getItemColors();
    	
    	itemColors.register(new FluidContainerColorer(), ModItems.FLUID_BOTTLE);
    	itemColors.register((stack, layer) -> {
    		AlchemySpell spell = ISpellCatalystItem.getAlchemySpell(stack);
    		return (spell == null || spell.isEmpty()) ? 16777215 : spell.getSpellEffect().getColor();
    	}, ModItems.SPELL_CATALYST);
    }

    private static void registerBlockColors() {
    	BlockColors blockColors = minecraft.getBlockColors();
    	
    	blockColors.register(((blockState, iEnviromentBlockReader, blockPos, i) ->
                blockPos != null && iEnviromentBlockReader != null && i == 0 ? BiomeColors.getFoliageColor(iEnviromentBlockReader, blockPos) : FoliageColors.getDefault()), ModBlocks.LATTICE);
    }
}
