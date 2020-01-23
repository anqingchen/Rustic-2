package com.samaritans.rustic.item;

import com.samaritans.rustic.alchemy.AlchemySpell;
import com.samaritans.rustic.alchemy.ICastingItem;
import com.samaritans.rustic.entity.LuteSpellEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class LuteItem extends Item implements ICastingItem {

	public LuteItem(Properties properties) {
		super(properties);
	}
	
	// TODO remove
	@Override
	public boolean opensRadialFromInventory() {
		return false;
	}
	
	
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		AlchemySpell spell = ICastingItem.getSelectedSpell(stack);
		System.out.println(spell);
		if (spell != null && !spell.isEmpty()) {
			
			// TODO use spell catalyst, unless in creative mod
			// TODO play sound
			// TODO change how usage works
			// TODO swing arm when casting
			// TODO add cooldown?
			
			LuteSpellEntity ent = new LuteSpellEntity(player, 1.0f, world).setSpell(spell);
			ent.shoot(player, player.rotationPitch, player.rotationYaw, 0.0f, 0.5f, 0.05f);
			world.addEntity(ent);
			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}
		return new ActionResult<>(ActionResultType.PASS, stack);
	}
	
	
}
