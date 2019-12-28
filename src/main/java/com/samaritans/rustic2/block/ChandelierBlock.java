package com.samaritans.rustic2.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.Random;

public class ChandelierBlock extends FallingBlock {
    private static final VoxelShape SHAPE = VoxelShapes.create(0, 0, 0, 1, 0.25, 1);

    public ChandelierBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    protected void onStartFalling(FallingBlockEntity fallingEntity) {
        fallingEntity.setHurtEntities(true);
        ObfuscationReflectionHelper.setPrivateValue(FallingBlockEntity.class, fallingEntity, 400, "fallHurtMax");
        ObfuscationReflectionHelper.setPrivateValue(FallingBlockEntity.class, fallingEntity, 6F, "fallHurtAmount");
        super.onStartFalling(fallingEntity);
    }

    @Override
    public void onEndFalling(World worldIn, BlockPos pos, BlockState p_176502_3_, BlockState p_176502_4_) {
        worldIn.playEvent(1031, pos, 0);
    }

    @Override
    public void onBroken(World worldIn, BlockPos pos) {
        worldIn.playEvent(1029, pos, 0);
    }

    @Override
    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
        if (!worldIn.isRemote) {
            this.checkFallable(worldIn, pos);
        }
    }

    private boolean suspended(World worldIn, BlockPos pos) {
        BlockState state = worldIn.getBlockState(pos.up());
        return Block.hasSolidSide(state, worldIn, pos.up(), Direction.DOWN);
    }

    private void checkFallable(World worldIn, BlockPos pos) {
        if (!suspended(worldIn, pos) && (worldIn.isAirBlock(pos.down()) || canFallThrough(worldIn.getBlockState(pos.down())) && pos.getY() >= 0)) {
            if (!worldIn.isRemote) {
                FallingBlockEntity fallingblockentity = new FallingBlockEntity(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos));
                this.onStartFalling(fallingblockentity);
                worldIn.addEntity(fallingblockentity);
            }
        }
    }
}
