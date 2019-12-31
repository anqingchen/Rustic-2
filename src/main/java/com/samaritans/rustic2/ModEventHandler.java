package com.samaritans.rustic2;

import com.samaritans.rustic2.client.model.FluidBottleModel;
import com.samaritans.rustic2.item.FluidBottleItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Map;

public class ModEventHandler {

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        for (Map.Entry<ResourceLocation, IBakedModel> e : event.getModelRegistry().entrySet()) {
            if (e.getValue() instanceof FluidBottleModel.BakedFluidBottle) {
                ResourceLocation stripVariant = new ResourceLocation(e.getKey().getNamespace(), e.getKey().getPath());
                ModelResourceLocation itemPath = new ModelResourceLocation(stripVariant, "inventory");
                event.getModelRegistry().put(itemPath, e.getValue());
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

            if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
                BlockPos pos2 = ((BlockRayTraceResult)raytraceresult).getPos();
                if (player.canPlayerEdit(pos2, event.getFace(), stack) && player.canPlayerEdit(pos2.offset(((BlockRayTraceResult) raytraceresult).getFace()), ((BlockRayTraceResult) raytraceresult).getFace(), stack)) {
                    BlockState state = world.getBlockState(pos2);
                    if (state.getBlock() instanceof FlowingFluidBlock) {
                        FlowingFluidBlock fluidblock = ((FlowingFluidBlock) state.getBlock());
                        if (FluidBottleItem.VALID_FLUIDS.contains(fluidblock.getFluid())) {
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
