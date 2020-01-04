package com.samaritans.rustic.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class FertileSoilBlock extends Block {
    private static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

    public FertileSoilBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFertile(BlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPE;
    }

    @Override
    public boolean canSustainPlant(BlockState blockState, IBlockReader world, BlockPos blockPos, Direction face, IPlantable plantable) {
        PlantType plantType = plantable.getPlantType(world, blockPos.offset(face));
        switch (plantType) {
            case Desert:
            case Beach:
            case Plains:
            case Cave:
            case Crop:
                return true;
            case Nether:
            case Water:
                return false;
        }
        return super.canSustainPlant(blockState, world, blockPos, face, plantable);
    }
}
