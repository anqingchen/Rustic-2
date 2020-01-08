package com.samaritans.rustic.block;

import com.samaritans.rustic.Rustic;
import com.samaritans.rustic.Util;
import com.samaritans.rustic.block.trees.IronwoodTree;
import com.samaritans.rustic.block.trees.YewTree;
import com.samaritans.rustic.block.util.*;
import com.samaritans.rustic.client.renderer.CabinetTileEntityRenderer;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.AxeItem;
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

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Rustic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Rustic.MODID)
public class ModBlocks {
    public static final Block SLATE = null;
    public static final Block SLATE_ROOF = null;
    public static final Block SLATE_ROOF_STAIRS = null;
    public static final Block SLATE_ROOF_SLAB = null;
    public static final Block SLATE_TILE = null;
    public static final Block SLATE_BRICKS = null;
    public static final Block SLATE_BRICKS_STAIRS = null;
    public static final Block SLATE_BRICKS_SLAB = null;
    public static final Block CHISELED_SLATE = null;
    public static final Block SLATE_PILLAR = null;
    public static final Block STONE_PILLAR = null;
    public static final Block ANDESITE_PILLAR = null;
    public static final Block DIORITE_PILLAR = null;
    public static final Block GRANITE_PILLAR = null;
    public static final Block CANDLE = null;
    public static final Block WALL_CANDLE = null;
    public static final Block GOLD_CANDLE = null;
    public static final Block GOLD_WALL_CANDLE = null;
    public static final Block LANTERN = null;
    public static final Block WALL_LANTERN = null;
    public static final Block GOLD_LANTERN = null;
    public static final Block GOLD_WALL_LANTERN = null;
    public static final Block ROPE = null;
    public static final Block STAKE = null;
    public static final Block CHANDELIER = null;
    public static final Block GOLD_CHANDELIER = null;
    public static final Block LATTICE = null;
    public static final Block FERTILE_SOIL = null;

    public static final Block IRONWOOD_SAPLING = null;
    public static final Block IRONWOOD_LOG = null;
    public static final Block STRIPPED_IRONWOOD_LOG = null;
    public static final Block IRONWOOD_WOOD = null;
    public static final Block STRIPPED_IRONWOOD_WOOD = null;
    public static final Block IRONWOOD_PLANKS = null;
    public static final Block IRONWOOD_SLAB = null;
    public static final Block IRONWOOD_STAIRS = null;
    public static final Block IRONWOOD_BUTTON = null;
    public static final Block IRONWOOD_PRESSURE_PLATE = null;
    public static final Block IRONWOOD_LEAVES = null;
    public static final Block IRONWOOD_FENCE = null;
    public static final Block IRONWOOD_FENCE_GATE = null;
    public static final Block IRONWOOD_TRAPDOOR = null;
    public static final Block IRONWOOD_DOOR = null;
//    public static final Block ironwood_sign = null;
//    public static final Block ironwood_wall_sign = null;

    public static final Block YEW_SAPLING = null;
    public static final Block YEW_LOG = null;
    public static final Block STRIPPED_YEW_LOG = null;
    public static final Block YEW_WOOD = null;
    public static final Block STRIPPED_YEW_WOOD = null;
    public static final Block YEW_PLANKS = null;
    public static final Block YEW_SLAB = null;
    public static final Block YEW_STAIRS = null;
    public static final Block YEW_BUTTON = null;
    public static final Block YEW_PRESSURE_PLATE = null;
    public static final Block YEW_LEAVES = null;
    public static final Block YEW_FENCE = null;
    public static final Block YEW_FENCE_GATE = null;
    public static final Block YEW_TRAPDOOR = null;
    public static final Block YEW_DOOR = null;

    public static final Block ACACIA_TABLE = null;
    public static final Block BIRCH_TABLE = null;
    public static final Block DARK_OAK_TABLE = null;
    public static final Block JUNGLE_TABLE = null;
    public static final Block OAK_TABLE = null;
    public static final Block SPRUCE_TABLE = null;
    public static final Block IRONWOOD_TABLE = null;

    public static final Block ACACIA_CHAIR = null;
    public static final Block BIRCH_CHAIR = null;
    public static final Block DARK_OAK_CHAIR = null;
    public static final Block JUNGLE_CHAIR = null;
    public static final Block OAK_CHAIR = null;
    public static final Block SPRUCE_CHAIR = null;
    public static final Block IRONWOOD_CHAIR = null;

