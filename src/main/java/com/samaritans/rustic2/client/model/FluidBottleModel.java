package com.samaritans.rustic2.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.samaritans.rustic2.Rustic2;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.*;
import java.util.function.Function;

public class FluidBottleModel implements IUnbakedModel {
//    private final ResourceLocation liquidLocation = new ResourceLocation("minecraft", "item/potion_overlay");
//    private final ResourceLocation bottleLocation = new ResourceLocation("minecraft", "item/potion_bottle_drinkable");

    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation(Rustic2.MODID, "fluid_bottle"), "inventory");

    private final ResourceLocation liquidLocation, bottleLocation;
    private final Fluid fluid;
    private final boolean tint;

    private static final float NORTH_Z_FLUID = 7.498f / 16f;
    private static final float SOUTH_Z_FLUID = 8.502f / 16f;

    public static final IUnbakedModel MODEL = new FluidBottleModel();

    public FluidBottleModel() {
        this(null, null, null, true);
    }

    public FluidBottleModel(ResourceLocation bottleLocation, ResourceLocation liquidLocation, Fluid fluid, boolean tint) {
        this.fluid = fluid;
        this.bottleLocation = new ResourceLocation("minecraft", "item/potion_bottle_drinkable");
        this.liquidLocation = new ResourceLocation("minecraft", "item/potion_overlay");
        this.tint = tint;
    }

    @Override
    public IUnbakedModel retexture(ImmutableMap<String, String> textures) {
        ResourceLocation base = new ResourceLocation("minecraft", "item/potion_bottle_drinkable");
        ResourceLocation liquid = new ResourceLocation("minecraft", "item/potion_overlay");

//        if (textures.containsKey("base"))
//            base = new ResourceLocation(textures.get("base"));
//        if (textures.containsKey("fluid"))
//            liquid = new ResourceLocation(textures.get("fluid"));

        return new FluidBottleModel(base, liquid, fluid, tint);
    }

    @Override
    public IUnbakedModel process(ImmutableMap<String, String> customData) {
        ResourceLocation fluidName = new ResourceLocation(customData.get("fluid"));
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
        if (fluid == null) {
            fluid = this.fluid;
        }
        return new FluidBottleModel(bottleLocation, liquidLocation, fluid, tint);
    }

    @Nullable
    @Override
    public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format) {
        ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(sprite.getState());

        TRSRTransformation transform = sprite.getState().apply(Optional.empty()).orElse(TRSRTransformation.identity());
        TextureAtlasSprite fluidSprite = null;
        TextureAtlasSprite particleSprite = null;
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        if (fluid != null) {
            fluidSprite = spriteGetter.apply(fluid.getAttributes().getStillTexture());
        }

        if (bottleLocation != null) {
            IBakedModel model = (new ItemLayerModel(ImmutableList.of(bottleLocation))).bake(bakery, spriteGetter, sprite, format);
            builder.addAll(model.getQuads(null, null, new Random(), EmptyModelData.INSTANCE));
            particleSprite = model.getParticleTexture();
        }

        if (liquidLocation != null && fluidSprite != null)
        {
            TextureAtlasSprite liquid = spriteGetter.apply(liquidLocation);
            builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, NORTH_Z_FLUID, Direction.NORTH, tint ? fluid.getAttributes().getColor() : 0xFFFFFFFF, 1));
            builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, SOUTH_Z_FLUID, Direction.SOUTH, tint ? fluid.getAttributes().getColor() : 0xFFFFFFFF, 1));
            particleSprite = fluidSprite;
        }

        return new BakedFluidBottle(bakery,this, builder.build(), particleSprite, format, Maps.immutableEnumMap(transformMap), Maps.<String, IBakedModel>newHashMap(), transform.isIdentity());
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        if (liquidLocation != null)
            builder.add(liquidLocation);
        if (bottleLocation != null)
            builder.add(bottleLocation);
        if (fluid != null)
            builder.add(fluid.getAttributes().getStillTexture());
        return builder.build();
    }

    private static final class BakedFluidBottleOverrideHandler extends ItemOverrideList {
        private final ModelBakery bakery;

        private BakedFluidBottleOverrideHandler(ModelBakery bakery) {
            this.bakery = bakery;
        }

        @Nonnull
        public IBakedModel getModelWithOverrides(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
            FluidStack fluidStack = null;
            if (stack.hasTag() && stack.getTag().contains(FluidHandlerItemStack.FLUID_NBT_KEY)) {
                fluidStack = FluidStack.loadFluidStackFromNBT((CompoundNBT) stack.getTag().get(FluidHandlerItemStack.FLUID_NBT_KEY));
            }

            if (fluidStack == null) {
                return originalModel;
            }

            BakedFluidBottle model = (BakedFluidBottle) originalModel;

            Fluid fluid = fluidStack.getFluid();
            String name = fluid.getRegistryName().getPath();

            if (!model.cache.containsKey(name)) {
                IUnbakedModel parent = model.parent.process(ImmutableMap.of("fluid", name));
                Function<ResourceLocation, TextureAtlasSprite> textureGetter;
                textureGetter = location -> Minecraft.getInstance().getTextureMap().getAtlasSprite(location.toString());
                ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms = ObfuscationReflectionHelper.getPrivateValue(BakedItemModel.class, model, "transforms");
                IBakedModel bakedModel = parent.bake(bakery, textureGetter, new SimpleModelState(transforms), model.format);
                model.cache.put(name, bakedModel);
                return bakedModel;
            }

            return model.cache.get(name);
        }
    }

    public enum FluidBottleLoader implements ICustomModelLoader
    {
        INSTANCE;

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getNamespace().equals(Rustic2.MODID) && modelLocation.getPath().contains("fluid_bottle");
        }

        @Override
        public IUnbakedModel loadModel(ResourceLocation modelLocation)
        {
            return MODEL;
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {
            // no need to clear cache since we create a new model instance
        }
    }

    private static final class BakedFluidBottle extends BakedItemModel {

        private final FluidBottleModel parent;
        private final Map<String, IBakedModel> cache;
        private final VertexFormat format;

        public BakedFluidBottle(ModelBakery bakery,
                                FluidBottleModel parent,
                                ImmutableList<BakedQuad> quads,
                                TextureAtlasSprite particle,
                                VertexFormat format,
                                ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms,
                                Map<String, IBakedModel> cache,
                                boolean untransformed) {
            super(quads, particle, transforms, new BakedFluidBottleOverrideHandler(bakery), untransformed);
            this.parent = parent;
            this.cache = cache;
            this.format = format;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
            return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType);
        }

        private static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> itemTransforms() {
            TRSRTransformation thirdperson = get(0, 3, 1, 0, 0, 0, 0.55f);
            TRSRTransformation firstperson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
            ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
            builder.put(ItemCameraTransforms.TransformType.GROUND, get(0, 2, 0, 0, 0, 0, 0.5f));
            builder.put(ItemCameraTransforms.TransformType.HEAD, get(0, 13, 7, 0, 180, 0, 1));
            builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
            builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson));
            builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, firstperson);
            builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstperson));
            return (ImmutableMap) builder.build();
        }

        private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
            return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(new Vector3f(tx / 16, ty / 16, tz / 16),
                    TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), null));
        }

        private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1),
                null);

        private static TRSRTransformation leftify(TRSRTransformation transform) {
            return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
            if (side == null)
                return quads;
            return ImmutableList.of();
        }

        public boolean isAmbientOcclusion() {
            return true;
        }

        public boolean isGui3d() {
            return false;
        }

        public boolean isBuiltInRenderer() {
            return false;
        }

        public TextureAtlasSprite getParticleTexture() {
            return particle;
        }

        public ItemCameraTransforms getItemCameraTransforms() {
            return ItemCameraTransforms.DEFAULT;
        }
    }
}
