package com.samaritans.rustic.client.gui;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.samaritans.rustic.alchemy.AlchemySpell;
import com.samaritans.rustic.alchemy.ICastingItem;
import com.samaritans.rustic.alchemy.ISpellCatalystItem;
import com.samaritans.rustic.network.PacketHandler;
import com.samaritans.rustic.network.SpellSelectPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class SpellRadialScreen extends Screen {

	protected static final int OPEN_ANIM_DURATION = 4;
	protected static final int OPTION_SELECT_ANIM_MAX = 4;
	
	int ticksOpen = 0;
	ItemStack casterStack, casterDisplayStack;
	int casterSlot;
	boolean isCasterSlotCurio;
	NonNullList<ItemStack> options;
	int[] optionSelectAnimVals;
	
	
	public SpellRadialScreen(ItemStack casterStack, int casterSlot, boolean isCasterSlotCurio, NonNullList<ItemStack> options) {
		super(new StringTextComponent("SPELL RADIAL"));	
		this.casterStack = casterStack;
		this.casterDisplayStack = casterStack.copy();
		this.casterSlot = casterSlot;
		this.isCasterSlotCurio = isCasterSlotCurio;
		this.options = options;
		
		// duplicates options to make it easier to test with large amounts of options
		/*int baseOptionsSize = this.options.size();
		for (int m = 0; m < 2; m++) {			
			for (int i = 1; i < baseOptionsSize; i++) {
				this.options.add(this.options.get(i).copy());
			}
		}*/
		
		this.optionSelectAnimVals = new int[this.options.size()];
		Arrays.fill(this.optionSelectAnimVals, 0);
	}
	
	@Override
	public void removed() {
		super.removed();
	}
	
	@Override
	public void tick() {
		ticksOpen++;
		
		int selectedOption = this.getSelectedOption();
		for (int i = 0; i < this.optionSelectAnimVals.length; i++) {
			if (i == selectedOption) {
				if (this.optionSelectAnimVals[i] < OPTION_SELECT_ANIM_MAX) this.optionSelectAnimVals[i]++;
			} else {
				if (this.optionSelectAnimVals[i] > 0) this.optionSelectAnimVals[i]--;
			}
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) return true;
		if (button != 0) return false;
		final int selectedOption = this.getSelectedOption(mouseX, mouseY);
		if (this.isValidOption(selectedOption)) {
			this.selectSpellAndClose(selectedOption);
			return true;
		}
		return false;
	}
	
	protected void selectSpellAndClose(int selectedOption) {
		AlchemySpell spell = this.getSelectedSpell(selectedOption);
		if (!this.isCasterSlotCurio) {
			ItemStack stack = minecraft.player.inventory.getStackInSlot(this.casterSlot);
			ICastingItem.setSelectedSpell(spell, stack);
		} else {
			// TODO update selected spell of stack in curio slot
		}
		PacketHandler.sendToServer(new SpellSelectPacket(this.casterSlot, this.isCasterSlotCurio, spell));
		this.onClose(); // close the radial menu
	}
	public void selectSpellAndClose() {
		this.selectSpellAndClose(this.getSelectedOption());
	}
	
	protected int getSelectedOption(double mouseX, double mouseY) {
		if (this.options.size() < 1) return -1;
		
		final int centerX = this.width / 2, centerY = this.height / 2;
		/*{
			final double dx = mouseX - centerX, dy = mouseY - centerY;
			if (MathHelper.sqrt((dx * dx) + (dy * dy)) < INNER_RADIUS)
				return -1;
		}*/
		final double sectionDegrees = 360d / this.options.size();
		final double degOffset = sectionDegrees / 2;
		
		final double rad = Math.atan2(mouseY - centerY, mouseX - centerX);
		int deg = (int) (degOffset + (rad * 180d / Math.PI)) + 90;
		deg = ((deg % 360) + 360) % 360;
		
		return (int) (deg / sectionDegrees);
	}
	protected int getSelectedOption() {
		double mouseX = minecraft.mouseHelper.getMouseX() * ((double) minecraft.mainWindow.getScaledWidth()) / ((double) minecraft.mainWindow.getWidth());
		double mouseY = minecraft.mouseHelper.getMouseY() * ((double) minecraft.mainWindow.getScaledHeight()) / ((double) minecraft.mainWindow.getHeight());
		return getSelectedOption(mouseX, mouseY);
	}
	
	protected boolean isValidOption(int optionIndex) {
		return (optionIndex >= 0 && optionIndex < this.options.size());
	}
	
	protected AlchemySpell getSelectedSpell(int selectedOption) {
		if (this.isValidOption(selectedOption)) {
			AlchemySpell spell = ISpellCatalystItem.getAlchemySpell(this.options.get(selectedOption));
			if (spell != null && !spell.isEmpty()) return spell;
		}
		return null;
	}
	protected AlchemySpell getSelectedSpell() {
		return this.getSelectedSpell(this.getSelectedOption());
	}
	
	protected float getOptionSelectAnimVal(int optionIndex, int selectedOption, float partialTicks) {
		final float selectAnimVal = this.optionSelectAnimVals[optionIndex];
		if (optionIndex == selectedOption) {
			return Math.min(selectAnimVal + partialTicks, OPTION_SELECT_ANIM_MAX) / OPTION_SELECT_ANIM_MAX;
		}
		return Math.max(selectAnimVal - partialTicks, 0) / OPTION_SELECT_ANIM_MAX;
	}
	
	protected static final int BASE_ICON_RADIUS = 12;
	protected static final double INNER_RADIUS = BASE_ICON_RADIUS * 2.0;
	
	protected double calculateIconRadius() {
		final double sectionRadians = 2d * Math.PI / this.options.size();
		final double minRadius = (BASE_ICON_RADIUS * 2.5) + INNER_RADIUS;
		final double maxRadius = this.height * 0.4;
		final double minIconRadius = minRadius / ((2d / sectionRadians) + 1d);
		final double maxIconRadius = maxRadius / ((2d / sectionRadians) + 1d);
		final double targetIconRadius = BASE_ICON_RADIUS * 1.5;
		final double targetRadius = ((2 * targetIconRadius / sectionRadians) + targetIconRadius);
		return MathHelper.clamp(targetRadius / ((2d / sectionRadians) + 1d), minIconRadius, maxIconRadius);
	}
	protected double calculateRadius(double iconRadius) {
		final double sectionRadians = 2d * Math.PI / this.options.size();
		return Math.max(((2 * iconRadius / sectionRadians) + iconRadius), (iconRadius * 2) + INNER_RADIUS);
	}
	protected double calculateSelectedOptionRadius(double radius) {
		final double maxSelectedOptionRadius = this.height * 0.45;
		return Math.min(radius + 4, maxSelectedOptionRadius);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		//this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		
		final float openProgress = Math.min((this.ticksOpen + partialTicks) / OPEN_ANIM_DURATION, 1f);

		final int selectedOption = this.getSelectedOption(mouseX, mouseY);
		
		final double iconRadius = this.calculateIconRadius();
		final double radius = this.calculateRadius(iconRadius) * openProgress;
		
		// render the background of the radial menu
		this.renderRadialBackground(radius, selectedOption, partialTicks);
		
		// render the caster item
		this.renderCasterItem(selectedOption);
		
		// render each option's itemstack on the radial menu
		this.renderRadialOptionIcons(radius, iconRadius, selectedOption, partialTicks);
		
		// render a tooltip for the currently selected option
		this.renderOptionTooltip(selectedOption, mouseX, mouseY);
	}
	
	protected void renderRadialBackground(double radius, int selectedOption, float partialTicks) {
		final double centerX = this.width / 2d, centerY = this.height / 2d;
		final double selectedOptionRadius = this.calculateSelectedOptionRadius(radius);
		final int numOptions = this.options.size();
		if (numOptions < 1) return;
		final double sectionDegrees = 360d / numOptions;
		
		final float red = 0.25f, green = 0.25f, blue = 0.25f, alpha = 0.6f;
		final float selectedRed = 0.0f, selectedGreen = 0.8f, selectedBlue = 0.1f, selectedAlpha = 0.6f;
		
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		//GlStateManager.shadeModel(GL11.GL_SMOOTH);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuffer();
		for (int i = 0; i < numOptions; i++) {
			final float selectAnimVal = getOptionSelectAnimVal(i, selectedOption, partialTicks);
			final double renderRadius = MathHelper.lerp(selectAnimVal, radius, selectedOptionRadius);
			final float selectAnimSqr = selectAnimVal * selectAnimVal;
			float r1 = MathHelper.lerp(selectAnimSqr, red, selectedRed);
			float g1 = MathHelper.lerp(selectAnimSqr, green, selectedGreen);
			float b1 = MathHelper.lerp(selectAnimSqr, blue, selectedBlue);
			float a1 = MathHelper.lerp(selectAnimSqr, alpha, selectedAlpha);
			
			double degOffset = (i - 0.5d) * sectionDegrees;
			/*buf.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
			buf.pos(centerX, centerY, 0).color(r1, g1, b1, a1).endVertex();
			for (double deg = sectionDegrees; deg >= -1; deg--) {
				double rad = (Math.max(deg, 0) + degOffset - 90d) / 180d * Math.PI;
				buf.pos(centerX + (Math.cos(rad) * renderRadius), centerY + (Math.sin(rad) * renderRadius), 0).color(r1, g1, b1, a1).endVertex();
			}
			buf.pos(centerX, centerY, 0).color(r1, g1, b1, a1).endVertex();*/
			buf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
			for (double deg = sectionDegrees; deg >= -1; deg--) {
				double rad = (Math.max(deg, 0) + degOffset - 90d) / 180d * Math.PI;
				buf.pos(centerX + (Math.cos(rad) * INNER_RADIUS), centerY + (Math.sin(rad) * INNER_RADIUS), 0).color(r1, g1, b1, a1).endVertex();
				buf.pos(centerX + (Math.cos(rad) * renderRadius), centerY + (Math.sin(rad) * renderRadius), 0).color(r1, g1, b1, a1).endVertex();
			}
			tessellator.draw();
		}
		final double innerRadius = INNER_RADIUS;//(INNER_RADIUS + BASE_ICON_RADIUS) / 2d;
		buf.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
		buf.pos(centerX, centerY, 0).color(red, green, blue, alpha).endVertex();
		for (double deg = 360; deg >= 0; deg--) {
			double rad = (Math.max(deg, 0) - 90d) / 180d * Math.PI;
			buf.pos(centerX + (Math.cos(rad) * innerRadius), centerY + (Math.sin(rad) * innerRadius), 0).color(red, green, blue, alpha).endVertex();
		}
		buf.pos(centerX, centerY, 0).color(red, green, blue, alpha).endVertex();
		tessellator.draw();
		
		//GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.enableAlphaTest();
		GlStateManager.disableBlend();
		GlStateManager.enableTexture();
	}
	
	protected void renderCasterItem(int selectedOption) {
		final float scale = 2f;
		final int x = (int) (((this.width / 2) / scale) - 8);
		final int y = (int) (((this.height / 2) / scale) - 8);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.scalef(scale, scale, scale);
		if (this.isValidOption(selectedOption)) {
			ICastingItem.setSelectedSpell(this.getSelectedSpell(selectedOption), this.casterDisplayStack);
			this.itemRenderer.renderItemAndEffectIntoGUI(this.casterDisplayStack, x, y);
		} else {
			this.itemRenderer.renderItemAndEffectIntoGUI(this.casterStack, x, y);
		}
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
	}
	
	protected void renderRadialOptionIcons(double radius, double iconRadius, int selectedOption, float partialTicks) {
		final int iconDownscale = (iconRadius < BASE_ICON_RADIUS) ? MathHelper.ceil(BASE_ICON_RADIUS / iconRadius) : 1;
		if ((radius - INNER_RADIUS) < (BASE_ICON_RADIUS / iconDownscale * 2d)) return;
		final double centerX = this.width / 2d, centerY = this.height / 2d;
		final double iconScale = 1d / iconDownscale;
		final double selectedOptionRadius = this.calculateSelectedOptionRadius(radius);
		final int numOptions = this.options.size();
		GlStateManager.pushMatrix();
		GlStateManager.scaled(iconScale, iconScale, iconScale);
		for (int i = 0; i < numOptions; i++) {
			final float selectAnimVal = getOptionSelectAnimVal(i, selectedOption, partialTicks);
			final double renderRadius = MathHelper.lerp(selectAnimVal * selectAnimVal, radius, selectedOptionRadius);
			
			ItemStack stack = this.options.get(i);
			double deg = i * (360d / numOptions);
			double rad = ((deg - 90d) / 180d) * Math.PI;
			int ix = (int) Math.round((centerX + (Math.cos(rad) * (renderRadius - iconRadius)) - (8 * iconScale)) * iconDownscale);
			int iy = (int) Math.round((centerY + (Math.sin(rad) * (renderRadius - iconRadius)) - (8 * iconScale)) * iconDownscale);
			this.itemRenderer.renderItemAndEffectIntoGUI(stack, ix, iy);
		}
		GlStateManager.popMatrix();
	}
	
	protected void renderOptionTooltip(int optionIndex, int x, int y) {
		if (this.isValidOption(optionIndex)) {
			ItemStack stack = this.options.get(optionIndex);
			List<String> tooltip = Lists.newArrayList();
			if (stack.isEmpty()) {
				tooltip.add(new TranslationTextComponent("spell_effect.rustic.none").getFormattedText());
			} else {
				if (stack.getItem() instanceof ISpellCatalystItem) {
					AlchemySpell spell = ISpellCatalystItem.getAlchemySpell(stack);
					if (spell != null && !spell.isEmpty()) {
						List<ITextComponent> spellTooltip = Lists.newArrayList();
						spellTooltip.add(spell.getDisplayName());
						spell.addInformation(spellTooltip, minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
						for (ITextComponent textComp : spellTooltip)
							tooltip.add(textComp.getFormattedText());
					}
				} else {
					List<ITextComponent> itemTooltip = stack.getTooltip(minecraft.player, minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
					for (ITextComponent textComp : itemTooltip)
						tooltip.add(textComp.getFormattedText());
				}
			}
			if (!tooltip.isEmpty())
				this.renderTooltip(tooltip, x, y);
		}
	}
	
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}
	
	
	
	@SubscribeEvent
	public static void onRenderCrosshair(RenderGameOverlayEvent.Pre e) {
		Minecraft mc = Minecraft.getInstance();
		if (e.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS &&
				mc.currentScreen != null && (mc.currentScreen instanceof SpellRadialScreen)) {
			e.setCanceled(true);
		}
	}

}
