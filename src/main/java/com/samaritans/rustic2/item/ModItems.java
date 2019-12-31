package com.samaritans.rustic2.item;

import com.samaritans.rustic2.Rustic2;
import com.samaritans.rustic2.Util;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Rustic2.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Rustic2.MODID)
public class ModItems {
    public static final Item FLUID_BOTTLE = null;

    @SubscribeEvent
    public static void onRegisterBlockItems(final RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                Util.setup(new FluidBottleItem(new Item.Properties().maxStackSize(1), 1000), "fluid_bottle")
        );
    }
}
