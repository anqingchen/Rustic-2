package com.samaritans.rustic.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;

public class CrushingTubRecipe {
    private final Ingredient input;
    private final ItemStack byproduct;
    private final FluidStack output;
    private ResourceLocation id;

    public CrushingTubRecipe(ResourceLocation id, Ingredient input, FluidStack output) {
        this(id, input, output, ItemStack.EMPTY);
    }

    public CrushingTubRecipe(ResourceLocation id, Ingredient input, FluidStack output, ItemStack byproduct) {
        this.id = id;
        this.input = input;
        this.byproduct = byproduct;
        this.output = output;
    }

    public static CrushingTubRecipe read(PacketBuffer buf) {
        ResourceLocation id = buf.readResourceLocation();
        Ingredient input = Ingredient.read(buf);
        ItemStack byproduct = buf.readItemStack();
        FluidStack output = buf.readFluidStack();
        return new CrushingTubRecipe(id, input, output, byproduct);
    }

    public boolean matches(IItemHandler inv) {
        return input.test(inv.getStackInSlot(0));
    }

    public ResourceLocation getId() {
        return id;
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getByproduct() {
        return byproduct;
    }

    public FluidStack getOutput() {
        return output;
    }

    public void write(PacketBuffer buf) {
        buf.writeResourceLocation(id);
        input.write(buf);
        buf.writeItemStack(byproduct, false);
        buf.writeFluidStack(output);
    }
}
