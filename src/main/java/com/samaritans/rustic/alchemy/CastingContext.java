package com.samaritans.rustic.alchemy;

import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class CastingContext {
	
	public final RayTraceResult rayTraceResult;
	public final Entity sourceEntity;
	public final Entity caster;
	public final World world;

	CastingContext(RayTraceResult rayTraceResult, Entity sourceEntity, Entity caster, World world) {
		this.rayTraceResult = rayTraceResult;
		this.sourceEntity = sourceEntity;
		this.caster = caster;
		this.world = world;
	}
	
	public Entity getTargetEntity() {
		if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY)
			return ((EntityRayTraceResult) rayTraceResult).getEntity();
		return null;
	}
	
	public BlockPos getBlockPos() {
		if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK)
			return ((BlockRayTraceResult) rayTraceResult).getPos();
		return null;
	}
	
	public Direction getBlockFace() {
		if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK)
			return ((BlockRayTraceResult) rayTraceResult).getFace();
		return null;
	}

}
