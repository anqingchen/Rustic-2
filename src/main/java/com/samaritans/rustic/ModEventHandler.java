package com.samaritans.rustic;

import com.samaritans.rustic.block.ModBlocks;
import com.samaritans.rustic.item.FluidBottleItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class ModEventHandler {
	
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
    
    private static final ResourceLocation YEW_WOOD_TAG = new ResourceLocation(Rustic.MODID, "yew_wood");
    
    @SubscribeEvent
    public static void onNoteBlockPlay(NoteBlockEvent.Play event) {
    	IWorld world = event.getWorld();
    	BlockPos pos = event.getPos();
    	BlockState downState = world.getBlockState(pos.down());
    	if (downState.getBlock().getTags().contains(YEW_WOOD_TAG)) {
    		event.setCanceled(true);
    		int note = event.getVanillaNoteId();
    		float pitch = (float) Math.pow(2d, (note - 12d) / 12d);
    		world.playSound(null, pos, ModSounds.LUTE, SoundCategory.RECORDS, 3.0f, pitch);
    		//world.addParticle(ParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, note / 24d, 0, 0);
    		if (world instanceof ServerWorld)
    			((ServerWorld) world).spawnParticle(ParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 0, note / 24d, 0, 0, 1);
    	}
    }
    
}
