package com.samaritans.rustic.item;

import com.samaritans.rustic.Rustic;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, Rustic.MODID);

    public static final RegistryObject<Item> FLUID_BOTTLE = ITEMS.register("fluid_bottle", () -> new FluidBottleItem(new Item.Properties().maxStackSize(64)));
    public static final RegistryObject<Item> GRAPES = ITEMS.register("grapes", () -> new Item(new Item.Properties().food(new Food.Builder().hunger(3).saturation(1).build())));
}
