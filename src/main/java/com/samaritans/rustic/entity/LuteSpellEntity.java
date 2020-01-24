package com.samaritans.rustic.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.samaritans.rustic.alchemy.AlchemySpell;
import com.samaritans.rustic.alchemy.SpellEffect;
import com.samaritans.rustic.alchemy.SpellInstance;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public class LuteSpellEntity extends Entity implements IProjectile, IEntityAdditionalSpawnData {
	
	public static final DataParameter<Integer> SEGMENTS = EntityDataManager.createKey(LuteSpellEntity.class, DataSerializers.VARINT);
	
	protected static final int numSegments = 20;	
	protected static int defaultSegmentStates = 0;
	static {
		for (int i = 0; i < numSegments; i++)
			defaultSegmentStates |= (1 << i);
	}
	
	protected static final int maxLifetime = 23;

	protected LivingEntity owner;
	protected UUID ownerId;
	protected float castingStrength = 1f;
	protected AlchemySpell spell;
	protected float radius;
	protected int remainingLife, initialLife;
	protected boolean[] segmentStates = new boolean[numSegments];
	protected Set<Entity> hitEntities = new HashSet<>();
	
	public LuteSpellEntity(EntityType<? extends LuteSpellEntity> type, World world) {
		super(type, world);
		Arrays.fill(this.segmentStates, true);
	}
	
	public LuteSpellEntity(EntityType<? extends LuteSpellEntity> type, double x, double y, double z, World world) {
		this(type, world);
		this.setPosition(x, y, z);
	}
	
	public LuteSpellEntity(LivingEntity casterEntity, float castingStrength, World world) {
		this(ModEntities.LUTE_SPELL, casterEntity.posX, casterEntity.posY + casterEntity.getEyeHeight() - 0.1, casterEntity.posZ, world);
		this.setCaster(casterEntity);
		this.castingStrength = castingStrength;
		this.initialLife = this.remainingLife = MathHelper.ceil(((castingStrength + 0.5f) / 1.5f) * maxLifetime);
		this.radius = (castingStrength * 0.125f) + 0.125f;
	}

	@Override
	protected void registerData() {
		this.getDataManager().register(SEGMENTS, defaultSegmentStates);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		final double d0 = 5d * 64d * getRenderDistanceWeight();
		return distance < d0 * d0;//false; // TODO set to false?
	}
	
	@Override
	protected void doBlockCollisions() {
		// TODO implement?
	}
	
	Set<BlockPos> collidedBlocks = new HashSet<>();
	
	@Override
	public void tick() {
		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;
		super.tick();
		
		this.collidedBlocks.clear();
		
		final Vec3d vec3d = this.getMotion();
		final float nextRadius = this.radius + (2f / 20f);
		
		final boolean collideEntities = this.ticksExisted > 3;
		boolean changedSegmentStates = false;
		boolean hasActiveSegments = false;
		
		final Vec3d ringAxis = this.getLook(1f).normalize();
		final Vec3d ringUp = this.getUpVector(1f).normalize();
		final Vec3d ringRight = ringAxis.crossProduct(ringUp).normalize();
		
		final float segmentRads = ((float) Math.PI * 2f) / numSegments;
		for (int seg = 0; seg < numSegments; seg++) {
			if (!this.segmentStates[seg]) continue;
			hasActiveSegments = true;
			
			final float rad = ((seg + 0.5f) * segmentRads);
			final float cos = MathHelper.cos(rad), sin = MathHelper.sin(rad);
			final double ringX = (ringUp.x * sin) + (cos * ringRight.x);
			final double ringY = (ringUp.y * sin) + (cos * ringRight.y);
			final double ringZ = (ringUp.z * sin) + (cos * ringRight.z);
			final double segX = this.posX + (ringX * this.radius);
			final double segY = this.posY + (ringY * this.radius);
			final double segZ = this.posZ + (ringZ * this.radius);
			final double nextSegX = this.posX + vec3d.x + (ringX * nextRadius);
			final double nextSegY = this.posY + vec3d.y + (ringY * nextRadius);
			final double nextSegZ = this.posZ + vec3d.z + (ringZ * nextRadius);
			
			final double velX = nextSegX - segX, velY = nextSegY - segY, velZ = nextSegZ - segZ;
			
			final double halfAABBWidth = (rad * this.radius / 2d) + 1d;
			final double minX = Math.min(segX, nextSegX) - halfAABBWidth, minY = Math.min(segY, nextSegY) - halfAABBWidth, minZ = Math.min(segZ, nextSegZ) - halfAABBWidth;
			final double maxX = Math.max(segX, nextSegX) + halfAABBWidth, maxY = Math.max(segY, nextSegY) + halfAABBWidth, maxZ = Math.max(segZ, nextSegZ) + halfAABBWidth;
			final AxisAlignedBB aabb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
			
			final RayTraceResult result = this.rayTrace(new Vec3d(segX, segY, segZ), new Vec3d(nextSegX, nextSegY, nextSegZ), aabb, collideEntities);
			RayTraceResult.Type resultType = result.getType();
			if ((resultType == RayTraceResult.Type.ENTITY) && !this.world.isRemote) {
				this.hitEntities.add(((EntityRayTraceResult) result).getEntity());
				this.onImpact(result);
			} else if (resultType == RayTraceResult.Type.BLOCK) {
				BlockRayTraceResult blockHit = (BlockRayTraceResult) result;
				BlockPos pos = blockHit.getPos();
				if (!this.collidedBlocks.contains(pos)) {
					this.collidedBlocks.add(pos);
					if (!this.world.isRemote) this.onImpact(result);
				}
				this.segmentStates[seg] = false;
				changedSegmentStates = true;
				// spawn impact particles
				if ((this.spell != null) && !this.spell.isEmpty() && this.world.isRemote) {
					final Vec3d hitPos = blockHit.getHitVec();
					final double distScale = MathHelper.sqrt(hitPos.squareDistanceTo(segX, segY, segZ) / ((velX * velX) + (velY * velY) + (velZ * velZ)));
					final double r = MathHelper.lerp(distScale, this.radius, nextRadius);
					final double px = MathHelper.lerp(distScale, this.posX, this.posX + vec3d.x);
					final double py = MathHelper.lerp(distScale, this.posY, this.posY + vec3d.y);
					final double pz = MathHelper.lerp(distScale, this.posZ, this.posZ + vec3d.z);
					final double particleSpeedMod = -0.125;
					for (int i = 0; i < 5; i++) {
						final float prad = ((seg + this.rand.nextFloat()) * segmentRads);
						final float pcos = MathHelper.cos(prad), psin = MathHelper.sin(prad);
						final double psegX = px + (((ringUp.x * psin) + (pcos * ringRight.x)) * r);
						final double psegY = py + (((ringUp.y * psin) + (pcos * ringRight.y)) * r);
						final double psegZ = pz + (((ringUp.z * psin) + (pcos * ringRight.z)) * r);
						this.spell.getSpellEffect().spawnProjectileParticle(this, psegX, psegY, psegZ, velX * particleSpeedMod, velY * particleSpeedMod, velZ * particleSpeedMod);
					}
				}
				continue;
			}
			
			// spawn particles
			if ((this.spell != null) && !this.spell.isEmpty() && this.world.isRemote) {
				final float prad = ((seg + this.rand.nextFloat()) * segmentRads);
				final float pcos = MathHelper.cos(prad), psin = MathHelper.sin(prad);
				final double psegX = this.posX + (((ringUp.x * psin) + (pcos * ringRight.x)) * this.radius);
				final double psegY = this.posY + (((ringUp.y * psin) + (pcos * ringRight.y)) * this.radius);
				final double psegZ = this.posZ + (((ringUp.z * psin) + (pcos * ringRight.z)) * this.radius);
				final double particleSpeedMod = 1.0;
				this.spell.getSpellEffect().spawnProjectileParticle(this, psegX, psegY, psegZ, velX * particleSpeedMod, velY * particleSpeedMod, velZ * particleSpeedMod);
			}		
		}
		if (changedSegmentStates)
			this.dataManager.set(SEGMENTS, this.getSegmentStatesAsInt());
		
		this.radius = nextRadius;
		
		this.posX += vec3d.x;
		this.posY += vec3d.y;
		this.posZ += vec3d.z;
		
		float f = MathHelper.sqrt(horizontalMag(vec3d));
		this.rotationYaw = (float) (MathHelper.atan2(-vec3d.x, vec3d.z) * (180d / Math.PI));
		for (this.rotationPitch = (float) (MathHelper.atan2(-vec3d.y, (double) f) * (180d / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F);

		while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
			this.prevRotationPitch += 360.0F;
		while (this.rotationYaw - this.prevRotationYaw < -180.0F)
			this.prevRotationYaw -= 360.0F;
		while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
			this.prevRotationYaw += 360.0F;

		this.rotationPitch = MathHelper.lerp(0.2F, this.prevRotationPitch, this.rotationPitch);
		this.rotationYaw = MathHelper.lerp(0.2F, this.prevRotationYaw, this.rotationYaw);
		//final float motionScale = (this.isInWater()) ? 0.8f : 1f;
		//this.setMotion(vec3d.scale((double) f1));
		this.setPosition(this.posX, this.posY, this.posZ);
		
		this.remainingLife--;
		if ((this.remainingLife <= 0) || !hasActiveSegments)
			this.remove();
	}

	public void onImpact(RayTraceResult result) {
		final LivingEntity caster = this.getCaster();
		final RayTraceResult.Type resultType = result.getType();
		if (resultType == RayTraceResult.Type.ENTITY) {
			EntityRayTraceResult entityHit = (EntityRayTraceResult) result;
			Entity entity = entityHit.getEntity();
			
			// TODO remove knockback?
			final double knockbackStrength = 0.5;
			Vec3d vec3d = this.getMotion().mul(1.0D, 0.0D, 1.0D).normalize().scale(knockbackStrength);
			if (vec3d.lengthSquared() > 0d)
				entity.addVelocity(vec3d.x, 0.1D, vec3d.z);
			
			if ((this.spell != null) && !this.spell.isEmpty() && (entity instanceof LivingEntity))
				new SpellInstance(this.spell, this.castingStrength).applyEffect(result, this, caster, world);
		} else if (resultType == RayTraceResult.Type.BLOCK) {
			if ((this.spell != null) && !this.spell.isEmpty())
				new SpellInstance(this.spell, this.castingStrength).applyEffect(result, this, caster, world);
		}
	}
	
	protected EntityRayTraceResult rayTraceEntities(Vec3d startVec, Vec3d endVec, AxisAlignedBB aabb) {
		final Entity caster = this.getCaster();
		double dist = Double.MAX_VALUE;
		Entity hitEnt = null;
		for (Entity e : this.world.getEntitiesInAABBexcluding(this, aabb, ent -> (
				!ent.isSpectator() && ent.canBeCollidedWith() && !ent.noClip && !this.hitEntities.contains(ent) &&
				((caster == null) || (!ent.isEntityEqual(caster) && !ent.isEntityEqual(caster.getRidingEntity())))
		))) {
			AxisAlignedBB entityAABB = e.getBoundingBox().grow(0.3);
			Optional<Vec3d> optional = entityAABB.rayTrace(startVec, endVec);
			if (optional.isPresent()) {
				double d = startVec.squareDistanceTo(optional.get());
				if (d < dist) {
					hitEnt = e;
					dist = d;
				}
			}
		}
		return (hitEnt == null) ? null : new EntityRayTraceResult(hitEnt);
	}
	
	protected RayTraceResult rayTrace(Vec3d startVec, Vec3d endVec, AxisAlignedBB aabb, boolean checkEntities) {
		final SpellEffect spellEffect = (this.spell != null) ? this.spell.getSpellEffect() : null;
		final RayTraceContext.BlockMode blockMode = (spellEffect != null) ? spellEffect.getRayTraceBlockMode() : RayTraceContext.BlockMode.OUTLINE;
		final RayTraceContext.FluidMode fluidMode = (spellEffect != null) ? spellEffect.getRayTraceFluidMode() : RayTraceContext.FluidMode.NONE;
		BlockRayTraceResult blockResult = world.rayTraceBlocks(new RayTraceContext(startVec, endVec, blockMode, fluidMode, this));
		EntityRayTraceResult entResult = null;
		if (checkEntities)
			entResult = this.rayTraceEntities(startVec, (blockResult.getType() != RayTraceResult.Type.MISS) ? blockResult.getHitVec() : endVec, aabb);
		return (entResult != null) ? entResult : blockResult;
	}
	
	public void shoot(Entity casterEntity, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy) {
		float f = -MathHelper.sin(rotationYawIn * ((float) Math.PI / 180F)) * MathHelper.cos(rotationPitchIn * ((float) Math.PI / 180F));
		float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * ((float) Math.PI / 180F));
		float f2 = MathHelper.cos(rotationYawIn * ((float) Math.PI / 180F)) * MathHelper.cos(rotationPitchIn * ((float) Math.PI / 180F));
		this.shoot((double) f, (double) f1, (double) f2, velocity, inaccuracy);
		Vec3d vec3d = casterEntity.getMotion();
		this.setMotion(this.getMotion().add(vec3d.x, casterEntity.onGround ? 0.0D : vec3d.y, vec3d.z));
	}
	
	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		Vec3d vec3d = (new Vec3d(x, y, z)).normalize().add(this.rand.nextGaussian() * 0.0075d * inaccuracy, this.rand.nextGaussian() * 0.0075d * inaccuracy, this.rand.nextGaussian() * 0.0075d * inaccuracy).scale((double) velocity);
		this.setMotion(vec3d);
		float f = MathHelper.sqrt(horizontalMag(vec3d));
		this.rotationYaw = (float) (MathHelper.atan2(-vec3d.x, vec3d.z) * (180d / Math.PI));
		this.rotationPitch = (float) (MathHelper.atan2(-vec3d.y, (double) f) * (180d / Math.PI));
		this.prevRotationYaw = this.rotationYaw;
		this.prevRotationPitch = this.rotationPitch;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void setVelocity(double x, double y, double z) {
		this.setMotion(x, y, z);
		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt(x * x + z * z);
			this.rotationYaw = (float) (MathHelper.atan2(-x, z) * (180d / Math.PI));
			this.rotationPitch = (float) (MathHelper.atan2(-y, (double) f) * (180d / Math.PI));
			this.prevRotationYaw = this.rotationYaw;
			this.prevRotationPitch = this.rotationPitch;
		}
	}
	
	public LuteSpellEntity setCaster(LivingEntity caster) {
		if (caster != null) {
			this.owner = caster;
			this.ownerId = caster.getUniqueID();
		} else {
			this.owner = null;
			this.ownerId = null;
		}
		return this;
	}
	
	@Nullable
	public LivingEntity getCaster() {
		if (this.owner == null && this.ownerId != null && this.world instanceof ServerWorld) {
			Entity entity = ((ServerWorld) this.world).getEntityByUuid(this.ownerId);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity) entity;
			} else {
				this.ownerId = null;
			}
		}
		return this.owner;
	}
	
	public LuteSpellEntity setSpell(AlchemySpell spell) {
		this.spell = spell;
		return this;
	}
	
	@Nullable
	public AlchemySpell getSpell() {
		return this.spell;
	}
	
	protected void setSegmentStatesFromInt(int segmentStatesInt) {
		for (int i = 0; i < numSegments; i++)
			this.segmentStates[i] = (((segmentStatesInt >> i) & 1) == 1);
	}
	
	protected int getSegmentStatesAsInt() {
		int segStates = 0;
		for (int i = 0; i < numSegments; i++) {
			if (this.segmentStates[i]) segStates |= (1 << i);
		}
		return segStates;
	}

	@Override
	protected void readAdditional(CompoundNBT compound) {
		if (compound.contains("spell", Constants.NBT.TAG_COMPOUND))
			this.spell = AlchemySpell.read(compound.getCompound("spell"));
		if (compound.contains("castingStrength", Constants.NBT.TAG_FLOAT))
			this.castingStrength = compound.getFloat("castingStrength");
		this.owner = null;
		if (compound.contains("owner", 10))
			this.ownerId = NBTUtil.readUniqueId(compound.getCompound("owner"));
		this.radius = compound.getFloat("radius");
		this.initialLife = compound.getInt("initialLife");
		this.remainingLife = compound.getInt("remainingLife");
		this.setSegmentStatesFromInt(compound.getInt("segmentStates"));
	}

	@Override
	protected void writeAdditional(CompoundNBT compound) {
		if (this.spell != null && !this.spell.isEmpty())
			compound.put("spell", this.spell.toNBT());
		compound.putFloat("castingStrength", this.castingStrength);
		if (this.ownerId != null)
			compound.put("owner", NBTUtil.writeUniqueId(this.ownerId));
		compound.putFloat("radius", this.radius);
		compound.putInt("initialLife", this.initialLife);
		compound.putInt("remainingLife", this.remainingLife);
		compound.putInt("segmentStates", this.getSegmentStatesAsInt());
	}

	@Override
	public boolean handleWaterMovement() {
		return false;
	}

	@Override
	public boolean isInLava() {
		return false;
	}
	
	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (SEGMENTS.equals(key) && this.world.isRemote)
			this.setSegmentStatesFromInt(this.dataManager.get(SEGMENTS));
		super.notifyDataManagerChange(key);
	}
	
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(PacketBuffer buf) {
		if (this.spell == null) this.spell = new AlchemySpell(null, 0, 0);
		this.spell.write(buf);
		buf.writeFloat(this.castingStrength);
		buf.writeFloat(this.radius);
		buf.writeVarInt(this.initialLife);
		buf.writeVarInt(this.remainingLife);
		if (this.ownerId != null) {
			buf.writeBoolean(true);
			buf.writeUniqueId(this.ownerId);
		} else {
			buf.writeBoolean(false);
		}
	}

	@Override
	public void readSpawnData(PacketBuffer buf) {
		this.spell = AlchemySpell.read(buf);
		this.castingStrength = buf.readFloat();
		this.radius = buf.readFloat();
		this.initialLife = buf.readVarInt();
		this.remainingLife = buf.readVarInt();
		this.owner = null;
		this.ownerId = (buf.readBoolean()) ? buf.readUniqueId() : null;
	}
	
}
