package com.samaritans.rustic.tileentity;

import com.samaritans.rustic.Rustic;
import com.samaritans.rustic.block.PotBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class PotTileEntity extends LockableLootTileEntity {
    public static int capacity = FluidAttributes.BUCKET_VOLUME * 8;

    private NonNullList<ItemStack> potContents = NonNullList.withSize(27, ItemStack.EMPTY);
    private LazyOptional<IItemHandlerModifiable> potHandler = LazyOptional.of(this::createItemHandler);
    public LazyOptional<FluidTank> fluidHandler = LazyOptional.of(this::createFluidHandler);

    public PotTileEntity() {
        super(ModTileEntityType.POT);
    }

    public boolean activate(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!(state.getBlock() instanceof PotBlock)) return false;
        ItemStack heldItem = player.getHeldItem(handIn);

        if (!heldItem.isEmpty()) {
            if (FluidUtil.getFluidContained(heldItem).isPresent()) {
                FluidStack f = FluidUtil.getFluidContained(heldItem).orElseThrow(RuntimeException::new);
                if (!f.getFluid().getAttributes().isGaseous()) {
                    boolean didFill = FluidUtil.interactWithFluidHandler(player, handIn, this.getFluidHandler());
                    if (didFill) {
                        this.world.addBlockEvent(this.pos, getBlockState().getBlock(), 1, 0);
                        this.world.notifyBlockUpdate(pos, state, state, 3);
                        this.world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
                        this.markDirty();
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.potContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(compound)) {
            ItemStackHelper.loadAllItems(compound, this.potContents);
        }
        this.getFluidHandler().readFromNBT(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, this.potContents);
        }
        this.getFluidHandler().writeToNBT(compound);
        return compound;
    }

    public boolean checkLootAndWrite(CompoundNBT p_184282_1_) {
        if (this.lootTable == null) {
            return false;
        } else {
            p_184282_1_.putString("LootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                p_184282_1_.putLong("LootTableSeed", this.lootTableSeed);
            }

            return true;
        }
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
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        if (this.potHandler != null) {
            this.potHandler.invalidate();
            this.potHandler = null;
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, Direction side) {
        if (!this.removed) {
            if (isFluidEmpty() && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return this.potHandler.cast();
            }
            else if (isItemEmpty() && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
                return this.fluidHandler.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    private IItemHandlerModifiable createItemHandler() {
        return new InvWrapper(this);
    }

    public boolean isItemEmpty() {
        for (ItemStack stack : potContents) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public NonNullList<ItemStack> getPotContents() {
        return potContents;
    }

    public boolean isFluidEmpty() {
        return getFluidHandler().getFluid().isEmpty();
    }

    private FluidTank createFluidHandler() {
        return new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                markDirty();
            }
        };
    }

    public FluidTank getFluidHandler() {
        return fluidHandler.orElseThrow(RuntimeException::new);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.potContents;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.potContents = itemsIn;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent(Rustic.MODID + ".container.pot");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return ChestContainer.createGeneric9X3(id, player, this);
    }

    @Override
    public int getSizeInventory() {
        return 27;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.potContents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
