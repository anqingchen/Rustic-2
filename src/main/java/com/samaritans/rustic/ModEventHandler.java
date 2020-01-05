package com.samaritans.rustic;

import com.google.common.collect.ImmutableMap;
import com.samaritans.rustic.block.ModBlocks;
import com.samaritans.rustic.item.FluidBottleItem;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Map;

public class ModEventHandler {
    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void onAxeStrip(PlayerInteractEvent.RightClickBlock event) {
        final Map<Block, Block> MOD_BLOCK_STRIPPING_MAP = (new ImmutableMap.Builder<Block, Block>()).put(ModBlocks.IRONWOOD_LOG, ModBlocks.STRIPPED_IRONWOOD_LOG).put(ModBlocks.IRONWOOD_WOOD, ModBlocks.STRIPPED_IRONWOOD_WOOD).build();
        if (event.getItemStack().getItem() instanceof AxeItem) {
            BlockPos blockpos = event.getPos();
            World world = event.getWorld();
            BlockState blockstate = world.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            Block stripped_block = MOD_BLOCK_STRIPPING_MAP.get(block);
            if (stripped_block != null) {
                PlayerEntity playerentity = event.getPlayer();
                world.playSound(playerentity, blockpos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!world.isRemote) {
                    world.setBlockState(blockpos, stripped_block.getDefaultState().with(RotatedPillarBlock.AXIS, blockstate.get(RotatedPillarBlock.AXIS)), 11);
                    if (playerentity != null) {
                        event.getItemStack().damageItem(1, playerentity, (p_220040_1_) -> {
                            p_220040_1_.sendBreakAnimation(event.getHand());
                        });
                    }
                }
                event.setCancellationResult(ActionResultType.SUCCESS);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerUseGlassBottle(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().getItem().equals(Items.GLASS_BOTTLE)) {
            PlayerEntity player = event.getPlayer();
            BlockPos pos = event.getPos();
            ItemStack stack = event.getItemStack();
            World world = event.getWorld();
            RayTraceResult raytraceresult = Util.rayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);

            if (raytraceresult.getType() == RayTraceResult.Type.MISS) return;

            if (raytraceresult.getType() == RayTraceResult.Type.BLOCK && event.getFace() != null) {
                BlockPos pos2 = ((BlockRayTraceResult)raytraceresult).getPos();
                if (player.canPlayerEdit(pos2, event.getFace(), stack) && player.canPlayerEdit(pos2.offset(((BlockRayTraceResult) raytraceresult).getFace()), ((BlockRayTraceResult) raytraceresult).getFace(), stack)) {
                    BlockState state = world.getBlockState(pos2);
                    if (state.getBlock() instanceof FlowingFluidBlock) {
                        FlowingFluidBlock fluidblock = ((FlowingFluidBlock) state.getBlock());
                        if (FluidBottleItem.VALID_FLUIDS.contains(fluidblock.getFluid())) {
                            fluidblock.pickupFluid(world, pos2, world.getBlockState(pos2));
                            world.setBlockState(pos2, Blocks.AIR.getDefaultState());
//                            player.addStat(.getObjectUseStats(stack.getItem()));
                            player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                            stack.shrink(1);
                            ItemStack bottlestack = FluidBottleItem.getFluidBottle(fluidblock.getFluid());
                            if (!player.inventory.addItemStackToInventory(bottlestack)) {
                                player.dropItem(bottlestack, false);
                            }
                        }
                    }
                    else if (state.getBlock().hasTileEntity(state) && world.getTileEntity(pos) != null && world.getTileEntity(pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, event.getFace()).isPresent()) {
//                        if (state.getBlock() == ModBlocks.BREWING_BARREL && event.getFace() != EnumFacing.DOWN) {
//                            return;
//                        }
                        IFluidHandler tank = world.getTileEntity(pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, event.getFace()).orElseThrow(RuntimeException::new);
                        FluidStack fluidStack = tank.drain(1000, IFluidHandler.FluidAction.SIMULATE);
                        if (fluidStack.getFluid() != null && !fluidStack.isEmpty()) {
                            if (FluidBottleItem.VALID_FLUIDS.contains(fluidStack.getFluid()) && fluidStack.getAmount() >= 1000) {
                                FluidStack fill = tank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
//                                player.addStat(StatList.getObjectUseStats(stack.getItem()));
                                player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                                stack.shrink(1);
                                ItemStack bottlestack = FluidBottleItem.getFluidBottle(fill.getFluid());
                                if (!player.inventory.addItemStackToInventory(bottlestack)) {
                                    player.dropItem(bottlestack, false);
                                }
                                event.setCanceled(true);
                            } else if (fluidStack.getFluid() == Fluids.WATER) {
                                tank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
//                                player.addStat(StatList.getObjectUseStats(stack.getItem()));
                                player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                                stack.shrink(1);
                                ItemStack bottlestack = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
                                if (!player.inventory.addItemStackToInventory(bottlestack)) {
                                    player.dropItem(bottlestack, false);
                                }
                                event.setCanceled(true);
                                event.setUseBlock(Event.Result.DENY);
                            }
                        }
                    }
                }
            }
        }
    }
}
