package com.samaritans.rustic.client.renderer;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.samaritans.rustic.Util;
import com.samaritans.rustic.block.CabinetBlock;
import com.samaritans.rustic.block.ModBlocks;
import com.samaritans.rustic.client.renderer.model.CabinetModel;
import com.samaritans.rustic.client.renderer.model.TallCabinetModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

// todo: possibly allow mirror, or render items inside cabinet?
public class CabinetTileEntityRenderer<T extends TileEntity & IChestLid> extends TileEntityRenderer<T> {
    protected static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("rustic:textures/model/cabinet.png");
    protected static final ResourceLocation TEXTURE_DOUBLE = new ResourceLocation("rustic:textures/model/cabinet_double.png");
    protected final CabinetModel simpleCabinet = new CabinetModel(false);
    //    protected final CabinetModel simpleCabinetM = new CabinetModel(true);
    protected final CabinetModel tallCabinet = new TallCabinetModel(false);
//    protected final TallCabinetModel doubleCabinetM = new TallCabinetModel(true);

    @SuppressWarnings("ConstantConditions")
    public static int getColor(Block block) {
        if (block == ModBlocks.ironwood_cabinet) {
            return 0x9a8064;
        } else if (block == ModBlocks.acacia_cabinet) {
            return 12215095;
        } else if (block == ModBlocks.birch_cabinet) {
            return 14139781;
        } else if (block == ModBlocks.dark_oak_cabinet) {
            return 5190168;
        } else if (block == ModBlocks.jungle_cabinet) {
            return 12093284;
        } else if (block == ModBlocks.oak_cabinet) {
            return 12096607;
        } else {
            return 8544570;
        }
    }

    @Override
    public void render(T tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
//        GlStateManager.enableDepthTest();
//        GlStateManager.depthFunc(515);
//        GlStateManager.depthMask(true);
        BlockState blockstate = tileEntityIn.hasWorld() ? tileEntityIn.getBlockState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
        CabinetBlock.CabinetType cabinetType = blockstate.has(CabinetBlock.TYPE) ? blockstate.get(CabinetBlock.TYPE) : CabinetBlock.CabinetType.SINGLE;
        if (cabinetType != CabinetBlock.CabinetType.TOP) {
            boolean flag = cabinetType != CabinetBlock.CabinetType.SINGLE;
            CabinetModel modelcabinet = getCabinetModel(tileEntityIn, destroyStage, flag);
            if (destroyStage >= 0) {
                GlStateManager.matrixMode(5890);
                GlStateManager.pushMatrix();
                GlStateManager.scalef(flag ? 8.0F : 4.0F, 4.0F, 1.0F);
                GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
                GlStateManager.matrixMode(5888);
            } else {
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            // boolean mirror = te.getWorld().getBlockState(te.getPos()).getValue(BlockCabinet.MIRROR);

            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            int color = getColor(tileEntityIn.getBlockState().getBlock());
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = ((color) & 0xFF) / 255f;
            GlStateManager.color3f(r, g, b);

            int packedLight = getWorld().getCombinedLight(tileEntityIn.getPos().up(), 0);
            int skyLight = packedLight & 0xFF;
            int blockLight = packedLight >> 16 & 0xFF;
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, skyLight, blockLight);

            GlStateManager.translatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
            GlStateManager.scalef(1.0F, -1.0F, -1.0F);

            float j = blockstate.get(ChestBlock.FACING).getHorizontalAngle();
            if ((double) Math.abs(j) > 1.0E-5D) {
                GlStateManager.translatef(0.5F, 0.5F, 0.5F);
                GlStateManager.rotatef(j, 0.0F, 1.0F, 0.0F);
                GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
            }

            float f = tileEntityIn.getLidAngle(partialTicks);
            f = 1.0F - f;
            f = 1.0F - f * f * f;
            modelcabinet.getDoor().rotateAngleY = (f * ((float) Math.PI / 2F));
            GlStateManager.translatef(0.5F, -0.5F, 0.5F);

            modelcabinet.renderAll();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (destroyStage >= 0) {
                GlStateManager.matrixMode(5890);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
            }
        }
    }

    private CabinetModel getCabinetModel(T tileEntityIn, int destroyStage, boolean tallCabinet) {
        ResourceLocation resourcelocation;
        if (destroyStage >= 0) {
            resourcelocation = DESTROY_STAGES[destroyStage];
        } else {
            resourcelocation = tallCabinet ? TEXTURE_DOUBLE : TEXTURE_NORMAL;
        }
        this.bindTexture(resourcelocation);
        return tallCabinet ? this.tallCabinet : this.simpleCabinet;
    }

    public static class TEISR extends ItemStackTileEntityRenderer {
        private static CabinetModel model = new CabinetModel(false);

        @Override
        public void renderByItem(ItemStack stack) {
//            GlStateManager.enableDepthTest();
//            GlStateManager.depthFunc(515);
//            GlStateManager.depthMask(true);

            Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_NORMAL);

            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();

            int color = getColor(Block.getBlockFromItem(stack.getItem()));
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = ((color) & 0xFF) / 255f;
            GlStateManager.color4f(r, g, b, 1F);

            GlStateManager.translatef(0F, 2F, 1F);
            GlStateManager.scalef(1.0F, -1.0F, -1.0F);
            GlStateManager.translatef(0.5F, 0.5F, 0.5F);
            model.renderAll();

            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
