package com.samaritans.rustic.block;

import com.samaritans.rustic.tileentity.CrushingTubTileEntity;
import com.samaritans.rustic.tileentity.ModTileEntityType;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class CrushingTubBlock extends ContainerBlock {
    private static final VoxelShape TUB = makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);

    // todo: somehow allow player inside the tub and crush stuff
    private static final VoxelShape INSIDE = makeCuboidShape(1.0D, 2.0D, 1.0D, 15.0D, 9.0D, 15.0D);
    private static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D),
            VoxelShapes.or(makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 9.0D, 1.0D),
                    makeCuboidShape(15.0D, 0.0D, 0.0D, 16.0D, 9.0D, 1.0D),
                    makeCuboidShape(0.0D, 0.0D, 15.0D, 1.0D, 9.0D, 16.0D),
                    makeCuboidShape(15.0D, 0.0D, 15.0D, 16.0D, 9.0D, 16.0D),
                    INSIDE), IBooleanFunction.ONLY_FIRST);

    public CrushingTubBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onBlockActivated(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        TileEntity tileEntity = world.getTileEntity(blockPos);
        if (tileEntity instanceof CrushingTubTileEntity) {
            return ((CrushingTubTileEntity) tileEntity).activate(blockState, world, blockPos, player, hand, rayTraceResult);
        }
        return false;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (worldIn.getTileEntity(pos) instanceof CrushingTubTileEntity) {
                CrushingTubTileEntity te = (CrushingTubTileEntity) worldIn.getTileEntity(pos);
                ItemStackHandler handler = te.getItemHandler();
                for (int i = 0; i < handler.getSlots(); i++) {
                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                }
            }
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entity, float distance) {
        if (!world.isRemote && entity instanceof LivingEntity && world.getTileEntity(pos) instanceof CrushingTubTileEntity) {
            ((CrushingTubTileEntity) world.getTileEntity(pos)).crush((LivingEntity) entity);
        }
        super.onFallenUpon(world, pos, entity, distance);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return TUB;
    }

    @Override
    public boolean isNormalCube(BlockState p_220081_1_, IBlockReader p_220081_2_, BlockPos p_220081_3_) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return ModTileEntityType.CRUSHING_TUB.create();
    }
}
