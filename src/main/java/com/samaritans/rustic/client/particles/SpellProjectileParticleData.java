package com.samaritans.rustic.client.particles;

import java.util.Locale;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

public class SpellProjectileParticleData implements IParticleData {

	public final float r, g, b;
	
	public SpellProjectileParticleData(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public SpellProjectileParticleData(int color) {
		this(((color >> 16) & 255) / 255f, ((color >> 8) & 255) / 255f, (color & 255) / 255f);
	}
	
	@Override
	public ParticleType<?> getType() {
		return ModParticles.POTION_SPELL_PROJECTILE;
	}

	@Override
	public void write(PacketBuffer buf) {
		buf.writeFloat(this.r);
		buf.writeFloat(this.g);
		buf.writeFloat(this.b);
	}

	@Override
	public String getParameters() {
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f",
				this.getType().getRegistryName(), this.r, this.g, this.b);
	}
	
	public static final IDeserializer<SpellProjectileParticleData> DESERIALIZER = new IDeserializer<SpellProjectileParticleData>() {
		@Override
		public SpellProjectileParticleData deserialize(ParticleType<SpellProjectileParticleData> particleType, StringReader reader) throws CommandSyntaxException {
			reader.expect(' ');
			float r = reader.readFloat();
			reader.expect(' ');
			float g = reader.readFloat();
			reader.expect(' ');
			float b = reader.readFloat();
			return new SpellProjectileParticleData(r, g, b);
		}
		@Override
		public SpellProjectileParticleData read(ParticleType<SpellProjectileParticleData> particleType, PacketBuffer buf) {
			return new SpellProjectileParticleData(buf.readFloat(), buf.readFloat(), buf.readFloat());
		}
	};
	
	public static class Type extends ParticleType<SpellProjectileParticleData> {
		public Type() {
			super(true, SpellProjectileParticleData.DESERIALIZER);
		}
	}
	
}
