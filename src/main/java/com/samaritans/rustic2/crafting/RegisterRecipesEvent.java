package com.samaritans.rustic2.crafting;

import net.minecraftforge.eventbus.api.Event;

import java.util.function.Consumer;

/**
 * This part of the code that handles Recipes are mimicked from Botania,
 * All credits to Vazkii
 * https://github.com/Vazkii/Botania
 */

public class RegisterRecipesEvent extends Event {
    private final Consumer<CrushingTubRecipe> crushingTub;

    public RegisterRecipesEvent(Consumer<CrushingTubRecipe> crushingTub) {
        this.crushingTub = crushingTub;
    }

    public Consumer<CrushingTubRecipe> crushingTub() {
        return crushingTub;
    }
}
