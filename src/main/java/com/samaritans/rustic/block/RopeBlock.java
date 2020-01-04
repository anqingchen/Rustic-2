package com.samaritans.rustic.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;

// fak dis i give up
public class RopeBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE_0_7;
    public static final BooleanProperty KNOT = BlockStateProperties.ATTACHED;
    private static final VoxelShape SHAPE = VoxelShapes.create(0.4375F, 0.0F, 0.4375F, 0.5625F, 1.0F, 0.5625F);

    public RopeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        Direction.Axis axis = state.get(AXIS);
        int distance = getDistance(worldIn, pos);
        if (axis.isVertical())
            return worldIn.getBlockState(pos.up()).getBlock() instanceof StakeBlock || worldIn.getBlockState(pos.up()).getBlock() == this;
        if (axis == Direction.Axis.X)
            return worldIn.getBlockState(pos.east()).getBlock() instanceof StakeBlock ||
                    worldIn.getBlockState(pos.west()).getBlock() instanceof StakeBlock ||
                    (worldIn.getBlockState(pos.east()).getBlock() == this && worldIn.getBlockState(pos.east()).get(AXIS) == axis && worldIn.getBlockState(pos.east()).get(DISTANCE) < distance) ||
                    (worldIn.getBlockState(pos.west()).getBlock() == this && worldIn.getBlockState(pos.west()).get(AXIS) == axis && worldIn.getBlockState(pos.west()).get(DISTANCE) < distance);
        if (axis == Direction.Axis.Z)
            return worldIn.getBlockState(pos.north()).getBlock() instanceof StakeBlock ||
                    worldIn.getBlockState(pos.south()).getBlock() instanceof StakeBlock ||
                    (worldIn.getBlockState(pos.north()).getBlock() == this && worldIn.getBlockState(pos.north()).get(AXIS) == axis && worldIn.getBlockState(pos.north()).get(DISTANCE) < distance) ||
                    (worldIn.getBlockState(pos.south()).getBlock() == this && worldIn.getBlockState(pos.south()).get(AXIS) == axis && worldIn.getBlockState(pos.south()).get(DISTANCE) < distance);
        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = this.getDefaultState();
        IWorldReader iworldreader = context.getWorld();
        BlockPos blockpos = context.getPos();

        for (Direction direction : Direction.values()) {
            if (isValidPosition(blockstate.with(AXIS, direction.getAxis()), iworldreader, blockpos)) {
                return blockstate.with(AXIS, direction.getAxis()).with(DISTANCE, getDistance(iworldreader, blockpos));
            }
        }
        return null;
    }

    public int getDistance(IBlockReader iblockReader, BlockPos blockPos) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = (new BlockPos.MutableBlockPos(blockPos)).move(Direction.UP);
        BlockState blockstate = iblockReader.getBlockState(blockpos$mutableblockpos);
        int i = 7;
        if (blockstate.getBlock() == this) {
            i = blockstate.get(DISTANCE);
        } else if (blockstate.getBlock() instanceof StakeBlock) {
            return 0;
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState blockstate1 = iblockReader.getBlockState(blockpos$mutableblockpos.setPos(blockPos).move(direction));
            if (blockstate1.getBlock() == this) {
                i = Math.min(i, blockstate1.get(DISTANCE) + 1);
                if (i == 1) {
                    break;
                }
            }
            if (blockstate1.getBlock() instanceof StakeBlock) {
                return 0;
            }
        }

        return i;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
        return true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(AXIS, DISTANCE);
//        builder.add(KNOT);
    }
}
