package com.samaritans.rustic.client.renderer.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CabinetModel extends Model {
    public RendererModel door;
    // fields
    RendererModel bottom;
    RendererModel top;
    RendererModel back;
    RendererModel right;
    RendererModel left;
    RendererModel handle;

    public CabinetModel(boolean mirror) {
        textureWidth = 128;
        textureHeight = 64;

        bottom = new RendererModel(this, 0, 0);
        bottom.addBox(0F, 0F, 0F, 16, 1, 16);
        bottom.setRotationPoint(-8F, 23F, -8F);
        bottom.setTextureSize(128, 64);
        bottom.mirror = true;
        setRotation(bottom, 0F, 0F, 0F);
        top = new RendererModel(this, 64, 0);
        top.addBox(0F, 0F, 0F, 16, 1, 16);
        top.setRotationPoint(-8F, 8F, -8F);
        top.setTextureSize(128, 64);
        top.mirror = true;
        setRotation(top, 0F, 0F, 0F);
        back = new RendererModel(this, 0, 17);
        back.addBox(0F, 0F, 0F, 16, 14, 1);
        back.setRotationPoint(-8F, 9F, 7F);
        back.setTextureSize(128, 64);
        back.mirror = true;
        setRotation(back, 0F, 0F, 0F);
        right = new RendererModel(this, 0, 32);
        right.addBox(0F, 0F, 0F, 1, 14, 15);
        right.setRotationPoint(-8F, 9F, -8F);
        right.setTextureSize(128, 64);
        right.mirror = true;
        setRotation(right, 0F, 0F, 0F);
        left = new RendererModel(this, 32, 32);
        left.addBox(0F, 0F, 0F, 1, 14, 15);
        left.setRotationPoint(7F, 9F, -8F);
        left.setTextureSize(128, 64);
        left.mirror = true;
        setRotation(left, 0F, 0F, 0F);
        door = new RendererModel(this, 64, 17);
        door.addBox((mirror) ? -14F : 0F, -7F, -0.5F, 14, 14, 1);
        door.setRotationPoint((mirror) ? 7F : -7F, 16F, -6.5F);
        door.setTextureSize(128, 64);
        door.mirror = true;
        setRotation(door, 0F, 0F, 0F);
        handle = new RendererModel(this, 94, 17);
        handle.addBox((mirror) ? -13F : 12F, -1F, -1.5F, 1, 2, 1);
        handle.setRotationPoint((mirror) ? 7F : -7F, 16F, -6.5F);
        handle.setTextureSize(128, 64);
        handle.mirror = true;
        setRotation(handle, 0F, 0F, 0F);
    }

    public void renderAll() {
        this.handle.rotateAngleY = this.door.rotateAngleY;
        this.bottom.render(0.0625F);
        this.top.render(0.0625F);
        this.back.render(0.0625F);
        this.right.render(0.0625F);
        this.left.render(0.0625F);
        this.door.render(0.0625F);
        this.handle.render(0.0625F);
    }

    public void setRotation(RendererModel model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public RendererModel getDoor() {
        return this.door;
    }
}
