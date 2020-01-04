package com.samaritans.rustic.network;

import com.samaritans.rustic.Rustic;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Rustic.MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void registerMessages() {
        int id = 0;
        HANDLER.registerMessage(id++, DismountChairPacket.class, DismountChairPacket::encode, DismountChairPacket::decode, DismountChairPacket::handle);
        HANDLER.registerMessage(id++, SyncRecipesPacket.class, SyncRecipesPacket::encode, SyncRecipesPacket::decode, SyncRecipesPacket::handle);
    }

    public static void sendToServer(Object msg) {
        HANDLER.sendToServer(msg);
    }

    public static void sendTo(Object msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer)) {
            HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void sendNonLocal(Object toSend, ServerPlayerEntity playerMP) {
        if (playerMP.server.isDedicatedServer() || !playerMP.getGameProfile().getName().equals(playerMP.server.getServerOwner())) {
            sendTo(toSend, playerMP);
        }
    }
}
