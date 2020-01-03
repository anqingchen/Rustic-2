package com.samaritans.rustic2.block;

import com.samaritans.rustic2.tileentity.ModTileEntityType;
import com.samaritans.rustic2.tileentity.PotTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;

public class PotBlock extends ContainerBlock {
    protected static final VoxelShape SHAPE = VoxelShapes.create(0.125D, 0.0D, 0.125D, 0.875D, 1D, 0.875D);

    public PotBlock(Properties builder) {
        super(builder);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        PotTileEntity tile = (PotTileEntity) worldIn.getTileEntity(pos);
        ItemStack stack = player.getHeldItem(handIn);
        if (tile != null) {
            if ((tile.isItemEmpty() && tile.getFluidHandler().getCapacity() > 0 && FluidUtil.getFluidContained(stack).isPresent()) || tile.getFluidHandler().getFluidAmount() > 0) {
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

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
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
        if (this == ModBlocks.pot0 || this == ModBlocks.pot1 || this == ModBlocks.pot2 || this == ModBlocks.pot3 || this == ModBlocks.pot4 || this == ModBlocks.pot5 || this == ModBlocks.pot6 || this == ModBlocks.pot7) {
            return 0;
        } else return 1;
    }
}
