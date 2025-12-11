package org.afterlike.openutils.module.api.hud;

import java.awt.*;
import java.io.IOException;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.util.game.RenderUtil;
import org.jetbrains.annotations.NotNull;

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
	public EditorGuiScreen(@NotNull final HudModule module, @NotNull final String placeholderText) {
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
		drawRect(0, 0, this.width, this.height, -1308622848);
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
		try {
			this.handleInput();
		} catch (final IOException ignored) {
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void drawSampleText(@NotNull final FontRenderer fontRenderer, final int x,
			final int startY) {
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
				final int newX = this.startDragX + (mouseX - this.lastMouseX);
				final int newY = this.startDragY + (mouseY - this.lastMouseY);
				module.getHudPosition().setPosition(newX, newY);
			} else if (mouseX > this.previewMinX && mouseX < this.previewMaxX
					&& mouseY > this.previewMinY && mouseY < this.previewMaxY) {
				this.dragging = true;
				this.lastMouseX = mouseX;
				this.lastMouseY = mouseY;
				this.startDragX = module.getHudPosition().getX();
				this.startDragY = module.getHudPosition().getY();
			}
		}
	}

	@Override
	protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
		super.mouseReleased(mouseX, mouseY, state);
		if (state == 0) {
			this.dragging = false;
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
