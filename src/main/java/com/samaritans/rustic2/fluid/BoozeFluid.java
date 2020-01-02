package com.samaritans.rustic2.fluid;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

public abstract class BoozeFluid extends DrinkableFluid {
    public static final String QUALITY_NBT_KEY = "quality";
    public static final String CONCENTRATION_NBT_KEY = "concentration";

    private final float inebriationChance;

    public BoozeFluid(Properties properties) {
        this(properties, 0.5F);
    }

    public BoozeFluid(Properties properties, float chance) {
        super(properties);
        this.inebriationChance = chance;
    }

    @Override
    public void onDrank(World world, PlayerEntity player, ItemStack stack, FluidStack fluid) {
        float quality = getQuality(fluid);
        float concentration = getConcentration(fluid);

        inebriate(world, player, quality, concentration);
        affectPlayer(world, player, quality, concentration);
    }

    public abstract void affectPlayer(World world, PlayerEntity player, float quality, float concentration);

    public float getQuality(FluidStack fluid) {
        float quality = 0F;
        if (fluid.hasTag() && fluid.getTag().contains(QUALITY_NBT_KEY, Constants.NBT.TAG_FLOAT)) {
            quality = fluid.getTag().getFloat(QUALITY_NBT_KEY);
        }
        return Math.max(Math.min(quality, 1), 0);
    }

    public float getConcentration(FluidStack fluid) {
        float concentration = 0F;
        if (fluid.hasTag() && fluid.getTag().contains(CONCENTRATION_NBT_KEY, Constants.NBT.TAG_FLOAT)) {
            concentration = fluid.getTag().getFloat(CONCENTRATION_NBT_KEY);
        }
        return Math.max(Math.min(concentration, 1), 0);
    }

    protected void inebriate(World world, PlayerEntity player, float quality, float concentration) {
        int duration = (int) (12000 * (Math.max(1 - Math.abs(quality - 0.75F), 0F)));
        float inebriationChanceMod = Math.max(Math.min(1 - Math.abs(0.67F * (quality - 0.75F)), 1), 0);
//        Potion tipsyEffect = player.getActivePotionEffect(PotionsRustic.TIPSY);
//        if (world.rand.nextFloat() < this.inebriationChance * inebriationChanceMod) {
//            if (tipsyEffect == null) {
//                player.addPotionEffect(new PotionEffect(PotionsRustic.TIPSY, duration, 0, false, false));
//            } else if (tipsyEffect.getAmplifier() < 3) {
//                player.addPotionEffect(new PotionEffect(PotionsRustic.TIPSY, duration, tipsyEffect.getAmplifier() + 1, false, false));
//            }
//        }
    }
}
