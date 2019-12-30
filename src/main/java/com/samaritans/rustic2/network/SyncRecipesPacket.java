package com.samaritans.rustic2.network;

import com.samaritans.rustic2.Rustic2API;
import com.samaritans.rustic2.crafting.CrushingTubRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This part of the code that handles Recipes are mimicked from Botania,
 * All credits to Vazkii
 * https://github.com/Vazkii/Botania
 */

public class SyncRecipesPacket {
    private Map<ResourceLocation, CrushingTubRecipe> crushingTub;

    public SyncRecipesPacket(Map<ResourceLocation, CrushingTubRecipe> crushingTub) {
        this.crushingTub = crushingTub;
    }

    public static SyncRecipesPacket decode(PacketBuffer buf) {
        int crushingCount = buf.readVarInt();
        Map<ResourceLocation, CrushingTubRecipe> crushingTub = Stream.generate(() -> CrushingTubRecipe.read(buf))
                .limit(crushingCount)
                .collect(Collectors.toMap(CrushingTubRecipe::getId, r -> r));
        return new SyncRecipesPacket(crushingTub);
    }

    public void encode(PacketBuffer buf) {
        buf.writeVarInt(crushingTub.size());
        for (CrushingTubRecipe recipe : crushingTub.values()) {
            recipe.write(buf);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Rustic2API.crushingTubRecipes = crushingTub;
        });
        ctx.get().setPacketHandled(true);
    }
}
