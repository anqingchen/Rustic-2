package com.samaritans.rustic2.block;

import com.samaritans.rustic2.entity.ModEntities;
import com.samaritans.rustic2.network.DismountChairPacket;
import com.samaritans.rustic2.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.entity.player.PlayerEntity.REACH_DISTANCE;

public class ChairBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE = VoxelShapes.create(0.125D, 0.0D, 0.125D, 0.875D, 1.125D, 0.875D);

    public ChairBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(FACING);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) >= player.getAttribute(REACH_DISTANCE).getValue() || player.isSneaking() || player.getRidingEntity() != null) {
            return true;
        }
        if (!worldIn.isRemote) {
            ChairEntity chair = new ChairEntity(ModEntities.chair, worldIn);
            Direction facing = state.get(FACING);
            chair.rotationYaw = facing.getHorizontalAngle();
            Vec3i facingVec = facing.getDirectionVec();
            double xOffset = facingVec.getX() * -0.125;
            double zOffset = facingVec.getZ() * -0.125;
            chair.setPosition(pos.getX() + 0.5 + xOffset, pos.getY() + 0.4, pos.getZ() + 0.5 + zOffset);
            worldIn.addEntity(chair);
            if (player.startRiding(chair)) {
                player.setPositionAndUpdate(chair.posX, chair.posY, chair.posZ);
            }
        }
        return true;
    }

    @SuppressWarnings("EntityConstructor")
    public static class ChairEntity extends Entity {
        public ChairEntity(EntityType<?> entityTypeIn, World worldIn) {
            super(entityTypeIn, worldIn);
        }

        @Override
        protected void registerData() {

        }

        @Override
        protected void readAdditional(CompoundNBT compound) {

        }

        @Override
        protected void writeAdditional(CompoundNBT compound) {

        }

        @Override
        public IPacket<?> createSpawnPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void applyOrientationToEntity(Entity entityToUpdate) {
            entityToUpdate.setRenderYawOffset(this.rotationYaw);
            float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
            float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
            entityToUpdate.prevRotationYaw += f1 - f;
            entityToUpdate.rotationYaw += f1 - f;
            entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
        }

        @Override
        public void tick() {
            super.tick();
            BlockPos pos = getPosition();
            if (!(world.getBlockState(pos).getBlock() instanceof ChairBlock)) {
                this.remove();
                return;
            }
            List<Entity> passengers = getPassengers();
            if (passengers.isEmpty()) {
                this.remove();
            }
            if (!world.isRemote) {
                for (Entity entity : passengers) {
                    if (entity.isSneaking() || entity.getDistanceSq(this.posX, this.posY, this.posZ) >= 1) {
                        this.remove();
                    }
                }
            }
        }

        @Override
        public boolean canBeAttackedWithItem() {
            return false;
        }

        @Override
        public void remove() {
            super.remove();
            if (world.isRemote) {
                PacketHandler.sendToServer(new DismountChairPacket());
            }
        }
    }
}
