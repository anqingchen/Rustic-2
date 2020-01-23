package com.samaritans.rustic.alchemy;

import java.util.Comparator;
import java.util.TreeSet;

import javax.annotation.Nullable;

import com.samaritans.rustic.item.ModItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public interface ISpellCatalystItem {
	
	static Comparator<ItemStack> CATALYST_ITEMSTACK_COMPARATOR = (ItemStack a, ItemStack b) -> {
		if (!a.isEmpty() && !b.isEmpty() && (a.getItem() instanceof ISpellCatalystItem) && (b.getItem() instanceof ISpellCatalystItem)) {
			AlchemySpell spellA = getAlchemySpell(a), spellB = getAlchemySpell(b);
			if (spellA != null && spellB != null)
				return spellA.compareTo(spellB);
		}
		return 0;
	};
	
	public void depleteSpellCatalyst(ItemStack stack, @Nullable PlayerEntity player);
	
	public static AlchemySpell getAlchemySpell(ItemStack stack) {
		if (!stack.isEmpty() && (stack.getItem() instanceof ISpellCatalystItem)) {
			CompoundNBT spellTag = stack.getChildTag("alchemySpell");
			if (spellTag != null) return AlchemySpell.read(spellTag);
		}
		return null;
	}
	
	public static ItemStack setAlchemySpell(AlchemySpell spell, ItemStack stack) {
		if (!stack.isEmpty() && (stack.getItem() instanceof ISpellCatalystItem)) {
			if (spell == null || spell.isEmpty()) {
				stack.removeChildTag("alchemySpell");
			} else {
				stack.setTagInfo("alchemySpell", spell.toNBT());
			}
		}
		return stack;
	}
	
	public static SpellEffect getSpellEffect(ItemStack stack) {
		AlchemySpell spell = getAlchemySpell(stack);
		return (spell == null) ? null : spell.getSpellEffect();
	}
	
	public static int getPotencyLevel(ItemStack stack) {
		AlchemySpell spell = getAlchemySpell(stack);
		return (spell == null) ? null : spell.getPotencyLevel();
	}
	
	public static int getDurationLevel(ItemStack stack) {
		AlchemySpell spell = getAlchemySpell(stack);
		return (spell == null) ? null : spell.getDurationLevel();
	}
	
	public static boolean matchesSpell(AlchemySpell spell, ItemStack stack) {
		return !stack.isEmpty() &&(stack.getItem() instanceof ISpellCatalystItem) &&
				spell.equals(getAlchemySpell(stack));
	}
	
	public static ItemStack findSpellCatalyst(AlchemySpell spell, PlayerEntity player) {
		if (spell == null) return ItemStack.EMPTY;
		if (player == null || player.abilities.isCreativeMode) {
			return setAlchemySpell(spell, new ItemStack(ModItems.SPELL_CATALYST, 1));
		}
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (matchesSpell(spell, stack))
				return stack;
			// TODO catalyst pouch
		}
		return ItemStack.EMPTY;
	}
	
	public static ItemStack[] findAllSpellCatalysts(PlayerEntity player) {
		TreeSet<ItemStack> catalystSet = new TreeSet<>(CATALYST_ITEMSTACK_COMPARATOR);
		if (player != null) {
			for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if (!stack.isEmpty()) {
					if (stack.getItem() instanceof ISpellCatalystItem) {
						catalystSet.add(stack);
					}
					// TODO catalyst pouch
				}
			}
		}
		return catalystSet.toArray(new ItemStack[catalystSet.size()]);
	}

}
