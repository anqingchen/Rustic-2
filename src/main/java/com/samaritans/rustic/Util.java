package com.samaritans.rustic;

import com.google.common.base.Preconditions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

public class Util {
    /**
     * Performs setup on a registry entry
     *
     * @param name The path of the entry's name. Used to make a name who's domain is our mod's modid
     */
    @Nonnull
    public static <T extends IForgeRegistryEntry<T>> T setup(@Nonnull final T entry, @Nonnull final String name) {
        Preconditions.checkNotNull(name, "Name to assign to entry cannot be null!");
        return setup(entry, new ResourceLocation(Rustic.MODID, name));
    }

    /**
     * Performs setup on a registry entry
     *
     * @param registryName The full registry name of the entry
     */
    public static <T extends IForgeRegistryEntry<T>> T setup(@Nonnull final T entry, @Nonnull final ResourceLocation registryName) {
        Preconditions.checkNotNull(entry, "Entry cannot be null!");
        Preconditions.checkNotNull(registryName, "Registry name to assign to entry cannot be null!");
        entry.setRegistryName(registryName);
        return entry;
    }

    public static RayTraceResult rayTrace(World worldIn, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
        float f = player.rotationPitch;
        float f1 = player.rotationYaw;
        Vec3d vec3d = player.getEyePosition(1.0F);
        float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
        Vec3d vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, fluidMode, player));
    }
    
    public static int rgbInt(float r, float g, float b) {
    	return (((int) (r * 255) & 255) << 16) | (((int) (g * 255) & 255) << 8) | ((int) (b * 255) & 255);
    }
    
    public static int rgbInt(int r, int g, int b) {
    	return ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }

//    public static int calculateColor(@Nullable TextureAtlasSprite sprite) {
//        if (sprite == null) return 0xFFFF00FF;
//        int numPixels = 0;
//        int width = sprite.getWidth();
//        int height = sprite.getHeight();
//
//        int a = 0xFF;
//        int r = 0;
//        int g = 0;
//        int b = 0;
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                int c = sprite.getPixelRGBA(0, x, y);
//                if (((c >> 24) & 0xFF) <= 0) continue;
//
//                r += (c >> 16) & 0xFF;
//                g += (c >> 8) & 0xFF;
//                b += (c) & 0xFF;
//
//                numPixels++;
//            }
//        }
//
//        r = MathHelper.clamp(Math.round(((float) r / (float) numPixels)), 0, 255);
//        g = MathHelper.clamp(Math.round(((float) g / (float) numPixels)), 0, 255);
//        b = MathHelper.clamp(Math.round(((float) b / (float) numPixels)), 0, 255);
//
//        int color = (a << 24) | (r << 16) | (g << 8) | (b);
//        return color;
//    }
}
