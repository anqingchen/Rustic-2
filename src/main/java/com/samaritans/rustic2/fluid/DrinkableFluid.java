package com.samaritans.rustic2.fluid;

import com.samaritans.rustic2.item.FluidBottleItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class DrinkableFluid extends ForgeFlowingFluid.Source {
    public DrinkableFluid(Properties properties) {
        super(properties);
        FluidBottleItem.VALID_FLUIDS.add(this);
    }

    public abstract void onDrank(World world, PlayerEntity player, ItemStack stack, FluidStack fluid);
}
