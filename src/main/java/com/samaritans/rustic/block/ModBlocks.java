package com.samaritans.rustic.block;

import com.samaritans.rustic.Rustic;
import com.samaritans.rustic.Util;
import com.samaritans.rustic.block.util.ModDoorBlock;
import com.samaritans.rustic.block.util.ModPressurePlateBlock;
import com.samaritans.rustic.block.util.ModTrapDoorBlock;
import com.samaritans.rustic.block.util.ModWoodButtonBlock;
import com.samaritans.rustic.client.renderer.CabinetTileEntityRenderer;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.*;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Rustic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Rustic.MODID)
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
    public static final Block gold_candle = null;
    public static final Block gold_wall_candle = null;
    public static final Block lantern = null;
    public static final Block wall_lantern = null;
    public static final Block gold_lantern = null;
    public static final Block gold_wall_lantern = null;
    public static final Block rope = null;
    public static final Block stake = null;
    public static final Block chandelier = null;
    public static final Block gold_chandelier = null;
    public static final Block lattice = null;
    public static final Block fertile_soil = null;

    public static final Block ironwood_log = null;
    public static final Block stripped_ironwood_log = null;
    public static final Block ironwood_wood = null;
    public static final Block stripped_ironwood_wood = null;
    public static final Block ironwood_planks = null;
    public static final Block ironwood_slab = null;
    public static final Block ironwood_stairs = null;
    public static final Block ironwood_button = null;
    public static final Block ironwood_pressure_plate = null;
    public static final Block ironwood_leaves = null;
    public static final Block ironwood_fence = null;
    public static final Block ironwood_fence_gate = null;
    public static final Block ironwood_trapdoor = null;
    public static final Block ironwood_door = null;
//    public static final Block ironwood_sign = null;
//    public static final Block ironwood_wall_sign = null;

    public static final Block acacia_table = null;
    public static final Block birch_table = null;
    public static final Block dark_oak_table = null;
    public static final Block jungle_table = null;
    public static final Block oak_table = null;
    public static final Block spruce_table = null;
    public static final Block ironwood_table = null;

    public static final Block acacia_chair = null;
    public static final Block birch_chair = null;
    public static final Block dark_oak_chair = null;
    public static final Block jungle_chair = null;
    public static final Block oak_chair = null;
    public static final Block spruce_chair = null;
    public static final Block ironwood_chair = null;

    public static final Block acacia_cabinet = null;
    public static final Block birch_cabinet = null;
    public static final Block dark_oak_cabinet = null;
    public static final Block jungle_cabinet = null;
    public static final Block oak_cabinet = null;
    public static final Block spruce_cabinet = null;
    public static final Block ironwood_cabinet = null;

    public static final Block pot0 = null;
    public static final Block pot1 = null;
    public static final Block pot2 = null;
    public static final Block pot3 = null;
    public static final Block pot4 = null;
    public static final Block pot5 = null;
    public static final Block pot6 = null;
    public static final Block pot7 = null;
    public static final Block pot8 = null;
    public static final Block pot9 = null;
    public static final Block pot10 = null;
    public static final Block pot11 = null;

    public static final Block crushing_tub = null;

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        final Block.Properties ROCK_PROPERTIES = Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F).sound(SoundType.STONE);
        final Block.Properties PLANKS_PROPERTIES = Block.Properties.create(Material.WOOD).hardnessAndResistance(2.0F, 3.0F).harvestTool(ToolType.AXE).sound(SoundType.WOOD);
        final Block.Properties LOG_PROPERTIES = Block.Properties.create(Material.WOOD).hardnessAndResistance(2.0F).harvestTool(ToolType.AXE).sound(SoundType.WOOD);
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

                Util.setup(new LogBlock(MaterialColor.IRON, LOG_PROPERTIES), "ironwood_log"),
                Util.setup(new LogBlock(MaterialColor.IRON, LOG_PROPERTIES), "stripped_ironwood_log"),
                Util.setup(new RotatedPillarBlock(LOG_PROPERTIES), "ironwood_wood"),
                Util.setup(new RotatedPillarBlock(LOG_PROPERTIES), "stripped_ironwood_wood"),
                Util.setup(new Block(PLANKS_PROPERTIES), "ironwood_planks"),
                Util.setup(new SlabBlock(PLANKS_PROPERTIES), "ironwood_slab"),
                Util.setup(new StairsBlock(() -> ModBlocks.ironwood_planks.getDefaultState(), PLANKS_PROPERTIES), "ironwood_stairs"),
                Util.setup(new ModWoodButtonBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)), "ironwood_button"),
                Util.setup(new ModPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)), "ironwood_pressure_plate"),
                Util.setup(new LeavesBlock(Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT)), "ironwood_leaves"),
                Util.setup(new FenceBlock(PLANKS_PROPERTIES), "ironwood_fence"),
                Util.setup(new FenceGateBlock(PLANKS_PROPERTIES), "ironwood_fence_gate"),
                Util.setup(new ModTrapDoorBlock(PLANKS_PROPERTIES), "ironwood_trapdoor"),
                Util.setup(new ModDoorBlock(PLANKS_PROPERTIES), "ironwood_door"),
//                Util.setup(new StandingSignBlock(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)), "ironwood_sign"),
//                Util.setup(new WallSignBlock(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)), "ironwood_wall_sign"),

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
                        && (block != ironwood_wood) && (block != stripped_ironwood_wood) && !(block instanceof AbstractSignBlock)).forEach(block -> {
            final BlockItem blockItem = new BlockItem(block, properties);
            registry.register(Util.setup(blockItem, block.getRegistryName()));
        });

        // Register WallOrFloorItem
        registry.register(Util.setup(new WallOrFloorItem(candle, wall_candle, properties), "candle"));
        registry.register(Util.setup(new WallOrFloorItem(gold_candle, gold_wall_candle, properties), "gold_candle"));
        registry.register(Util.setup(new WallOrFloorItem(lantern, wall_lantern, properties), "lantern"));
        registry.register(Util.setup(new WallOrFloorItem(gold_lantern, gold_wall_lantern, properties), "gold_lantern"));

        // Register Signs
//        registry.register(Util.setup(new SignItem(new Item.Properties().maxStackSize(16).group(ItemGroup.DECORATIONS), ModBlocks.ironwood_sign, ModBlocks.ironwood_wall_sign), "ironwood_sign"));

        // Register TEISR Items
        ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block instanceof CabinetBlock).forEach(block -> {
            final BlockItem blockItem = new BlockItem(block, new Item.Properties().setTEISR(() -> CabinetTileEntityRenderer.TEISR::new).group(Rustic.TAB));
            registry.register(Util.setup(blockItem, block.getRegistryName()));
        });
    }
}