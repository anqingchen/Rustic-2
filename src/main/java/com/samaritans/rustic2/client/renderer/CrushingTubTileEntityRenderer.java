package com.samaritans.rustic2.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.samaritans.rustic2.tileentity.CrushingTubTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class CrushingTubTileEntityRenderer extends TileEntityRenderer<CrushingTubTileEntity> {
    @Override
    public void render(CrushingTubTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        IItemHandlerModifiable itemStackHandler = te.getItemHandler();
        if (itemStackHandler.getSlots() > 0 && !itemStackHandler.getStackInSlot(0).isEmpty() && te.getWorld() != null) {
            ItemStack stack = itemStackHandler.getStackInSlot(0);
            int itemCount = (int)Math.ceil((stack.getCount())/8.0);
            Random rand = new Random(getWorld().getSeed());
            for (int i = 0; i < itemCount; i ++){
                GlStateManager.pushMatrix();
                RenderHelper.enableStandardItemLighting();
                GlStateManager.enableLighting();
                GlStateManager.translated(x, y+0.062+(i*0.0625), z);
                GlStateManager.translated(0.5, 0.0, 0.5);
                GlStateManager.rotated(rand.nextFloat()*360.0, 0, 1.0, 0);
                GlStateManager.translated(-0.5, 0, -0.5);
                GlStateManager.rotated(90, 1, 0, 0);
                GlStateManager.translated(0.5, -0.1875, 0.0);
                GlStateManager.translated(0.0, 0.6875, 0.0);
                GlStateManager.scaled(0.5, 0.5, 0.5);
                Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                GlStateManager.popMatrix();
            }
        }

        FluidTank tank = te.getFluidHandler();
        int amount = tank.getFluidInTank(0).getAmount();
        int capacity = tank.getTankCapacity(0);
        Fluid fluid = tank.getFluidInTank(0).getFluid();
        if (fluid != Fluids.EMPTY){
            int c = fluid.getAttributes().getColor();
            int blue = c & 0xFF;
            int green = (c >> 8) & 0xFF;
            int red = (c >> 16) & 0xFF;
            int a = (c >> 24) & 0xFF;

            TextureAtlasSprite stillSprite = Minecraft.getInstance().getTextureMap().getAtlasSprite(fluid.getAttributes().getStillTexture().toString());

            float minU = stillSprite.getInterpolatedU(0);
            float maxU = stillSprite.getInterpolatedU(16);
            float minV = stillSprite.getInterpolatedV(0);
            float maxV = stillSprite.getInterpolatedV(16);

            int i = getWorld().getCombinedLight(te.getPos(), fluid.getAttributes().getLuminosity());
            int lightx = i >> 0x10 & 0xFFFF;
            int lighty = i & 0xFFFF;

            GlStateManager.pushTextureAttributes();
            GlStateManager.disableCull();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            buf.pos(x+0.0625, y+0.0625+0.5*((float)amount/(float)capacity), z+0.0625).tex(minU, minV).lightmap(lightx,lighty).color(red,green,blue,a).endVertex();
            buf.pos(x+0.9375, y+0.0625+0.5*((float)amount/(float)capacity), z+0.0625).tex(maxU, minV).lightmap(lightx,lighty).color(red,green,blue,a).endVertex();
            buf.pos(x+0.9375, y+0.0625+0.5*((float)amount/(float)capacity), z+0.9375).tex(maxU, maxV).lightmap(lightx,lighty).color(red,green,blue,a).endVertex();
            buf.pos(x+0.0625, y+0.0625+0.5*((float)amount/(float)capacity), z+0.9375).tex(minU, maxV).lightmap(lightx,lighty).color(red,green,blue,a).endVertex();
            tess.draw();

            GlStateManager.disableAlphaTest();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            GlStateManager.popAttributes();
        }
    }
}
