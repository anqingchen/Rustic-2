package com.samaritans.rustic.block.trees;

import com.samaritans.rustic.world.feature.IronwoodTreeFeature;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class IronwoodTree extends Tree {
    @Nullable
    @Override
    protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random random) {
        return new IronwoodTreeFeature(NoFeatureConfig::deserialize, true);
    }
}
