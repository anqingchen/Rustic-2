package com.samaritans.rustic.network;

import java.util.function.Supplier;

import com.samaritans.rustic.alchemy.AlchemySpell;
import com.samaritans.rustic.alchemy.ICastingItem;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SpellSelectPacket {
	
	private final int slot;
	private final boolean isCurioSlot;
	private final AlchemySpell spell;
	
	public SpellSelectPacket(int slot, boolean isCurioSlot, AlchemySpell spell) {
		this.slot = slot;
		this.isCurioSlot = isCurioSlot;
		this.spell = (spell != null) ? spell : new AlchemySpell(null, 0, 0);
	}
	
	public static SpellSelectPacket decode(PacketBuffer buf) {
		return new SpellSelectPacket(buf.readVarInt(), buf.readBoolean(), AlchemySpell.read(buf));
	}
	
	public void encode(PacketBuffer buf) {
		buf.writeVarInt(this.slot);
		buf.writeBoolean(this.isCurioSlot);
		this.spell.write(buf);
	}
	
	public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player == null) return;
            if (!this.isCurioSlot) {
            	ItemStack stack = player.inventory.getStackInSlot(this.slot);
            	ICastingItem.setSelectedSpell(this.spell, stack);
            } else {            	
            	// TODO update selected spell of stack in curio slot
            }
        });
        context.get().setPacketHandled(true);
    }
	
}
