package com.samaritans.rustic.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class LatticeBlock extends Block implements IWaterLoggable {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty LEAVES = BooleanProperty.create("leaves");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final Map<Direction, BooleanProperty> FACING_MAP = new HashMap<Direction, BooleanProperty>() {
        {
            put(Direction.NORTH, NORTH);
            put(Direction.SOUTH, SOUTH);
            put(Direction.EAST, EAST);
            put(Direction.WEST, WEST);
            put(Direction.UP, UP);
            put(Direction.DOWN, DOWN);
        }
    };
    public static final VoxelShape BASE_SHAPE = VoxelShapes.create(.4375f, .4375f, .4375f, .5625f, .5625f, .5625f);
    public static final Map<Direction, VoxelShape> SHAPE_MAP = new HashMap<Direction, VoxelShape>() {
        {
            put(Direction.NORTH, VoxelShapes.create(.4375f, .4375f, .4375f, .5625f, .5625f, 0f));
            put(Direction.SOUTH, VoxelShapes.create(.4375f, .4375f, 1f, .5625f, .5625f, .5625f));
            put(Direction.EAST, VoxelShapes.create(.5625f, .4375f, .4375f, 1f, .5625f, .5625f));
            put(Direction.WEST, VoxelShapes.create(0f, .4375f, .4375f, .4375f, .5625f, .5625f));
            put(Direction.UP, VoxelShapes.create(.4375f, .5625f, .4375f, .5625f, 1f, .5625f));
            put(Direction.DOWN, VoxelShapes.create(.4375f, 0f, .4375f, .5625f, .4375f, .5625f));
        }
    };

    public LatticeBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.setDefaultState(this.getStateContainer().getBaseState()
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false)
                .with(LEAVES, false)
                .with(WATERLOGGED, false));
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack held = player.getHeldItem(handIn);
        if (!state.get(LEAVES) && Block.getBlockFromItem(held.getItem()) instanceof LeavesBlock) {
            worldIn.setBlockState(pos, state.with(LEAVES, true));
            return true;
        }
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape base = BASE_SHAPE;
        for (Direction dir : Direction.values()) {
            if (state.get(FACING_MAP.get(dir))) {
                base = VoxelShapes.combine(base, SHAPE_MAP.get(dir), IBooleanFunction.OR);
            }
        }
        return base;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, LEAVES, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        IFluidState fluidState = world.getFluidState(pos);
        BlockState state = this.getDefaultState();
        for (Direction dir : Direction.values()) {
            if (world.getBlockState(pos.offset(dir)).isSolid() || world.getBlockState(pos.offset(dir)).getBlock() == this) {
                state = state.with(FACING_MAP.get(dir), true);
            }
        }
        return state.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        if (facingState.isSolid() || facingState.getBlock() == this) {
            return stateIn.with(FACING_MAP.get(facing), true);
        }
        return stateIn.with(FACING_MAP.get(facing), false);
    }

    @Override
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }
}
