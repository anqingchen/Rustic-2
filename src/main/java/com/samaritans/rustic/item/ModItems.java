package com.samaritans.rustic.item;

import com.samaritans.rustic.Rustic;
import com.samaritans.rustic.Util;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Rustic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Rustic.MODID)
public class ModItems {
    public static final Item FLUID_BOTTLE = null;
    public static final Item GRAPES = null;
    public static final Item SPELL_CATALYST = null;
    public static final Item LUTE = null;

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                Util.setup(new FluidBottleItem(new Item.Properties().maxStackSize(64).group(Rustic.TAB)), "fluid_bottle"),
                Util.setup(new Item(new Item.Properties().food(new Food.Builder().hunger(3).saturation(1).build()).group(Rustic.TAB)), "grapes"),
                Util.setup(new SpellCatalystItem(new Item.Properties().maxStackSize(64).group(Rustic.TAB)), "spell_catalyst"),
                Util.setup(new LuteItem(new Item.Properties().maxStackSize(1).group(Rustic.TAB)), "lute")
        );
    }
}
