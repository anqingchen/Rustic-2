package com.samaritans.rustic.client;

import org.lwjgl.glfw.GLFW;

import com.samaritans.rustic.Rustic;
import com.samaritans.rustic.alchemy.ICastingItem;
import com.samaritans.rustic.client.gui.SpellRadialScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Rustic.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KeyHandler {
	
	public static final KeyBinding OPEN_SPELL_RADIAL_KEYBIND = new KeyBinding(
			"key.rustic.open_spell_radial", GLFW.GLFW_KEY_V, "key.rustic.category");
	
	
	public static void initKeyBinds() {
		ClientRegistry.registerKeyBinding(OPEN_SPELL_RADIAL_KEYBIND);
	}
	
	public static void onOpenSpellRadialKeyBindPressed() {
		Minecraft mc = Minecraft.getInstance();
		// TODO check if either of the player's hands is holding a spell casting item
		// TODO pass list of options to radial (don't open screen if there are no options)
		// TODO pass slot of casting item to the screen
		if (mc.currentScreen == null) {
			if (mc.player == null) return;
			final int playerMainHandSlot = mc.player.inventory.currentItem;
			final int playerOffHandSlot = mc.player.inventory.mainInventory.size() + mc.player.inventory.armorInventory.size();
			
			ItemStack casterStack = ItemStack.EMPTY;
			int casterSlot = -1;
			boolean isCurioSlot = false;
			
			//mc.player.inventory.getStackInSlot(playerMainHandSlot);
			ItemStack handStack = mc.player.getHeldItemMainhand();
			if (!handStack.isEmpty() && (handStack.getItem() instanceof ICastingItem)) {
				casterStack = handStack;
				casterSlot = playerMainHandSlot;
			}
			if (casterStack.isEmpty()) {
				handStack = mc.player.getHeldItemOffhand();
				if (!handStack.isEmpty() && (handStack.getItem() instanceof ICastingItem)) {
					casterStack = handStack;
					casterSlot = playerOffHandSlot;
				}
			}
			/*if (casterStack.isEmpty()) {
				// TODO check curio inventory
				
				
			}*/
			if (casterStack.isEmpty()) {
				for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
					if ((i == playerMainHandSlot) || (i == playerOffHandSlot))
						continue;
					ItemStack stack = mc.player.inventory.getStackInSlot(i);
					if (!stack.isEmpty() && (stack.getItem() instanceof ICastingItem) && ((ICastingItem) stack.getItem()).opensRadialFromInventory(stack)) {
						casterStack = stack;
						casterSlot = i;
						break;
					}
				}
			}
			
			if (!casterStack.isEmpty() && (casterSlot >= 0)) {
				NonNullList<ItemStack> catalystOptions = ((ICastingItem) casterStack.getItem()).findCatalystsForRadial(casterStack, mc.player);
				if (!catalystOptions.isEmpty())
					mc.displayGuiScreen(new SpellRadialScreen(casterStack, casterSlot, isCurioSlot, catalystOptions));
			}
		} else if (mc.currentScreen instanceof SpellRadialScreen) {
			mc.currentScreen.onClose();
		}
	}
	
	@SubscribeEvent
	public static void onKeyInput(KeyInputEvent e) {
		if (isPressedFromKeyInput(OPEN_SPELL_RADIAL_KEYBIND, e.getKey(), e.getScanCode(), e.getAction())) {
			onOpenSpellRadialKeyBindPressed();
		}
	}
	@SubscribeEvent
	public static void onMouseInput(MouseInputEvent e) {
		if (isPressedFromMouseInput(OPEN_SPELL_RADIAL_KEYBIND, e.getButton(), e.getAction())) {
			onOpenSpellRadialKeyBindPressed();
		}
	}
	
	private static boolean isPressedFromKeyInput(KeyBinding keybind, int key, int scancode, int action) {
		return (action == GLFW.GLFW_PRESS) && keybind.matchesKey(key, scancode) &&
				keybind.getKeyConflictContext().isActive() &&
				keybind.getKeyModifier().isActive(keybind.getKeyConflictContext());
	}
	private static boolean isPressedFromMouseInput(KeyBinding keybind, int button, int action) {
		return (action == GLFW.GLFW_PRESS) && keybind.matchesMouseKey(button) &&
				keybind.getKeyConflictContext().isActive() &&
				keybind.getKeyModifier().isActive(keybind.getKeyConflictContext());
	}

}
