package com.samaritans.rustic.block;

import com.samaritans.rustic.tileentity.ModTileEntityType;
import com.samaritans.rustic.tileentity.PotTileEntity;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

public class PotBlock extends ContainerBlock implements IWaterLoggable {
    protected static final VoxelShape SHAPE = VoxelShapes.create(0.125D, 0.0D, 0.125D, 0.875D, 1D, 0.875D);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public PotBlock(Properties builder) {
        super(builder);
        setDefaultState(this.getStateContainer().getBaseState().with(WATERLOGGED, false));
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if (worldIn.getTileEntity(pos) instanceof PotTileEntity) {
            PotTileEntity tileEntity = (PotTileEntity) worldIn.getTileEntity(pos);
            if (!worldIn.isRemote) {
                if (!tileEntity.isFluidEmpty()) {
                    ItemStack toDrop = new ItemStack(state.getBlock());
                    CompoundNBT tag = new CompoundNBT();
                    if (!tileEntity.isFluidEmpty()) {
                        tileEntity.getFluidHandler().writeToNBT(tag);
                    }
                    if (!tag.isEmpty()) {
                        toDrop.setTagInfo("BlockEntityTag", tag);
                    }
                    if (tileEntity.hasCustomName()) {
                        toDrop.setDisplayName(tileEntity.getCustomName());
                    }
                    ItemEntity itementity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), toDrop);
                    itementity.setDefaultPickupDelay();
                    worldIn.addEntity(itementity);
                } else {
                    InventoryHelper.dropItems(worldIn, pos, tileEntity.getPotContents());
                }
            }
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        PotTileEntity tile = (PotTileEntity) worldIn.getTileEntity(pos);
        ItemStack stack = player.getHeldItem(handIn);
        if (tile != null) {
            if ((tile.isItemEmpty() && tile.getFluidHandler().getCapacity() > 0 && FluidUtil.getFluidContained(stack).isPresent() && !FluidUtil.getFluidContained(stack).orElseThrow(RuntimeException::new).isEmpty()) || tile.getFluidHandler().getFluidAmount() > 0) {
                return tile.activate(state, worldIn, pos, player, handIn, hit);
            } else if (!worldIn.isRemote) {
                INamedContainerProvider inamedcontainerprovider = this.getContainer(state, worldIn, pos);
                if (inamedcontainerprovider != null) {
                    player.openContainer(inamedcontainerprovider);
                    // todo: open stats
                }
            }
        }
        return true;
    }

    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
        return this.getDefaultState().with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
    }

    @Override
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (worldIn.getTileEntity(pos) instanceof PotTileEntity) {
            PotTileEntity tile = (PotTileEntity) worldIn.getTileEntity(pos);
            if (stack.hasTag()) {
                FluidTank tank = tile.getFluidHandler();
                CompoundNBT nbt = (CompoundNBT) stack.getTag().get("BlockEntityTag");
                tank.readFromNBT(nbt);
                tile.markDirty();
            }
        }
        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof PotTileEntity) {
                ((PotTileEntity) tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(WATERLOGGED);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return ModTileEntityType.POT.create();
    }

    public float getInnerRadius(int y) {
        int modelStyle = getModelStyle();
        if (modelStyle == 0) {
            if (y >= 10) return 0.125f;
            if (y >= 2) return 0.25f;
        } else if (modelStyle == 1) {
            if (y >= 12) return 0.125f;
            if (y >= 6) return 0.25f;
            if (y >= 3) return 0.1875f;
            if (y >= 2) return 0.125f;
        }
        return 0f;
    }

    private int getModelStyle() {
        if (this == ModBlocks.POT_0 || this == ModBlocks.POT_1 || this == ModBlocks.POT_2 || this == ModBlocks.POT_3 || this == ModBlocks.POT_4 || this == ModBlocks.POT_5 || this == ModBlocks.POT_6 || this == ModBlocks.POT_7) {
            return 0;
        } else return 1;
    }
}
