package com.samaritans.rustic.tileentity;

import com.samaritans.rustic.Rustic;
import com.samaritans.rustic.block.CabinetBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;

@OnlyIn(value = Dist.CLIENT, _interface = IChestLid.class)
public class CabinetTileEntity extends LockableLootTileEntity implements ITickableTileEntity, IChestLid {
    protected float lidAngle;
    protected float prevLidAngle;
    protected int numPlayersUsing;
    private NonNullList<ItemStack> cabinetContents = NonNullList.withSize(27, ItemStack.EMPTY);
    private int ticksSinceSync;
    private LazyOptional<IItemHandlerModifiable> cabinetHandler;

    public CabinetTileEntity() {
        super(ModTileEntityType.CABINET);
    }

    public static int calculatePlayersUsingSync(World world, LockableTileEntity tileEntity, int ticksSinceSync, int x, int y, int z, int numPlayerUsing) {
        if (!world.isRemote && numPlayerUsing != 0 && ticksSinceSync % 200 == 0) {
            numPlayerUsing = calculatePlayersUsing(world, tileEntity, x, y, z);
        }
        return numPlayerUsing;
    }

    public static int calculatePlayersUsing(World world, LockableTileEntity tileEntity, int x, int y, int z) {
        int i = 0;
        for (PlayerEntity playerentity : world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(((float) x - 5.0F), ((float) y - 5.0F), ((float) z - 5.0F), ((float) (x + 1) + 5.0F), ((float) (y + 1) + 5.0F), ((float) (z + 1) + 5.0F)))) {
            if (playerentity.openContainer instanceof ChestContainer) {
                IInventory iinventory = ((ChestContainer) playerentity.openContainer).getLowerChestInventory();
                if (iinventory == tileEntity || iinventory instanceof DoubleSidedInventory && ((DoubleSidedInventory) iinventory).isPartOfLargeChest(tileEntity)) {
                    ++i;
                }
            }
        }
        return i;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.cabinetContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(compound)) {
            ItemStackHelper.loadAllItems(compound, this.cabinetContents);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, this.cabinetContents);
        }
        return compound;
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }
            ++this.numPlayersUsing;
            this.onOpenOrClose();
        }
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.numPlayersUsing;
            this.onOpenOrClose();
        }
    }

    private void onOpenOrClose() {
        Block block = this.getBlockState().getBlock();
        if (block instanceof CabinetBlock) {
            this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, block);
        }
    }

    @Override
    public void tick() {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        ++this.ticksSinceSync;
        this.numPlayersUsing = calculatePlayersUsingSync(this.world, this, this.ticksSinceSync, i, j, k, this.numPlayersUsing);
        this.prevLidAngle = this.lidAngle;
        float f = 0.1F;
        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
            this.playSound(SoundEvents.BLOCK_CHEST_OPEN);
        }
        if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
            float f1 = this.lidAngle;
            if (this.numPlayersUsing > 0) {
                this.lidAngle += 0.1F;
            } else {
                this.lidAngle -= 0.1F;
            }
            if (this.lidAngle > 1.0F) {
                this.lidAngle = 1.0F;
            }
            if (this.lidAngle < 0.5F && f1 >= 0.5F) {
                this.playSound(SoundEvents.BLOCK_CHEST_CLOSE);
            }
            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    private void playSound(SoundEvent soundIn) {
        CabinetBlock.CabinetType cabinetType = this.getBlockState().get(CabinetBlock.TYPE);
        if (cabinetType != CabinetBlock.CabinetType.BOTTOM) {
            double d0 = (double) this.pos.getX() + 0.5D;
            double d1 = (double) this.pos.getY() + 0.5D;
            double d2 = (double) this.pos.getZ() + 0.5D;
            if (cabinetType == CabinetBlock.CabinetType.TOP) {
                Direction direction = CabinetBlock.getDirectionToAttached(this.getBlockState());
                d0 += (double) direction.getXOffset() * 0.5D;
                d2 += (double) direction.getZOffset() * 0.5D;
            }
            this.world.playSound(null, d0, d1, d2, soundIn, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        if (this.cabinetHandler != null) {
            this.cabinetHandler.invalidate();
            this.cabinetHandler = null;
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.cabinetHandler == null) {
                this.cabinetHandler = LazyOptional.of(this::createHandler);
            }
            return this.cabinetHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private IItemHandlerModifiable createHandler() {
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof CabinetBlock)) {
            return new InvWrapper(this);
        }
        CabinetBlock.CabinetType type = state.get(CabinetBlock.TYPE);
        if (type != CabinetBlock.CabinetType.SINGLE) {
            BlockPos opos = this.getPos().offset(CabinetBlock.getDirectionToAttached(state));
            BlockState ostate = this.getWorld().getBlockState(opos);
            if (state.getBlock() == ostate.getBlock()) {
                CabinetBlock.CabinetType otype = ostate.get(CabinetBlock.TYPE);
                if (otype != CabinetBlock.CabinetType.SINGLE && type != otype && state.get(CabinetBlock.FACING) == ostate.get(CabinetBlock.FACING)) {
                    TileEntity ote = this.getWorld().getTileEntity(opos);
                    if (ote instanceof CabinetTileEntity) {
                        IInventory top = type == CabinetBlock.CabinetType.TOP ? this : (IInventory) ote;
                        IInventory bottom = type == CabinetBlock.CabinetType.BOTTOM ? (IInventory) ote : this;
                        return new CombinedInvWrapper(new InvWrapper(top), new InvWrapper(bottom));
                    }
                }
            }
        }
        return new InvWrapper(this);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.cabinetContents;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.cabinetContents = itemsIn;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent(Rustic.MODID + ".container.cabinet");
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
        for (ItemStack itemstack : this.cabinetContents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void remove() {
        super.remove();
        if (cabinetHandler != null)
            cabinetHandler.invalidate();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getLidAngle(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevLidAngle, this.lidAngle);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (getTileEntity().getBlockState().get(CabinetBlock.TYPE) == CabinetBlock.CabinetType.BOTTOM) {
            return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1D, pos.getY() + 2D, pos.getZ() + 1D);
        }
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1D, pos.getY() + 1D, pos.getZ() + 1D);
    }
}
