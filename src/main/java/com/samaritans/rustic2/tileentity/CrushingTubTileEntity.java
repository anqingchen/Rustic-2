package com.samaritans.rustic2.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrushingTubTileEntity extends TileEntity {
    public static int capacity = FluidAttributes.BUCKET_VOLUME * 8;

    public LazyOptional<ItemStackHandler> itemStackHandler = LazyOptional.of(this::createItemHandler);
    public LazyOptional<FluidTank> fluidHandler = LazyOptional.of(this::createFluidHandler);

    public CrushingTubTileEntity() {
        super(ModTileEntityType.CRUSHING_TUB);
    }

    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        ItemStack heldItem = player.getHeldItem(hand);
        IFluidHandler fluidHandler = this.getFluidHandler();
        IItemHandlerModifiable itemStackHandler = this.getItemHandler();
        if (heldItem != ItemStack.EMPTY) {
            if ((FluidUtil.getFluidHandler(heldItem) != null || heldItem.getItem() instanceof BucketItem) && FluidUtil.getFluidContained(heldItem).isPresent()) {
                boolean didFill = FluidUtil.interactWithFluidHandler(player, hand, fluidHandler);
                if (didFill) {
                    sendUpdates();
                    return true;
                }
            } else {
                player.setHeldItem(hand, itemStackHandler.insertItem(0, heldItem, false));
                sendUpdates();
                return true;
            }
        } else if (player.isSneaking() && fluidHandler.getFluidInTank(0).getAmount() > 0) {
            FluidStack drained = fluidHandler.drain(fluidHandler.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE);
            SoundEvent soundevent = drained.getFluid().getAttributes().getEmptySound(drained);
            world.playSound(null, blockPos, soundevent, SoundCategory.BLOCKS, 1F, 1F);
            sendUpdates();
            return true;
        }
        if (itemStackHandler.getStackInSlot(0) != ItemStack.EMPTY && !world.isRemote) {
            InventoryHelper.spawnItemStack(world, player.posX, player.posY, player.posZ, itemStackHandler.getStackInSlot(0));
            itemStackHandler.setStackInSlot(0, ItemStack.EMPTY);
            sendUpdates();
            return true;
        }
        return false;
    }

    public void crush(LivingEntity entity) {
        FluidTank tank = getFluidHandler();
        ItemStackHandler itemStackHandler = getItemHandler();
        if (!itemStackHandler.getStackInSlot(0).isEmpty()) {
            ItemStack stack = itemStackHandler.getStackInSlot(0);
            if (stack.getItem() == Items.OAK_SAPLING) {
                FluidStack output = new FluidStack(Fluids.WATER, 250);
                if (tank.getFluidAmount() <= tank.getCapacity() - output.getAmount()) {
                    tank.fill(output, IFluidHandler.FluidAction.EXECUTE);
                    itemStackHandler.extractItem(0, 1, false);
//                    ItemStack by = recipe.getByproduct().copy();
//                    if (!by.isEmpty()) {
//                        Block.spawnAsEntity(world, pos, by);
//                    }
                    this.getWorld().playSound(null, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, SoundEvents.BLOCK_SLIME_BLOCK_FALL, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
                    this.sendUpdates();
                }
            }
        }
    }

    private void sendUpdates() {
        if (this.getWorld() != null) {
            this.getWorld().addBlockEvent(this.pos, getBlockState().getBlock(), 1, 0);
            this.getWorld().notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
            this.getWorld().notifyNeighborsOfStateChange(this.pos, getBlockState().getBlock());
            this.markDirty();
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1 && getWorld() != null) {
            this.getWorld().notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
            this.getWorld().notifyNeighborsOfStateChange(this.pos, getBlockState().getBlock());
            this.markDirty();
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        getFluidHandler().readFromNBT(tag);
        if (tag.contains("items")) {
            getItemHandler().deserializeNBT((CompoundNBT) tag.get("items"));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag = super.write(tag);
        getFluidHandler().writeToNBT(tag);
        tag.put("items", getItemHandler().serializeNBT());
        return tag;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), 3, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!this.removed) {
            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return this.itemStackHandler.cast();
            }
            if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
                return this.fluidHandler.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    public ItemStackHandler getItemHandler() {
        return itemStackHandler.orElseThrow(RuntimeException::new);
    }

    public FluidTank getFluidHandler() {
        return fluidHandler.orElseThrow(RuntimeException::new);
    }

    private ItemStackHandler createItemHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                getWorld().addBlockEvent(getPos(), getBlockState().getBlock(), 1, 0);
                getWorld().notifyNeighborsOfStateChange(getPos(), getBlockState().getBlock());
                markDirty();
            }
        };
    }

    private FluidTank createFluidHandler() {
        return new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                markDirty();
            }
        };
    }
}
