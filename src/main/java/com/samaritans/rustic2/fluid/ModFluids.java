package com.samaritans.rustic2.fluid;

import com.samaritans.rustic2.Rustic2;
import com.samaritans.rustic2.Util;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Rustic2.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Rustic2.MODID)
public class ModFluids {
    public static final FlowingFluid OLIVE_OIL_STILL = null;
    public static final FlowingFluid OLIVE_OIL_FLOW = null;

    public static final Item OLIVE_OIL_BUCKET = null;

    public static final FlowingFluidBlock OLIVE_OIL = null;

    @SubscribeEvent
    public static void onRegisterFluids(RegistryEvent.Register<Fluid> event) {
        final ResourceLocation STILL = new ResourceLocation("minecraft", "block/water_still");
        final ResourceLocation FLOW = new ResourceLocation("minecraft", "block/water_flow");
        final ResourceLocation OVERLAY = new ResourceLocation("minecraft", "block/water_overlay");

        final ForgeFlowingFluid.Properties olive_oil_properties = new ForgeFlowingFluid.Properties(() -> ModFluids.OLIVE_OIL_STILL, () -> ModFluids.OLIVE_OIL_FLOW,
                FluidAttributes.builder(STILL, FLOW).density(920).viscosity(2000).overlay(OVERLAY).color(0xFF86904D))
                .bucket(() -> ModFluids.OLIVE_OIL_BUCKET).block(() -> ModFluids.OLIVE_OIL);


        event.getRegistry().registerAll(
                // Olive Oil
                Util.setup(new DrinkableFluid(olive_oil_properties) {
                    @Override
                    public void onDrank(World world, PlayerEntity player, ItemStack stack, FluidStack fluid) {

                    }
                }, "olive_oil_still"),
                Util.setup(new ForgeFlowingFluid.Flowing(olive_oil_properties), "olive_oil_flow")
        );
    }

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                Util.setup(new FlowingFluidBlock(() -> ModFluids.OLIVE_OIL_STILL, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()), "olive_oil")
        );
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                Util.setup(new BucketItem(() -> ModFluids.OLIVE_OIL_STILL, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(Rustic2.TAB)), "olive_oil_bucket")
        );
    }
}
