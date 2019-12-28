package com.samaritans.rustic2.tileentity;

import com.samaritans.rustic2.Rustic2;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public class PotTileEntity extends LockableLootTileEntity {
    private NonNullList<ItemStack> potContents = NonNullList.withSize(27, ItemStack.EMPTY);
    private LazyOptional<IItemHandlerModifiable> potHandler;

    public PotTileEntity() {
        super(ModTileEntityType.POT);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.potContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(compound)) {
            ItemStackHelper.loadAllItems(compound, this.potContents);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, this.potContents);
        }
        return compound;
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
        if (!this.removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.potHandler == null) {
                this.potHandler = LazyOptional.of(this::createHandler);
            }
            return this.potHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private IItemHandlerModifiable createHandler() {
        return new InvWrapper(this);
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
        return new TranslationTextComponent(Rustic2.MODID + ".container.pot");
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
        for(ItemStack itemstack : this.potContents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
