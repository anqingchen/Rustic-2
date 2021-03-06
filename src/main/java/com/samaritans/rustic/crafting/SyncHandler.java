package com.samaritans.rustic.crafting;

import com.google.common.collect.ImmutableMap;
import com.samaritans.rustic.Rustic;
import com.samaritans.rustic.RusticAPI;
import com.samaritans.rustic.network.PacketHandler;
import com.samaritans.rustic.network.SyncRecipesPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This part of the code that handles Recipes are mimicked from Botania,
 * All credits to Vazkii
 * https://github.com/Vazkii/Botania
 */

public class SyncHandler {
    private static SyncRecipesPacket syncPacket() {
        return new SyncRecipesPacket(RusticAPI.crushingTubRecipes);
    }

    public static class ReloadListener implements IResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
            Map<ResourceLocation, CrushingTubRecipe> crushingTub = new HashMap<>();

            RegisterRecipesEvent event = new RegisterRecipesEvent(
                    r -> crushingTub.put(r.getId(), r)
            );
            MinecraftForge.EVENT_BUS.post(event);

            RusticAPI.crushingTubRecipes = ImmutableMap.copyOf(crushingTub);

            PacketHandler.HANDLER.send(PacketDistributor.ALL.noArg(), syncPacket());
        }
    }

    @Mod.EventBusSubscriber(modid = Rustic.MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void clientLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
            RusticAPI.crushingTubRecipes = Collections.emptyMap();
        }
    }

    @Mod.EventBusSubscriber(modid = Rustic.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void serverLogin(PlayerEvent.PlayerLoggedInEvent event) {
            PacketHandler.sendNonLocal(syncPacket(), (ServerPlayerEntity) event.getPlayer());
        }
    }
}
