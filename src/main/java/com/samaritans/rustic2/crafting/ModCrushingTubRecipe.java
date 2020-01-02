package com.samaritans.rustic2.crafting;

import com.samaritans.rustic2.Rustic;
import com.samaritans.rustic2.fluid.ModFluids;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Rustic.MODID)
public class ModCrushingTubRecipe {
    @SubscribeEvent
    public static void registerCrushingTubRecipes(RegisterRecipesEvent event) {
        event.crushingTub().accept(new CrushingTubRecipe(new ResourceLocation(Rustic.MODID, "water"), Ingredient.fromItems(Items.OAK_SAPLING), new FluidStack(Fluids.WATER, 250)));
        event.crushingTub().accept(new CrushingTubRecipe(new ResourceLocation(Rustic.MODID, "lava"), Ingredient.fromItems(Items.ACACIA_SAPLING), new FluidStack(Fluids.LAVA, 250)));
        event.crushingTub().accept(new CrushingTubRecipe(new ResourceLocation(Rustic.MODID, "olive_oil"), Ingredient.fromItems(Items.BIRCH_SAPLING), new FluidStack(ModFluids.OLIVE_OIL_STILL, 250)));
    }
}
