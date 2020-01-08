package com.samaritans.rustic.world.feature;

import com.mojang.datafixers.Dynamic;
import com.samaritans.rustic.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.IPlantable;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class YewTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
    private static final BlockState LOG = ModBlocks.YEW_LOG.getDefaultState();
    private static final BlockState LEAVES = ModBlocks.YEW_LEAVES.getDefaultState();

    public YewTreeFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49920_1_, boolean p_i49920_2_) {
        super(p_i49920_1_, p_i49920_2_);
        this.setSapling((IPlantable) ModBlocks.YEW_SAPLING);
    }

    @Override
    public boolean place(Set<BlockPos> changedBlocks, IWorldGenerationReader worldIn, Random rand, BlockPos position, MutableBoundingBox boundsIn) {
        int height = rand.nextInt(3) + 4;
        int j = position.getX();
        int k = position.getY();
        int l = position.getZ();
        if (k >= 1 && k + height + 1 < 256) {
            BlockPos blockpos = position.down();
            if (!isSoil(worldIn, blockpos, getSapling())) {
                return false;
            } else if (!this.canGrow(worldIn, position, height)) {
                return false;
            } else {
                this.setDirtAt(worldIn, blockpos, position);
                this.setDirtAt(worldIn, blockpos.east(), position);
                this.setDirtAt(worldIn, blockpos.south(), position);
                this.setDirtAt(worldIn, blockpos.south().east(), position);

                for(int j2 = 0; j2 < height; ++j2) {
                    int k2 = k + j2;
                    BlockPos blockpos1 = new BlockPos(j, k2, l);
                    if (isAirOrLeaves(worldIn, blockpos1)) {
                        this.placeLogs(changedBlocks, worldIn, blockpos1, boundsIn);
                        this.placeLogs(changedBlocks, worldIn, blockpos1.east(), boundsIn);
                        this.placeLogs(changedBlocks, worldIn, blockpos1.south(), boundsIn);
                        this.placeLogs(changedBlocks, worldIn, blockpos1.east().south(), boundsIn);
                    }
                }

                for (int j3 = 2; j3 < height; ++j3) {
                    int k2 = k + j3;
                    BlockPos blockpos1 = new BlockPos(j, k2, l);
                    this.placeLeaves(worldIn, blockpos1.north(), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.north().east(), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.north().east(2), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.north().west(), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.west(), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.west().south(), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.west().south(2), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.east(2), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.east(2).south(), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.south(2), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.south(2).east(), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.south(2).east(2), boundsIn, changedBlocks);

                    this.placeLeaves(worldIn, blockpos1.north(2), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.north(2).east(), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.north(2).east(2), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.north(2).west(), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.west(2).north(), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.west(2), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.west(2).south(), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.west(2).south(2), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.east(3), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.east(3).south(), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.east(3).north(), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.east(3).south(2), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.south(3), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.south(3).east(), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.south(3).east(2), boundsIn, changedBlocks);
                    this.placeLeaves(worldIn, blockpos1.south(3).west(), boundsIn, changedBlocks);
                }

                BlockPos blockpos1 = new BlockPos(j-1, k + height, l-1);
                for (int j4 = 0; j4 < 16; ++j4) {
                    this.placeLeaves(worldIn, blockpos1.east(j4 % 4).south(j4 / 4), boundsIn, changedBlocks);
                }
                blockpos1 = blockpos1.up().east().south();
                this.placeLeaves(worldIn, blockpos1, boundsIn, changedBlocks);
                this.placeLeaves(worldIn, blockpos1.east(), boundsIn, changedBlocks);
                this.placeLeaves(worldIn, blockpos1.south(), boundsIn, changedBlocks);
                this.placeLeaves(worldIn, blockpos1.south().east(), boundsIn, changedBlocks);

                return true;
            }
        } else {
            return false;
        }
    }

    private boolean canGrow(IWorldGenerationBaseReader p_214615_1_, BlockPos blockPos, int height) {
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        BlockPos.MutableBlockPos mutableblockpos = new BlockPos.MutableBlockPos();

        for(int l = 0; l <= height + 1; ++l) {
            int i1 = 1;
            if (l == 0) {
                i1 = 0;
            }

            if (l >= height - 1) {
                i1 = 2;
            }

            for(int j1 = -i1; j1 <= i1; ++j1) {
                for(int k1 = -i1; k1 <= i1; ++k1) {
                    if (!func_214587_a(p_214615_1_, mutableblockpos.setPos(i + j1, j + l, k + k1))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void placeLogs(Set<BlockPos> p_214616_1_, IWorldGenerationReader p_214616_2_, BlockPos p_214616_3_, MutableBoundingBox p_214616_4_) {
        if (func_214587_a(p_214616_2_, p_214616_3_)) {
            this.setLogState(p_214616_1_, p_214616_2_, p_214616_3_, LOG, p_214616_4_);
        }
    }

    private void placeLeaves(IWorldGenerationReader reader, BlockPos pos, MutableBoundingBox boundingBox, Set<BlockPos> changed) {
        this.placeLeaves(reader, pos.getX(), pos.getY(), pos.getZ(), boundingBox, changed);
    }

    private void placeLeaves(IWorldGenerationReader p_214617_1_, int x, int y, int z, MutableBoundingBox boundingBox, Set<BlockPos> p_214617_6_) {
        BlockPos blockpos = new BlockPos(x, y, z);
        if (isAir(p_214617_1_, blockpos)) {
            this.setLogState(p_214617_6_, p_214617_1_, blockpos, LEAVES, boundingBox);
        }
    }
}
