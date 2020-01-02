package com.samaritans.rustic2;

import com.samaritans.rustic2.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.FluidContainerColorer;

public class ModItemColors {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final FluidContainerColorer colorer = new FluidContainerColorer();

    public static void register() {
        minecraft.getItemColors().register(colorer, ModItems.FLUID_BOTTLE);
    }
}