    public static final Block ACACIA_CABINET = null;
    public static final Block BIRCH_CABINET = null;
    public static final Block DARK_OAK_CABINET = null;
    public static final Block JUNGLE_CABINET = null;
    public static final Block OAK_CABINET = null;
    public static final Block SPRUCE_CABINET = null;
    public static final Block IRONWOOD_CABINET = null;

    public static final Block POT0 = null;
    public static final Block POT1 = null;
    public static final Block POT2 = null;
    public static final Block POT3 = null;
    public static final Block POT4 = null;
    public static final Block POT5 = null;
    public static final Block POT6 = null;
    public static final Block POT7 = null;
    public static final Block POT8 = null;
    public static final Block POT9 = null;
    public static final Block POT10 = null;
    public static final Block POT11 = null;

    public static final Block CRUSHING_TUB = null;

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        final Block.Properties ROCK_PROPERTIES = Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F).sound(SoundType.STONE);
        final Block.Properties PLANKS_PROPERTIES = Block.Properties.create(Material.WOOD).hardnessAndResistance(2.0F, 3.0F).harvestTool(ToolType.AXE).sound(SoundType.WOOD);
        final Block.Properties LOG_PROPERTIES = Block.Properties.create(Material.WOOD).hardnessAndResistance(2.0F).harvestTool(ToolType.AXE).sound(SoundType.WOOD);
        event.getRegistry().registerAll(
                Util.setup(new Block(ROCK_PROPERTIES), "slate"),
                Util.setup(new Block(ROCK_PROPERTIES), "slate_roof"),
                Util.setup(new StairsBlock(() -> ModBlocks.SLATE_ROOF.getDefaultState(), ROCK_PROPERTIES), "slate_roof_stairs"),
                Util.setup(new SlabBlock(ROCK_PROPERTIES), "slate_roof_slab"),
                Util.setup(new Block(ROCK_PROPERTIES), "slate_tile"),
                Util.setup(new Block(ROCK_PROPERTIES), "slate_bricks"),
                Util.setup(new StairsBlock(() -> ModBlocks.SLATE_BRICKS.getDefaultState(), ROCK_PROPERTIES), "slate_bricks_stairs"),
                Util.setup(new SlabBlock(ROCK_PROPERTIES), "slate_bricks_slab"),
                Util.setup(new Block(ROCK_PROPERTIES), "chiseled_slate"),
                Util.setup(new RotatedPillarBlock(ROCK_PROPERTIES), "slate_pillar"),
                Util.setup(new RotatedPillarBlock(ROCK_PROPERTIES), "stone_pillar"),
                Util.setup(new RotatedPillarBlock(ROCK_PROPERTIES), "andesite_pillar"),
                Util.setup(new RotatedPillarBlock(ROCK_PROPERTIES), "diorite_pillar"),
                Util.setup(new RotatedPillarBlock(ROCK_PROPERTIES), "granite_pillar"),
                Util.setup(new CandleBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).lightValue(14)), "candle"),
                Util.setup(new WallCandleBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).lightValue(14)), "wall_candle"),
                Util.setup(new CandleBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).lightValue(14)), "gold_candle"),
                Util.setup(new WallCandleBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).lightValue(14)), "gold_wall_candle"),
                Util.setup(new LanternBlock(Block.Properties.create(Material.IRON).doesNotBlockMovement().hardnessAndResistance(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).lightValue(14)), "lantern"),
                Util.setup(new WallLanternBlock(Block.Properties.create(Material.IRON).doesNotBlockMovement().hardnessAndResistance(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).lightValue(14)), "wall_lantern"),
                Util.setup(new LanternBlock(Block.Properties.create(Material.IRON).doesNotBlockMovement().hardnessAndResistance(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).lightValue(14)), "gold_lantern"),
                Util.setup(new WallLanternBlock(Block.Properties.create(Material.IRON).doesNotBlockMovement().hardnessAndResistance(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).lightValue(14)), "gold_wall_lantern"),
                Util.setup(new RopeBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0)), "rope"),
                Util.setup(new StakeBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.0F)), "stake"),
                Util.setup(new ChandelierBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F, 1200.0F).harvestTool(ToolType.PICKAXE)), "chandelier"),
                Util.setup(new ChandelierBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F, 1200.0F).harvestTool(ToolType.PICKAXE)), "gold_chandelier"),
                Util.setup(new LatticeBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F).harvestTool(ToolType.PICKAXE)), "lattice"),
                Util.setup(new FertileSoilBlock(Block.Properties.create(Material.EARTH).harvestTool(ToolType.SHOVEL).hardnessAndResistance(0.5F).sound(SoundType.GROUND)), "fertile_soil"),

                Util.setup(new ModSaplingBlock(new IronwoodTree(), Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0).sound(SoundType.PLANT)), "ironwood_sapling"),
                Util.setup(new LogBlock(MaterialColor.IRON, LOG_PROPERTIES), "ironwood_log"),
                Util.setup(new LogBlock(MaterialColor.IRON, LOG_PROPERTIES), "stripped_ironwood_log"),
                Util.setup(new RotatedPillarBlock(LOG_PROPERTIES), "ironwood_wood"),
                Util.setup(new RotatedPillarBlock(LOG_PROPERTIES), "stripped_ironwood_wood"),
                Util.setup(new Block(PLANKS_PROPERTIES), "ironwood_planks"),
                Util.setup(new SlabBlock(PLANKS_PROPERTIES), "ironwood_slab"),
                Util.setup(new StairsBlock(() -> ModBlocks.IRONWOOD_PLANKS.getDefaultState(), PLANKS_PROPERTIES), "ironwood_stairs"),
                Util.setup(new ModWoodButtonBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)), "ironwood_button"),
                Util.setup(new ModPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)), "ironwood_pressure_plate"),
                Util.setup(new LeavesBlock(Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT)), "ironwood_leaves"),
                Util.setup(new FenceBlock(PLANKS_PROPERTIES), "ironwood_fence"),
                Util.setup(new FenceGateBlock(PLANKS_PROPERTIES), "ironwood_fence_gate"),
                Util.setup(new ModTrapDoorBlock(PLANKS_PROPERTIES), "ironwood_trapdoor"),
                Util.setup(new ModDoorBlock(PLANKS_PROPERTIES), "ironwood_door"),
