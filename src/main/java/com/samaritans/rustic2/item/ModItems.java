package com.samaritans.rustic2.item;

import com.samaritans.rustic2.Rustic;
import com.samaritans.rustic2.Util;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, Rustic.MODID);

    public static final RegistryObject<Item> FLUID_BOTTLE = ITEMS.register("fluid_bottle", () -> new FluidBottleItem(new Item.Properties().maxStackSize(64)));
    public static final RegistryObject<Item> GRAPES = ITEMS.register("grapes", () -> new Item(new Item.Properties().food(new Food.Builder().hunger(3).saturation(1).build())));
}
