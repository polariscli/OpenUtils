package org.afterlike.openutils.gui;

import java.awt.*;
import java.io.IOException;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.module.api.hud.Anchor;
import org.afterlike.openutils.module.api.hud.HudModule;
import org.afterlike.openutils.module.api.hud.Position;
import org.afterlike.openutils.util.game.RenderUtil;

public class EditorGuiScreen extends GuiScreen {
	private final HudModule module;
	private final String placeholderText;
	private GuiButtonExt resetButton;
	private boolean dragging = false;
	private int previewMinX = 0;
	private int previewMinY = 0;
	private int previewMaxX = 0;
	private int previewMaxY = 0;
	private int startDragX = 0;
	private int startDragY = 0;
	private int lastMouseX = 0;
	private int lastMouseY = 0;
	private int currentScreenX = 0;
	private int currentScreenY = 0;
	public EditorGuiScreen(final HudModule module, final String placeholderText) {
		this.module = module;
		this.placeholderText = placeholderText;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.add(this.resetButton = new GuiButtonExt(1, this.width - 90, 5, 85, 20,
				"Reset position"));
	}

	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
		drawDefaultBackground();
		final Position position = module.getHudPosition();
		final int minX = position.getX();
		final int minY = position.getY();
		final int maxX = minX + 50;
		final int maxY = minY + 32;
		this.drawSampleText(this.mc.fontRendererObj, minX, minY);
		this.previewMinX = minX;
		this.previewMinY = minY;
		this.previewMaxX = maxX;
		this.previewMaxY = maxY;
		final ScaledResolution res = new ScaledResolution(this.mc);
		final int x = res.getScaledWidth() / 2 - 84;
		final int y = res.getScaledHeight() / 2 - 20;
		RenderUtil.drawChromaText("Edit the HUD position by dragging.", '-', x, y, 2L, 0L, true,
				this.mc.fontRendererObj);
		final Anchor anchor = position.getAnchor();
		final String anchorText = "Anchor: " + anchor.name().replace("_", " ");
		this.mc.fontRendererObj.drawStringWithShadow(anchorText, 5, 5, Color.LIGHT_GRAY.getRGB());
		try {
			this.handleInput();
		} catch (final IOException ignored) {
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void drawSampleText(final FontRenderer fontRenderer, final int x, final int startY) {
		int y = startY;
		for (final String line : placeholderText.split("-|\\n")) {
			fontRenderer.drawString(line, x, y, Color.white.getRGB(), module.useHudDropShadow());
			y += fontRenderer.FONT_HEIGHT + 2;
		}
	}

	@Override
	protected void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton,
			final long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		if (clickedMouseButton == 0) {
			if (this.dragging) {
				this.currentScreenX = this.startDragX + (mouseX - this.lastMouseX);
				this.currentScreenY = this.startDragY + (mouseY - this.lastMouseY);
				module.getHudPosition().setScreenPosition(this.currentScreenX, this.currentScreenY);
			} else if (mouseX > this.previewMinX && mouseX < this.previewMaxX
					&& mouseY > this.previewMinY && mouseY < this.previewMaxY) {
				this.dragging = true;
				this.lastMouseX = mouseX;
				this.lastMouseY = mouseY;
				this.startDragX = module.getHudPosition().getX();
				this.startDragY = module.getHudPosition().getY();
				this.currentScreenX = this.startDragX;
				this.currentScreenY = this.startDragY;
			}
		}
	}

	@Override
	protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
		super.mouseReleased(mouseX, mouseY, state);
		if (state == 0 && this.dragging) {
			this.dragging = false;
			final Position position = module.getHudPosition();
			final ScaledResolution res = new ScaledResolution(this.mc);
			final int screenX = position.getX();
			final int screenY = position.getY();
			final int centerX = screenX + 25;
			final int centerY = screenY + 16;
			final Anchor newAnchor = Anchor.detect(centerX, centerY, res.getScaledWidth(),
					res.getScaledHeight());
			position.setAnchor(newAnchor);
			position.setScreenPosition(screenX, screenY);
			OpenUtils.get().getConfigHandler().saveConfiguration();
		}
	}

	@Override
	protected void actionPerformed(final GuiButton button) {
		if (button == this.resetButton) {
			module.getHudPosition().reset();
			OpenUtils.get().getConfigHandler().saveConfiguration();
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
