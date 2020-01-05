package com.samaritans.rustic;

import com.samaritans.rustic.block.ModBlocks;
import com.samaritans.rustic.client.renderer.CabinetTileEntityRenderer;
import com.samaritans.rustic.client.renderer.CrushingTubTileEntityRenderer;
import com.samaritans.rustic.client.renderer.PotTileEntityRenderer;
import com.samaritans.rustic.crafting.SyncHandler;
import com.samaritans.rustic.item.ModItems;
import com.samaritans.rustic.network.PacketHandler;
import com.samaritans.rustic.tileentity.CabinetTileEntity;
import com.samaritans.rustic.tileentity.CrushingTubTileEntity;
import com.samaritans.rustic.tileentity.PotTileEntity;
import com.samaritans.rustic.world.ModWorldGen;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Rustic.MODID)
public class Rustic {
    public static final String MODID = "rustic";
    public static final ItemGroup TAB = new ItemGroup(MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.SLATE);
        }
    };
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public Rustic() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the setup method for modloading
        modEventBus.addListener(this::setup);
        // Register the enqueueIMC method for modloading
        modEventBus.addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        modEventBus.addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        modEventBus.addListener(this::doClientStuff);

        ModItems.ITEMS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ModEventHandler.class);

        MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        PacketHandler.registerMessages();

        ModWorldGen.setupTreeGen();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        ClientRegistry.bindTileEntitySpecialRenderer(CabinetTileEntity.class, new CabinetTileEntityRenderer<>());
        ClientRegistry.bindTileEntitySpecialRenderer(CrushingTubTileEntity.class, new CrushingTubTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(PotTileEntity.class, new PotTileEntityRenderer());

        ModColorManager.register();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        // InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
//        LOGGER.info("Got IMC {}", event.getIMCStream().
//                map(m->m.getMessageSupplier().get()).
//                collect(Collectors.toList()));
    }

    private void serverAboutToStart(FMLServerAboutToStartEvent event) {
        event.getServer().getResourceManager().addReloadListener(new SyncHandler.ReloadListener());
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
//        LOGGER.info("HELLO from server starting");
    }
}
