package com.samaritans.rustic.alchemy;

import com.samaritans.rustic.Util;
import com.samaritans.rustic.proxy.ClientProxy;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IgniteSpellEffect extends SpellEffect {

	private static final int[] DURATIONS = { 1, 5, 10 };
	private static final float[] DAMAGES = { 3f, 5f, 7f };
	
	@Override
	public boolean applyEffect(SpellInstance spell, CastingContext context) {
		World world = context.world;
		if (world.isRemote) return false;
		if (context.getRayTraceType() == Type.ENTITY) {
			final Entity target = context.getTargetEntity();
			if (target.isWet()) return false;
			final float castingStrength = spell.getCastingStrength();
			final int duration = Math.max(MathHelper.ceil(DURATIONS[spell.getDurationLevel()] * castingStrength), 1);
			final float damage = Math.max(MathHelper.ceil(DAMAGES[spell.getPotencyLevel()] * castingStrength), 1f);
			final boolean hasCaster = context.caster != null, hasProjectile = context.sourceEntity != null;
			DamageSource source;
			if (hasProjectile && hasCaster)
				source =  new IndirectEntityDamageSource("onFire", context.sourceEntity, context.caster).setProjectile();
			else if (hasProjectile && !hasCaster)
				source = new IndirectEntityDamageSource("onFire", context.sourceEntity, context.sourceEntity).setProjectile();
			else if (hasCaster)
				source = new EntityDamageSource("onFire", context.caster);
			else
				source = new DamageSource("onFire");
			source.setFireDamage();
			
			int oldFireTimer = target.getFireTimer();
			target.setFire(duration);
			if (target.attackEntityFrom(source, damage)) {
				return true;
			} else {
				target.setFireTimer(oldFireTimer);
			}
		} else if (context.getRayTraceType() == Type.BLOCK) {
			BlockPos pos = context.getBlockPos();
			BlockState blockstate = world.getBlockState(pos);
			if (blockstate.getBlock() == Blocks.CAMPFIRE) {
				if (!blockstate.get(CampfireBlock.LIT) && !blockstate.get(CampfireBlock.WATERLOGGED)) {
					world.setBlockState(pos, blockstate.with(CampfireBlock.LIT, Boolean.valueOf(true)));
					return true;
				}
			} else {
				pos = pos.offset(context.getBlockFace());
				if (world.isAirBlock(pos)) {
					world.setBlockState(pos, ((FireBlock) Blocks.FIRE).getStateForPlacement(world, pos));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int getMaxModifierLevel(SpellModifier modifier) {
		switch (modifier) {
		case POTENCY:
			return DAMAGES.length - 1;
		case DURATION:
			return DURATIONS.length - 1;
		default:
			return 0;
		}
	}

	@Override
	public Category getCategory() {
		return Category.HARMFUL;
	}

	@Override
	public int getColor() {
		return Util.rgbInt(208, 123, 6);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void spawnProjectileParticle(Entity projectile, double x, double y, double z, double velX, double velY, double velZ) {
		Particle p = ClientProxy.spawnParticle(ParticleTypes.FLAME, x, y, z, velX, velY, velZ);
		p.setMaxAge((int) (Math.random() * 4) + 2);
	}
	
	@Override
	public RayTraceContext.FluidMode getRayTraceFluidMode() {
		return RayTraceContext.FluidMode.ANY;
	}

	@Override
	public int getAutoCastingCooldown(int potencyLevel, int durationLevel) {
		return 20 * 10;
	}

	@Override
	public boolean shouldAutoCast(LivingEntity entity) {
		return !entity.isImmuneToFire() && !entity.isBurning();
	}

}
