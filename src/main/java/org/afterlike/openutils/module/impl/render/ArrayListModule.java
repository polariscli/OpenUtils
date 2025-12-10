package org.afterlike.openutils.module.impl.render;

import java.awt.*;
import java.io.IOException;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.event.handler.EventHandler;
import org.afterlike.openutils.event.impl.RenderOverlayEvent;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.Setting;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.afterlike.openutils.util.client.ClientUtil;
import org.afterlike.openutils.util.game.RenderUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArrayListModule extends Module {
	private final BooleanSetting editPosition;
	private final BooleanSetting dropShadow;
	private final BooleanSetting alphabeticalSort;
	private int hudX = 5;
	private int hudY = 70;
	public ArrayListModule() {
		super("Array List", ModuleCategory.RENDER);
		this.registerSetting(editPosition = new BooleanSetting("Edit position", false));
		this.registerSetting(dropShadow = new BooleanSetting("Drop shadow", true));
		this.registerSetting(alphabeticalSort = new BooleanSetting("Alphabetical sort", false));
	}

	@Override
	public void onSettingChanged(@Nullable final Setting<?> setting) {
		if (setting == editPosition) {
			editPosition.disable();
			mc.displayGuiScreen(new EditorGuiScreen());
		}
		super.onSettingChanged(setting);
	}

	@EventHandler
	private void onRenderOverlay(@NotNull final RenderOverlayEvent event) {
		if (!ClientUtil.notNull()) {
			return;
		}
		if (mc.currentScreen != null || mc.gameSettings.showDebugInfo) {
			return;
		}
		int y = hudY;
		int delta = 0;
		for (@NotNull final Module module : OpenUtils.get().getModuleHandler().getEnabledModulesSorted()) {
			if (module.isEnabled() && module != this) {
				mc.fontRendererObj.drawString(module.getName(), hudX, y,
						RenderUtil.getChromaColor(2L, delta), dropShadow.getValue());
				y += mc.fontRendererObj.FONT_HEIGHT + 2;
				delta -= 120;
			}
		}
	}
	class EditorGuiScreen extends GuiScreen {
		private static final String SAMPLE_TEXT = "This is an-Example-HUD";
		private GuiButtonExt resetButton;
		private boolean dragging = false;
		private int previewMinX = 0;
		private int previewMinY = 0;
		private int previewMaxX = 0;
		private int previewMaxY = 0;
		private int currentX = 5;
		private int currentY = 70;
		private int startDragX = 0;
		private int startDragY = 0;
		private int lastMouseX = 0;
		private int lastMouseY = 0;
		public void initGui() {
			super.initGui();
			this.buttonList.add(this.resetButton = new GuiButtonExt(1, this.width - 90, 5, 85, 20,
					"Reset position"));
			this.currentX = ArrayListModule.this.hudX;
			this.currentY = ArrayListModule.this.hudY;
		}

		public void drawScreen(final int mX, final int mY, final float pt) {
			drawRect(0, 0, this.width, this.height, -1308622848);
			final int minX = this.currentX;
			final int minY = this.currentY;
			final int maxX = minX + 50;
			final int maxY = minY + 32;
			this.drawSampleText(this.mc.fontRendererObj);
			this.previewMinX = minX;
			this.previewMinY = minY;
			this.previewMaxX = maxX;
			this.previewMaxY = maxY;
			ArrayListModule.this.hudX = minX;
			ArrayListModule.this.hudY = minY;
			final ScaledResolution res = new ScaledResolution(this.mc);
			final int x = res.getScaledWidth() / 2 - 84;
			final int y = res.getScaledHeight() / 2 - 20;
			RenderUtil.drawChromaText("Edit the HUD position by dragging.", '-', x, y, 2L, 0L, true,
					this.mc.fontRendererObj);
			try {
				this.handleInput();
			} catch (final IOException ignored) {
			}
			super.drawScreen(mX, mY, pt);
		}

		private void drawSampleText(@NotNull final FontRenderer fontRenderer) {
			final int x = this.previewMinX;
			int y = this.previewMinY;
			for (final String line : EditorGuiScreen.SAMPLE_TEXT.split("-")) {
				fontRenderer.drawString(line, x, y, Color.white.getRGB(),
						ArrayListModule.this.dropShadow.getValue());
				y += fontRenderer.FONT_HEIGHT + 2;
			}
		}

		protected void mouseClickMove(final int mouseX, final int mouseY,
				final int clickedMouseButton, final long timeSinceLastClick) {
			super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
			if (clickedMouseButton == 0) {
				if (this.dragging) {
					this.currentX = this.startDragX + (mouseX - this.lastMouseX);
					this.currentY = this.startDragY + (mouseY - this.lastMouseY);
				} else if (mouseX > this.previewMinX && mouseX < this.previewMaxX
						&& mouseY > this.previewMinY && mouseY < this.previewMaxY) {
					this.dragging = true;
					this.lastMouseX = mouseX;
					this.lastMouseY = mouseY;
					this.startDragX = this.currentX;
					this.startDragY = this.currentY;
				}
			}
		}

		protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
			super.mouseReleased(mouseX, mouseY, state);
			if (state == 0) {
				this.dragging = false;
			}
		}

		protected void actionPerformed(final GuiButton button) {
			if (button == this.resetButton) {
				this.currentX = ArrayListModule.this.hudX = 5;
				this.currentY = ArrayListModule.this.hudY = 70;
			}
		}

		public boolean doesGuiPauseGame() {
			return false;
		}
	}
}
