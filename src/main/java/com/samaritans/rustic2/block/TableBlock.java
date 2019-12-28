package com.samaritans.rustic2.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class TableBlock extends Block {
    public static final BooleanProperty NW = BooleanProperty.create("nw");
    public static final BooleanProperty NE = BooleanProperty.create("ne");
    public static final BooleanProperty SE = BooleanProperty.create("se");
    public static final BooleanProperty SW = BooleanProperty.create("sw");

    // todo: more accurate hitbox
    public static final VoxelShape SHAPE = VoxelShapes.create(0, 0.875, 0, 1, 1, 1);

    public TableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        BlockState stateTemp = worldIn.getBlockState(currentPos.north());
        Block blockTemp = stateTemp.getBlock();
        boolean blockNorth = blockTemp == this;
        stateTemp = worldIn.getBlockState(currentPos.south());
        blockTemp = stateTemp.getBlock();
        boolean blockSouth = blockTemp == this;
        stateTemp = worldIn.getBlockState(currentPos.east());
        blockTemp = stateTemp.getBlock();
        boolean blockEast = blockTemp == this;
        stateTemp = worldIn.getBlockState(currentPos.west());
        blockTemp = stateTemp.getBlock();
        boolean blockWest = blockTemp == this;

        return stateIn.with(NW, !blockNorth && !blockWest)
                .with(NE, !blockNorth && !blockEast)
                .with(SE, !blockSouth && !blockEast)
                .with(SW, !blockSouth && !blockWest);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(NW, NE, SE, SW);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
}
