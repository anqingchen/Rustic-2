package com.samaritans.rustic.client.particles;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpellProjectileParticle extends SpriteTexturedParticle {
	
	protected final IAnimatedSprite spriteSheet;
	
	protected SpellProjectileParticle(World world, double x, double y, double z, double velX, double velY, double velZ, IAnimatedSprite spriteSheet) {
		super(world, x, y, z, velX, velY, velZ);
		this.spriteSheet = spriteSheet;
		
		this.motionX = (this.motionX * 0.01) + velX;
		this.motionY = (this.motionY * 0.01) + velY;
		this.motionZ = (this.motionZ * 0.01) + velZ;
		this.particleScale *= 0.9f;
		this.maxAge = this.rand.nextInt(4) + 3;
		this.canCollide = true;
		this.selectSpriteWithAge(spriteSheet);
	}
	
	public static SpellProjectileParticle create(World world, double x, double y, double z, double velX, double velY, double velZ) {
		return new SpellProjectileParticle(world, x, y, z, velX, velY, velZ, Factory.getAnimatedSprite());
	}
	
	public static SpellProjectileParticle create(World world, double x, double y, double z, double velX, double velY, double velZ, float r, float g, float b) {
		return new SpellProjectileParticle(world, x, y, z, velX, velY, velZ, Factory.getAnimatedSprite()).color(r, g, b);
	}
	
	public static SpellProjectileParticle create(World world, double x, double y, double z, double velX, double velY, double velZ, int color) {
		return new SpellProjectileParticle(world, x, y, z, velX, velY, velZ, Factory.getAnimatedSprite()).color(color);
	}
	
	public SpellProjectileParticle color(float r, float g, float b) {
		this.setColor(r, g, b);
		return this;
	}
	
	public SpellProjectileParticle color(int color) {
		return this.color(((color >> 16) & 255) / 255f, ((color >> 8) & 255) / 255f, (color & 255) / 255f);
	}
	
	public SpellProjectileParticle lifetime(int lifetime) {
		this.setMaxAge(lifetime);
		return this;
	}
	
	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	@Override
	public void tick() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.setExpired();
		} else {
			this.selectSpriteWithAge(this.spriteSheet);
			//this.motionY += 0.004;
			this.move(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.96;
			this.motionY *= 0.96;
			this.motionZ *= 0.96;
			if (this.onGround) {
				this.motionX *= 0.7;
				this.motionZ *= 0.7;
			}
		}
	}
	
	@Override
	public float getScale(float partialTicks) {
		return this.particleScale * (1f - (0.3f * (float) this.age / (float) this.maxAge));
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<SpellProjectileParticleData> {
		private static Factory instance;
		
		private final IAnimatedSprite spriteSheet;
		
		public Factory(IAnimatedSprite spriteSheet) {
			this.spriteSheet = spriteSheet;
			instance = this;
		}
		
		@Override
		public Particle makeParticle(SpellProjectileParticleData data, World world, double x, double y, double z, double velX, double velY, double velZ) {
			return new SpellProjectileParticle(world, x, y, z, velX, velY, velZ, this.spriteSheet).color(data.r, data.g, data.b);
		}
		
		public static IAnimatedSprite getAnimatedSprite() {
			return instance.spriteSheet;
		}
	}

}