//                Util.setup(new StandingSignBlock(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)), "ironwood_sign"),
//                Util.setup(new WallSignBlock(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)), "ironwood_wall_sign"),

                Util.setup(new ModSaplingBlock(new YewTree(), Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0).sound(SoundType.PLANT)), "yew_sapling"),
                Util.setup(new LogBlock(MaterialColor.IRON, LOG_PROPERTIES), "yew_log"),
                Util.setup(new LogBlock(MaterialColor.IRON, LOG_PROPERTIES), "stripped_yew_log"),
                Util.setup(new RotatedPillarBlock(LOG_PROPERTIES), "yew_wood"),
                Util.setup(new RotatedPillarBlock(LOG_PROPERTIES), "stripped_yew_wood"),
                Util.setup(new Block(PLANKS_PROPERTIES), "yew_planks"),
                Util.setup(new SlabBlock(PLANKS_PROPERTIES), "yew_slab"),
                Util.setup(new StairsBlock(() -> ModBlocks.IRONWOOD_PLANKS.getDefaultState(), PLANKS_PROPERTIES), "yew_stairs"),
                Util.setup(new ModWoodButtonBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)), "yew_button"),
                Util.setup(new ModPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)), "yew_pressure_plate"),
                Util.setup(new LeavesBlock(Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT)), "yew_leaves"),
                Util.setup(new FenceBlock(PLANKS_PROPERTIES), "yew_fence"),
                Util.setup(new FenceGateBlock(PLANKS_PROPERTIES), "yew_fence_gate"),
                Util.setup(new ModTrapDoorBlock(PLANKS_PROPERTIES), "yew_trapdoor"),
                Util.setup(new ModDoorBlock(PLANKS_PROPERTIES), "yew_door"),

                // todo: eventually add rustic woods to this list
                // Tables
                Util.setup(new TableBlock(PLANKS_PROPERTIES), "acacia_table"),
                Util.setup(new TableBlock(PLANKS_PROPERTIES), "birch_table"),
                Util.setup(new TableBlock(PLANKS_PROPERTIES), "dark_oak_table"),
                Util.setup(new TableBlock(PLANKS_PROPERTIES), "jungle_table"),
                Util.setup(new TableBlock(PLANKS_PROPERTIES), "oak_table"),
                Util.setup(new TableBlock(PLANKS_PROPERTIES), "spruce_table"),
                Util.setup(new TableBlock(PLANKS_PROPERTIES), "ironwood_table"),
                // Chairs
                Util.setup(new ChairBlock(PLANKS_PROPERTIES), "acacia_chair"),
                Util.setup(new ChairBlock(PLANKS_PROPERTIES), "birch_chair"),
                Util.setup(new ChairBlock(PLANKS_PROPERTIES), "dark_oak_chair"),
                Util.setup(new ChairBlock(PLANKS_PROPERTIES), "jungle_chair"),
                Util.setup(new ChairBlock(PLANKS_PROPERTIES), "oak_chair"),
                Util.setup(new ChairBlock(PLANKS_PROPERTIES), "spruce_chair"),
                Util.setup(new ChairBlock(PLANKS_PROPERTIES), "ironwood_chair"),
                // Cabinets
                Util.setup(new CabinetBlock(PLANKS_PROPERTIES), "acacia_cabinet"),
                Util.setup(new CabinetBlock(PLANKS_PROPERTIES), "birch_cabinet"),
                Util.setup(new CabinetBlock(PLANKS_PROPERTIES), "dark_oak_cabinet"),
                Util.setup(new CabinetBlock(PLANKS_PROPERTIES), "jungle_cabinet"),
                Util.setup(new CabinetBlock(PLANKS_PROPERTIES), "oak_cabinet"),
                Util.setup(new CabinetBlock(PLANKS_PROPERTIES), "spruce_cabinet"),
                Util.setup(new CabinetBlock(PLANKS_PROPERTIES), "ironwood_cabinet"),

                // Pots
                Util.setup(new PotBlock(ROCK_PROPERTIES), "pot0"),
                Util.setup(new PotBlock(ROCK_PROPERTIES), "pot1"),
                Util.setup(new PotBlock(ROCK_PROPERTIES), "pot2"),
                Util.setup(new PotBlock(ROCK_PROPERTIES), "pot3"),
                Util.setup(new PotBlock(ROCK_PROPERTIES), "pot4"),
                Util.setup(new PotBlock(ROCK_PROPERTIES), "pot5"),
                Util.setup(new PotBlock(ROCK_PROPERTIES), "pot6"),
                Util.setup(new PotBlock(ROCK_PROPERTIES), "pot7"),
                Util.setup(new PotBlock(ROCK_PROPERTIES), "pot8"),
                Util.setup(new PotBlock(ROCK_PROPERTIES), "pot9"),
                Util.setup(new PotBlock(ROCK_PROPERTIES), "pot10"),
                Util.setup(new PotBlock(ROCK_PROPERTIES), "pot11"),

                Util.setup(new CrushingTubBlock(PLANKS_PROPERTIES), "crushing_tub")
        );
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void onRegisterBlockItems(final RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        final Item.Properties properties = new Item.Properties().group(Rustic.TAB);
        ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block.getRegistryName().getNamespace().equals(Rustic.MODID))
                .filter(block -> !(block instanceof CandleBlock) && !(block instanceof CabinetBlock) && !(block instanceof FlowingFluidBlock)
                        && (block != IRONWOOD_WOOD) && (block != STRIPPED_IRONWOOD_WOOD) && !(block instanceof AbstractSignBlock)).forEach(block -> {
            final BlockItem blockItem = new BlockItem(block, properties);
            registry.register(Util.setup(blockItem, block.getRegistryName()));
        });

        // Register WallOrFloorItem
        registry.register(Util.setup(new WallOrFloorItem(CANDLE, WALL_CANDLE, properties), "candle"));
        registry.register(Util.setup(new WallOrFloorItem(GOLD_CANDLE, GOLD_WALL_CANDLE, properties), "gold_candle"));
        registry.register(Util.setup(new WallOrFloorItem(LANTERN, WALL_LANTERN, properties), "lantern"));
        registry.register(Util.setup(new WallOrFloorItem(GOLD_LANTERN, GOLD_WALL_LANTERN, properties), "gold_lantern"));

        // Register Signs
