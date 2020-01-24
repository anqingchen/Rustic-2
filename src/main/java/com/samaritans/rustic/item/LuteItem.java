package com.samaritans.rustic.item;

import java.util.Random;

import com.samaritans.rustic.ModSounds;
import com.samaritans.rustic.Rustic;
import com.samaritans.rustic.alchemy.AlchemySpell;
import com.samaritans.rustic.alchemy.ICastingItem;
import com.samaritans.rustic.alchemy.ISpellCatalystItem;
import com.samaritans.rustic.entity.LuteSpellEntity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class LuteItem extends Item implements ICastingItem {

	public LuteItem(Properties properties) {
		super(properties);
		this.addPropertyOverride(new ResourceLocation(Rustic.MODID, "playing"), (stack, world, entity) -> (
				(entity != null) && entity.isHandActive() && (entity.getActiveItemStack() == stack) ? 1.0F : 0.0F));
		this.addPropertyOverride(new ResourceLocation(Rustic.MODID, "charge"), (stack, world, entity) -> {
			if ((entity == null) || !(entity.getActiveItemStack().getItem() instanceof LuteItem))
				return 0f;
			else
				return (stack.getUseDuration() - entity.getItemInUseCount()) / CHARGE_TIME;
		});
	}
	
	// TODO remove
	@Override
	public boolean opensRadialFromInventory() {
		return false;
	}
	
	protected static final int USE_DURATION = 72000;
	protected static final float CHARGE_TIME = 20.0f;
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return hasSelectedSpell(stack) ? USE_DURATION : 0;
	}
	
	@Override
	public UseAction getUseAction(ItemStack stack) {
		return hasSelectedSpell(stack) ? UseAction.BOW : UseAction.NONE;
	}
	
	protected Random noteRand = new Random();
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft) {
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			AlchemySpell spell = ICastingItem.getSelectedSpell(stack);
			if (hasSelectedSpell(spell)) {
				final boolean isCreative = player.abilities.isCreativeMode;
				ItemStack catalystStack = (isCreative) ? ItemStack.EMPTY : ISpellCatalystItem.findSpellCatalyst(spell, player);
				final int useTime = USE_DURATION - timeLeft;
				if ((useTime >= 0) && (isCreative || !catalystStack.isEmpty())) {
					final float castingStrength = getCastingStrength(useTime);
					if (castingStrength >= 0.1f) {
						if (!world.isRemote) { // create and shoot the spell projectile entity
							LuteSpellEntity ent = new LuteSpellEntity(player, castingStrength, world).setSpell(spell);
							ent.shoot(player, player.rotationPitch, player.rotationYaw, 0.0f, 0.5f, 0.05f);
							world.addEntity(ent);
						}
						if (!isCreative) // deplete the spell catalyst
							((ISpellCatalystItem) catalystStack.getItem()).depleteSpellCatalyst(catalystStack, player);
						
						this.playNote(world, player, player.getActiveHand(), castingStrength);
						
						//final int cooldown = 10;
						//player.getCooldownTracker().setCooldown(this, cooldown);
						
						player.addStat(Stats.ITEM_USED.get(this));
					}
				}
			}
		}
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		AlchemySpell spell = ICastingItem.getSelectedSpell(stack);
		//System.out.println(spell);
		if (hasSelectedSpell(spell)) {
			final boolean isCreative = player.abilities.isCreativeMode;
			ItemStack catalystStack = (isCreative) ? ItemStack.EMPTY : ISpellCatalystItem.findSpellCatalyst(spell, player);
			if (isCreative || !catalystStack.isEmpty()) {
				player.setActiveHand(hand);
			} else {
				return new ActionResult<>(ActionResultType.FAIL, stack);
			}
		} else {
			player.swingArm(hand);
			this.playNote(world, player, hand, 1f);
			final int cooldown = 10;
			player.getCooldownTracker().setCooldown(this, cooldown);
			player.addStat(Stats.ITEM_USED.get(this));
		}
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}
	
	protected void playNote(World world, PlayerEntity player, Hand hand, float volumeScale) {
		if (world instanceof ServerWorld) {
			final int note = this.noteRand.nextInt(12) + 12; // random note within mid octave
			final float pitch = (float) Math.pow(2d, (note - 12d) / 12d);
			final float volume = (volumeScale * volumeScale * 3.0f) + 1.5f;
			
			final double offsetY = player.getHeight() * 0.6;
			world.playSound(null, new BlockPos(player.posX, player.posX + offsetY, player.posZ), ModSounds.LUTE, SoundCategory.PLAYERS, volume, pitch);
			
			final boolean rightHand = hand == ((player.getPrimaryHand() == HandSide.RIGHT) ? Hand.MAIN_HAND : Hand.OFF_HAND);
			
			final float yawOffset = (rightHand) ? 40f : -40f;
			final float yawRads = -(player.rotationYaw + yawOffset) * ((float) Math.PI / 180f);
			final double offsetScale = 0.7;
			final double offsetX = MathHelper.sin(yawRads) * offsetScale, offsetZ = MathHelper.cos(yawRads) * offsetScale;
			((ServerWorld) world).spawnParticle(ParticleTypes.NOTE, player.posX + offsetX, player.posY + offsetY, player.posZ + offsetZ, 0, note / 24d, 0, 0, 1);
		}
	}
	
	public static boolean hasSelectedSpell(ItemStack stack) {
		AlchemySpell spell = ICastingItem.getSelectedSpell(stack);
		return (spell != null) && !spell.isEmpty();
	}
	public static boolean hasSelectedSpell(AlchemySpell spell) {
		return (spell != null) && !spell.isEmpty();
	}
	
	public static float getCastingStrength(int charge) {
		float f = charge / CHARGE_TIME;
		f = ((f * f) + (f * 2f)) / 3f;
		return (f > 1f) ? 1f : f;
	}
	
	@Override
	public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
		if (ItemStack.areItemsEqual(oldStack, newStack)) {
			AlchemySpell oldSpell = ICastingItem.getSelectedSpell(oldStack);
			AlchemySpell newSpell = ICastingItem.getSelectedSpell(newStack);
			return (oldSpell == null) ? (newSpell == null) : oldSpell.equals(newSpell);
		}
		return false;
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (ItemStack.areItemsEqual(oldStack, newStack)) {
			AlchemySpell oldSpell = ICastingItem.getSelectedSpell(oldStack);
			AlchemySpell newSpell = ICastingItem.getSelectedSpell(newStack);
			return (oldSpell == null) ? (newSpell != null) : !oldSpell.equals(newSpell);
		}
		return true;
	}
	
	
}
