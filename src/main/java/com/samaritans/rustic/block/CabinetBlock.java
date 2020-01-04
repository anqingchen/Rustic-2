package com.samaritans.rustic.block;

import com.samaritans.rustic.Rustic;
import com.samaritans.rustic.tileentity.CabinetTileEntity;
import com.samaritans.rustic.tileentity.ModTileEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CabinetBlock extends ContainerBlock {
    public static final EnumProperty<CabinetType> TYPE = EnumProperty.create("type", CabinetType.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE_SINGLE = VoxelShapes.fullCube();
    private static final VoxelShape SHAPE_BOTTOM = VoxelShapes.create(0, 0, 0, 1, 2, 1);
    private static final VoxelShape SHAPE_TOP = VoxelShapes.create(0, 1, 0, 1, -1, 1);
    private static final InventoryFactory<IInventory> inventoryFactory = new InventoryFactory<IInventory>() {
        public IInventory forDouble(CabinetTileEntity cabinetA, CabinetTileEntity cabinetB) {
            return new DoubleSidedInventory(cabinetA, cabinetB);
        }

        public IInventory forSingle(CabinetTileEntity cabinetA) {
            return cabinetA;
        }
    };
    private static final InventoryFactory<INamedContainerProvider> namedFactory = new InventoryFactory<INamedContainerProvider>() {
        public INamedContainerProvider forDouble(final CabinetTileEntity cabinetTileEntity, final CabinetTileEntity cabinetTileEntity1) {
            final IInventory iinventory = new DoubleSidedInventory(cabinetTileEntity, cabinetTileEntity1);
            return new INamedContainerProvider() {
                @Nullable
                public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
                    if (cabinetTileEntity.canOpen(p_createMenu_3_) && cabinetTileEntity1.canOpen(p_createMenu_3_)) {
                        cabinetTileEntity.fillWithLoot(p_createMenu_2_.player);
                        cabinetTileEntity1.fillWithLoot(p_createMenu_2_.player);
                        return ChestContainer.createGeneric9X6(p_createMenu_1_, p_createMenu_2_, iinventory);
                    } else {
                        return null;
                    }
                }

                public ITextComponent getDisplayName() {
                    if (cabinetTileEntity.hasCustomName()) {
                        return cabinetTileEntity.getDisplayName();
                    } else {
                        return (cabinetTileEntity1.hasCustomName() ? cabinetTileEntity1.getDisplayName() : new TranslationTextComponent(Rustic.MODID + ".container.cabinetDouble"));
                    }
                }
            };
        }

        public INamedContainerProvider forSingle(CabinetTileEntity cabinetTileEntity) {
            return cabinetTileEntity;
        }
    };

    public CabinetBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    public static <T> T getCabinetInventory(BlockState blockStateIn, IWorld world, BlockPos blockPos, InventoryFactory<T> factory) {
        TileEntity tileentity = world.getTileEntity(blockPos);
        if (!(tileentity instanceof CabinetTileEntity)) {
            return null;
        } else if (isBlocked(world, blockPos)) {
            return null;
        } else {
            CabinetTileEntity cabinetTileEntity = (CabinetTileEntity) tileentity;
            CabinetType cabinetType = blockStateIn.get(TYPE);
            if (cabinetType == CabinetType.SINGLE) {
                return factory.forSingle(cabinetTileEntity);
            } else {
                BlockPos blockpos = blockPos.offset(getDirectionToAttached(blockStateIn));
                BlockState blockstate = world.getBlockState(blockpos);
                if (blockstate.getBlock() == blockStateIn.getBlock()) {
                    CabinetType cabinetType1 = blockstate.get(TYPE);
                    if (cabinetType1 != CabinetType.SINGLE && cabinetType != cabinetType1 && blockstate.get(FACING) == blockStateIn.get(FACING)) {
                        if (isBlocked(world, blockpos)) {
                            return null;
                        }
                        TileEntity tileentity1 = world.getTileEntity(blockpos);
                        if (tileentity1 instanceof CabinetTileEntity) {
                            CabinetTileEntity cabinetTileEntity1 = cabinetType == CabinetType.BOTTOM ? cabinetTileEntity : (CabinetTileEntity) tileentity1;
                            CabinetTileEntity cabinetTileEntity2 = cabinetType == CabinetType.BOTTOM ? (CabinetTileEntity) tileentity1 : cabinetTileEntity;
                            return factory.forDouble(cabinetTileEntity1, cabinetTileEntity2);
                        }
                    }
                }
                return factory.forSingle(cabinetTileEntity);
            }
        }
    }

    private static boolean isBlocked(IWorld world, BlockPos blockPos) {
        Direction direction = world.getBlockState(blockPos).get(FACING);
        return world.getBlockState(blockPos.offset(direction)).isNormalCube(world, blockPos.offset(direction));
    }

    public static Direction getDirectionToAttached(BlockState state) {
        return state.get(TYPE) == CabinetType.TOP ? Direction.DOWN : Direction.UP;
    }

    @Nullable
    public static IInventory getInventory(BlockState state, World world, BlockPos pos) {
        return getCabinetInventory(state, world, pos, inventoryFactory);
    }

    @Override
    public boolean hasCustomBreakingProgress(BlockState state) {
        return true;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(TYPE)) {
            case TOP:
                return SHAPE_TOP;
            case BOTTOM:
                return SHAPE_BOTTOM;
            default:
                return SHAPE_SINGLE;
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            INamedContainerProvider inamedcontainerprovider = this.getContainer(state, worldIn, pos);
            if (inamedcontainerprovider != null) {
                player.openContainer(inamedcontainerprovider);
                // todo: open stats
            }
        }
        return true;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof IInventory) {
                InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
                worldIn.updateComparatorOutputLevel(pos, this);
            }
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        CabinetType cabinetType = CabinetType.SINGLE;
        Direction direction = context.getPlacementHorizontalFacing().getOpposite();
        boolean flag = context.isPlacerSneaking();
        Direction direction1 = context.getFace();
        if (direction1.getAxis().isVertical() && flag) {
            Direction direction2 = this.getDirectionToAttach(context, direction1.getOpposite());
            if (direction2 != null) {
                cabinetType = direction2 == Direction.DOWN ? CabinetType.BOTTOM : CabinetType.TOP;
            }
        }
        return this.getDefaultState().with(FACING, direction).with(TYPE, cabinetType);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof CabinetTileEntity) {
                ((CabinetTileEntity) tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing.getAxis().isVertical()) {
            if (facingState.getBlock() == this) {
                CabinetType cabinetType = facingState.get(TYPE);
                if (stateIn.get(TYPE) == CabinetType.SINGLE && cabinetType != CabinetType.SINGLE && stateIn.get(FACING) == facingState.get(FACING)) {
                    return stateIn.with(TYPE, cabinetType.opposite());
                }
            } else if (getDirectionToAttached(stateIn) == facing) {
                return stateIn.with(TYPE, CabinetType.SINGLE);
            }
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Nullable
    private Direction getDirectionToAttach(BlockItemUseContext context, Direction direction) {
        BlockState blockstate = context.getWorld().getBlockState(context.getPos().offset(direction));
        return blockstate.getBlock() == this && blockstate.get(TYPE) == CabinetType.SINGLE && blockstate.get(FACING) == context.getPlacementHorizontalFacing().getOpposite() ? direction.getOpposite() : null;
    }

    @Nullable
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return getCabinetInventory(state, worldIn, pos, namedFactory);
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstoneFromInventory(getInventory(blockState, worldIn, pos));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(TYPE, FACING);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return ModTileEntityType.CABINET.create();
    }

    public enum CabinetType implements IStringSerializable {
        SINGLE("single", 0),
        TOP("top", 2),
        BOTTOM("bottom", 1);

        public static final CabinetType[] VALUES = values();
        private final String name;
        private final int opposite;

        CabinetType(String name, int oppositeIn) {
            this.name = name;
            this.opposite = oppositeIn;
        }

        public String getName() {
            return this.name;
        }

        public CabinetType opposite() {
            return VALUES[this.opposite];
        }
    }

    interface InventoryFactory<T> {
        T forDouble(CabinetTileEntity p_212855_1_, CabinetTileEntity p_212855_2_);

        T forSingle(CabinetTileEntity p_212856_1_);
    }
}
