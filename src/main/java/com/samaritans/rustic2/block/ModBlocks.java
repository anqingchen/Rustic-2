package com.samaritans.rustic2.block;

import com.samaritans.rustic2.Rustic2;
import com.samaritans.rustic2.Util;
import com.samaritans.rustic2.client.renderer.CabinetTileEntityRenderer;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.WallOrFloorItem;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Rustic2.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Rustic2.MODID)
public class ModBlocks {
    public static final Block slate = null;
    public static final Block slate_roof = null;
    public static final Block slate_roof_stairs = null;
    public static final Block slate_roof_slab = null;
    public static final Block slate_tile = null;
    public static final Block slate_bricks = null;
    public static final Block slate_bricks_stairs = null;
    public static final Block slate_bricks_slab = null;
    public static final Block chiseled_slate = null;
    public static final Block slate_pillar = null;
    public static final Block stone_pillar = null;
    public static final Block andesite_pillar = null;
    public static final Block diorite_pillar = null;
    public static final Block granite_pillar = null;
    public static final Block candle = null;
    public static final Block wall_candle = null;
    public static final Block rope = null;
    public static final Block stake = null;
    public static final Block chandelier = null;

    public static final Block acacia_table = null;
    public static final Block birch_table = null;
    public static final Block dark_oak_table = null;
    public static final Block jungle_table = null;
    public static final Block oak_table = null;
    public static final Block spruce_table = null;

    public static final Block acacia_chair = null;
    public static final Block birch_chair = null;
    public static final Block dark_oak_chair = null;
    public static final Block jungle_chair = null;
    public static final Block oak_chair = null;
    public static final Block spruce_chair = null;

    public static final Block acacia_cabinet = null;
    public static final Block birch_cabinet = null;
    public static final Block dark_oak_cabinet = null;
    public static final Block jungle_cabinet = null;
    public static final Block oak_cabinet = null;
    public static final Block spruce_cabinet = null;

    public static final Block pot = null;

    public static final Block crushing_tub = null;

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        final Block.Properties ROCK_PROPERTIES = Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F).sound(SoundType.STONE);
        final Block.Properties PLANKS_PROPERTIES = Block.Properties.create(Material.WOOD).hardnessAndResistance(2.0F).harvestTool(ToolType.AXE).sound(SoundType.WOOD);
        event.getRegistry().registerAll(
                Util.setup(new Block(ROCK_PROPERTIES), "slate"),
                Util.setup(new Block(ROCK_PROPERTIES), "slate_roof"),
                Util.setup(new StairsBlock(() -> ModBlocks.slate_roof.getDefaultState(), ROCK_PROPERTIES), "slate_roof_stairs"),
                Util.setup(new SlabBlock(ROCK_PROPERTIES), "slate_roof_slab"),
                Util.setup(new Block(ROCK_PROPERTIES), "slate_tile"),
                Util.setup(new Block(ROCK_PROPERTIES), "slate_bricks"),
                Util.setup(new StairsBlock(() -> ModBlocks.slate_bricks.getDefaultState(), ROCK_PROPERTIES), "slate_bricks_stairs"),
                Util.setup(new SlabBlock(ROCK_PROPERTIES), "slate_bricks_slab"),
                Util.setup(new Block(ROCK_PROPERTIES), "chiseled_slate"),
                Util.setup(new RotatedPillarBlock(ROCK_PROPERTIES), "slate_pillar"),
                Util.setup(new RotatedPillarBlock(ROCK_PROPERTIES), "stone_pillar"),
                Util.setup(new RotatedPillarBlock(ROCK_PROPERTIES), "andesite_pillar"),
                Util.setup(new RotatedPillarBlock(ROCK_PROPERTIES), "diorite_pillar"),
                Util.setup(new RotatedPillarBlock(ROCK_PROPERTIES), "granite_pillar"),
                Util.setup(new CandleBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).lightValue(14)), "candle"),
                Util.setup(new WallCandleBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).lightValue(14)), "wall_candle"),
                Util.setup(new RopeBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0)), "rope"),
                Util.setup(new StakeBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.0F)), "stake"),
                Util.setup(new ChandelierBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F, 1200.0F).harvestTool(ToolType.PICKAXE)), "chandelier"),
                // todo: eventually add rustic woods to this list
                // Tables
                Util.setup(new TableBlock(PLANKS_PROPERTIES), "acacia_table"),
                Util.setup(new TableBlock(PLANKS_PROPERTIES), "birch_table"),
                Util.setup(new TableBlock(PLANKS_PROPERTIES), "dark_oak_table"),
                Util.setup(new TableBlock(PLANKS_PROPERTIES), "jungle_table"),
                Util.setup(new TableBlock(PLANKS_PROPERTIES), "oak_table"),
                Util.setup(new TableBlock(PLANKS_PROPERTIES), "spruce_table"),
                // Chairs
                Util.setup(new ChairBlock(PLANKS_PROPERTIES), "acacia_chair"),
                Util.setup(new ChairBlock(PLANKS_PROPERTIES), "birch_chair"),
                Util.setup(new ChairBlock(PLANKS_PROPERTIES), "dark_oak_chair"),
                Util.setup(new ChairBlock(PLANKS_PROPERTIES), "jungle_chair"),
                Util.setup(new ChairBlock(PLANKS_PROPERTIES), "oak_chair"),
                Util.setup(new ChairBlock(PLANKS_PROPERTIES), "spruce_chair"),
                // Cabinets
                Util.setup(new CabinetBlock(PLANKS_PROPERTIES), "acacia_cabinet"),
                Util.setup(new CabinetBlock(PLANKS_PROPERTIES), "birch_cabinet"),
                Util.setup(new CabinetBlock(PLANKS_PROPERTIES), "dark_oak_cabinet"),
                Util.setup(new CabinetBlock(PLANKS_PROPERTIES), "jungle_cabinet"),
                Util.setup(new CabinetBlock(PLANKS_PROPERTIES), "oak_cabinet"),
                Util.setup(new CabinetBlock(PLANKS_PROPERTIES), "spruce_cabinet"),
                // todo: more pot variants
                // Pots
                Util.setup(new PotBlock(ROCK_PROPERTIES), "pot"),

                Util.setup(new CrushingTubBlock(PLANKS_PROPERTIES), "crushing_tub")
        );
    }

    @SubscribeEvent
    public static void onRegisterBlockItems(final RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        final Item.Properties properties = new Item.Properties().group(Rustic2.TAB);
        ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block.getRegistryName().getNamespace().equals(Rustic2.MODID))
                .filter(block -> !(block instanceof CandleBlock) && !(block instanceof CabinetBlock)).forEach(block -> {
                    final BlockItem blockItem = new BlockItem(block, properties);
                    registry.register(Util.setup(blockItem, block.getRegistryName()));
                });

        // Register WallOrFloorItem
        registry.register(Util.setup(new WallOrFloorItem(candle, wall_candle, properties), "candle"));

        // Register TEISR Items
        ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block instanceof CabinetBlock).forEach(block -> {
            final BlockItem blockItem = new BlockItem(block, new Item.Properties().setTEISR(() -> CabinetTileEntityRenderer.TEISR::new).group(Rustic2.TAB));
            registry.register(Util.setup(blockItem, block.getRegistryName()));
        });

    }
}
