package com.samaritans.rustic2.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.samaritans.rustic2.block.PotBlock;
import com.samaritans.rustic2.tileentity.PotTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import org.lwjgl.opengl.GL11;

public class PotTileEntityRenderer extends TileEntityRenderer<PotTileEntity> {
    @Override
    public void render(PotTileEntity tank, double x, double y, double z, float partialTicks, int destroyStage) {
        int amount = tank.getFluidHandler().getFluidAmount();
        int capacity = tank.getFluidHandler().getCapacity();
        Fluid fluid = tank.getFluidHandler().getFluid().getFluid();
        if (fluid != Fluids.EMPTY){
            int c = fluid.getAttributes().getColor();
            int blue = c & 0xFF;
            int green = (c >> 8) & 0xFF;
            int red = (c >> 16) & 0xFF;
            int a = (c >> 24) & 0xFF;

            TextureAtlasSprite stillSprite = Minecraft.getInstance().getTextureMap().getAtlasSprite(fluid.getAttributes().getStillTexture().toString());

            double fluidHeight = 0.125 + (0.8125 * ((float) amount / (float) capacity));
            float fluidRadius = ((PotBlock) tank.getBlockState().getBlock()).getInnerRadius((int) (fluidHeight * 16));

            float minU = stillSprite.getInterpolatedU(0);
            float maxU = stillSprite.getInterpolatedU(16);
            float minV = stillSprite.getInterpolatedV(0);
            float maxV = stillSprite.getInterpolatedV(16);

            int i = getWorld().getCombinedLight(tank.getPos(), fluid.getAttributes().getLuminosity());
            int lightx = i >> 0x10 & 0xFFFF;
            int lighty = i & 0xFFFF;

            GlStateManager.disableCull();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            buffer.pos(x + 0.5 - fluidRadius, y + fluidHeight, z + 0.5 - fluidRadius).tex(minU, minV).lightmap(lightx, lighty).color(red, green, blue, a).endVertex();
            buffer.pos(x + 0.5 + fluidRadius, y + fluidHeight, z + 0.5 - fluidRadius).tex(maxU, minV).lightmap(lightx, lighty).color(red, green, blue, a).endVertex();
            buffer.pos(x + 0.5 + fluidRadius, y + fluidHeight, z + 0.5 + fluidRadius).tex(maxU, maxV).lightmap(lightx, lighty).color(red, green, blue, a).endVertex();
            buffer.pos(x + 0.5 - fluidRadius, y + fluidHeight, z + 0.5 + fluidRadius).tex(minU, maxV).lightmap(lightx, lighty).color(red, green, blue, a).endVertex();
            tess.draw();

            GlStateManager.disableAlphaTest();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
        }
    }
}
