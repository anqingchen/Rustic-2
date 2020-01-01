package com.samaritans.rustic2;

import com.samaritans.rustic2.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.FluidContainerColorer;

public class ModItemColors {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final FluidContainerColorer colorer = new FluidContainerColorer();

    public static void register() {
        minecraft.getItemColors().register((c, p) -> p > 0 ? -1 : colorer.getColor(c, p), ModItems.FLUID_BOTTLE);
    }
}
