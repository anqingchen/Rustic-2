package com.samaritans.rustic.item;

import java.util.List;

import javax.annotation.Nullable;

import com.samaritans.rustic.alchemy.AlchemySpell;
import com.samaritans.rustic.alchemy.ISpellCatalystItem;
import com.samaritans.rustic.alchemy.ModSpells;
import com.samaritans.rustic.alchemy.SpellEffect;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpellCatalystItem extends Item implements ISpellCatalystItem {

	public SpellCatalystItem(Properties properties) {
		super(properties);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public ItemStack getDefaultInstance() {
		return getSpellCatalyst(ModSpells.POISON);
	}
	
	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		AlchemySpell spell = ISpellCatalystItem.getAlchemySpell(stack);
		if (spell == null || spell.isEmpty())
			return new TranslationTextComponent(this.getTranslationKey());
		return new TranslationTextComponent(this.getTranslationKey() + ".spell", spell.getDisplayName());
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		//SpellEffect spellEffect = ISpellCatalystItem.getSpellEffect(stack);
		//if (spellEffect == null)
			return this.getTranslationKey();
		//return this.getTranslationKey() + '.' + spellEffect.getTranslationKey();
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return super.hasEffect(stack);// || (ISpellCatalystItem.getAlchemySpell(stack) != null);
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.isInGroup(group)) {
			for (AlchemySpell spell : ModSpells.getAllAlchemySpells()) {
				if (spell != null && !spell.isEmpty()) items.add(getSpellCatalyst(spell));
			}
		}
	}

	@Override
	public void depleteSpellCatalyst(ItemStack stack, @Nullable PlayerEntity player) {
		stack.shrink(1);
	}
	
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		AlchemySpell spell = ISpellCatalystItem.getAlchemySpell(stack);
		if (spell != null)
			spell.addInformation(tooltip, flagIn);
	}
	
	public static ItemStack getSpellCatalyst(AlchemySpell spell) {
		return ISpellCatalystItem.setAlchemySpell(spell, new ItemStack(ModItems.SPELL_CATALYST, 1));
	}
	
	public static ItemStack getSpellCatalyst(SpellEffect spellEffect) {
		return getSpellCatalyst(new AlchemySpell(spellEffect, 0, 0));
	}
	
	public static ItemStack getSpellCatalyst(SpellEffect spellEffect, int potencyLvl, int durationLvl) {
		return getSpellCatalyst(new AlchemySpell(spellEffect, potencyLvl, durationLvl));
	}

}
