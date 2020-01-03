package com.samaritans.rustic2.item;

import com.samaritans.rustic2.fluid.DrinkableFluid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;

public class FluidBottleItem extends ItemFluidContainer {
    public static Collection<Fluid> VALID_FLUIDS = new HashSet<>();
    private final ItemStack CONTAINER = new ItemStack(Items.GLASS_BOTTLE);
    public static final String FLUID_NBT_KEY = "Fluid";

    /**
     * @param properties Item properties
     */
    public FluidBottleItem(Properties properties) {
        super(properties, 1000);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return CONTAINER;
    }

    public FluidStack getFluid(ItemStack container) {
        if (container.hasTag() && container.getTag().contains(FLUID_NBT_KEY)) {
            return FluidStack.loadFluidStackFromNBT((CompoundNBT) container.getTag().get(FLUID_NBT_KEY));
        }
        return null;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        //System.out.println(playerIn.getHeldItem(handIn).getTagCompound());
        return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        if (getFluid(stack) != null && getFluid(stack).getFluid() != null && getFluid(stack).getFluid() instanceof DrinkableFluid) {
            return UseAction.DRINK;
        }
        return super.getUseAction(stack);
    }

    @Override
    public int getUseDuration(ItemStack p_77626_1_) {
        return 32;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        PlayerEntity entityplayer = entityLiving instanceof PlayerEntity ? (PlayerEntity) entityLiving : null;
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElseThrow(RuntimeException::new);

        FluidStack fluidstack = handler.getFluidInTank(0);
        Fluid fluid = fluidstack.getFluid();

        if (!worldIn.isRemote && fluid instanceof DrinkableFluid && entityplayer != null) {
            ((DrinkableFluid) fluid).onDrank(worldIn, entityplayer, stack, fluidstack);
        }

//        if (entityplayer != null) {
//            entityplayer.addStat(StatList.getObjectUseStats(this));
//        }

        if (entityplayer == null || !entityplayer.abilities.isCreativeMode) {
            stack.shrink(1);
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (entityplayer != null) {
                entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return stack;
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundNBT nbt) {
        return new FluidHandlerItemStackSimple.SwapEmpty(stack, new ItemStack(Items.GLASS_BOTTLE), capacity);
    }

    public static ItemStack getFluidBottle(Fluid fluid) {
        ItemStack bottlestack = new ItemStack(ModItems.FLUID_BOTTLE.get(), 1);
        CompoundNBT fluidTag = new FluidStack(fluid, 1000).writeToNBT(new CompoundNBT());
        CompoundNBT tag = new CompoundNBT();
        tag.put(FluidBottleItem.FLUID_NBT_KEY, fluidTag);
        bottlestack.setTag(tag);
        return bottlestack;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        FluidStack fluidStack = getFluid(stack);
        if (fluidStack != null && !fluidStack.isEmpty()) {
            String unloc = fluidStack.getTranslationKey();
            return "item." + unloc.substring(6, unloc.length() - 5) + "bottle";
        }
        return super.getTranslationKey(stack);
    }
}
