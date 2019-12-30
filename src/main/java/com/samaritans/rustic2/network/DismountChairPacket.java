package com.samaritans.rustic2.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class DismountChairPacket {

    public DismountChairPacket() {

    }

    public DismountChairPacket(PacketBuffer buf) {

    }

    public static DismountChairPacket decode(PacketBuffer buf) {
        return new DismountChairPacket();
    }

    public void encode(PacketBuffer buf) {

    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null) {
                player.stopRiding();
            }
        });
        context.get().setPacketHandled(true);
    }
}
