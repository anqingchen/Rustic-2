package com.samaritans.rustic.alchemy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public interface ICastingItem {
	
	public default boolean opensRadialFromInventory() {
		return false;
	}
	
	public default boolean opensRadialFromInventory(ItemStack stack) {
		return this.opensRadialFromInventory();
	}
	
	public default NonNullList<ItemStack> findCatalystsForRadial(ItemStack stack, PlayerEntity player) {
		NonNullList<ItemStack> list = NonNullList.create();
		if (stack.isEmpty()) return list;
		ItemStack[] catalysts = ISpellCatalystItem.findAllSpellCatalysts(player);
		for (ItemStack c : catalysts)
			list.add(c);
		if (!list.isEmpty()) {
			AlchemySpell spell = getSelectedSpell(stack);
			if (spell != null && !spell.isEmpty())
				list.add(0, ItemStack.EMPTY);
		}
		return list;
	}
	
	public static AlchemySpell getSelectedSpell(ItemStack stack) {
		if (!stack.isEmpty() && (stack.getItem() instanceof ICastingItem)) {
			CompoundNBT spellTag = stack.getChildTag("selectedAlchemySpell");
			if (spellTag != null) return  AlchemySpell.read(spellTag);
		}
		return null;
	}
	
	public static void setSelectedSpell(AlchemySpell spell, ItemStack stack) {
		if (!stack.isEmpty() && (stack.getItem() instanceof ICastingItem)) {
			if (spell == null || spell.isEmpty()) {
				stack.removeChildTag("selectedAlchemySpell");
			} else {
				stack.setTagInfo("selectedAlchemySpell", spell.toNBT());
			}
		}
	}

}
