package com.samaritans.rustic2.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class DismountChairMessage implements IMessage {

    public DismountChairMessage() {

    }

    public DismountChairMessage(PacketBuffer buf) {

    }

    public void encode(PacketBuffer buf) {

    }

    public static DismountChairMessage decode(PacketBuffer buf) {
        return new DismountChairMessage();
    }

    public static void handle(IMessage msg, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
           ServerPlayerEntity player = context.get().getSender();
           if (player != null) {
               player.stopRiding();
           }
        });
        context.get().setPacketHandled(true);
    }
}