//        registry.register(Util.setup(new SignItem(new Item.Properties().maxStackSize(16).group(ItemGroup.DECORATIONS), ModBlocks.ironwood_sign, ModBlocks.ironwood_wall_sign), "ironwood_sign"));

        // Register TEISR Items
        ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block instanceof CabinetBlock).forEach(block -> {
            final BlockItem blockItem = new BlockItem(block, new Item.Properties().setTEISR(() -> CabinetTileEntityRenderer.TEISR::new).group(Rustic.TAB));
            registry.register(Util.setup(blockItem, block.getRegistryName()));
        });

        // Adding ironwood to Axe Stripping, might need to move this somewhere better
        Map<Block, Block> map = new HashMap<>(AxeItem.BLOCK_STRIPPING_MAP);
        map.put(ModBlocks.IRONWOOD_LOG, ModBlocks.STRIPPED_IRONWOOD_LOG);
        map.put(ModBlocks.IRONWOOD_WOOD, ModBlocks.STRIPPED_IRONWOOD_WOOD);
        map.put(ModBlocks.YEW_LOG, ModBlocks.STRIPPED_YEW_LOG);
        map.put(ModBlocks.YEW_WOOD, ModBlocks.STRIPPED_YEW_WOOD);
        AxeItem.BLOCK_STRIPPING_MAP = map;
    }
}
