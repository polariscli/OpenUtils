package org.afterlike.openutils.gui;

import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.client.gui.GuiScreen;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.gui.component.Component;
import org.afterlike.openutils.gui.component.impl.ModuleComponent;
import org.afterlike.openutils.gui.panel.CategoryPanel;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.afterlike.openutils.module.impl.client.GuiModule;
import org.lwjgl.input.Keyboard;

public class ClickGuiScreen extends GuiScreen {
	public static ArrayList<CategoryPanel> categoryPanels;
	public ClickGuiScreen() {
		categoryPanels = new ArrayList<>();
		int y = 5;
		for (final ModuleCategory category : ModuleCategory.values()) {
			final CategoryPanel panel = new CategoryPanel(category);
			panel.setY(y);
			categoryPanels.add(panel);
			y += 20;
		}
		OpenUtils.get().getConfigHandler().applyPanels(categoryPanels);
	}

	public void initGui() {
		super.initGui();
	}

	public void drawScreen(final int x, final int y, final float partialTicks) {
		if (categoryPanels == null)
			return;
		if (Objects.requireNonNull(OpenUtils.get().getModuleHandler()
				.getModuleClass(GuiModule.class).getSetting("Background", BooleanSetting.class))
				.getValue()) {
			drawDefaultBackground();
		}
		for (final CategoryPanel panel : categoryPanels) {
			panel.renderPanel(this.fontRendererObj);
			panel.updateDragPosition(x, y);
			for (final Component component : panel.getComponents()) {
				component.update(x, y);
			}
		}
	}

	public void mouseClicked(final int x, final int y, final int mouseButton) {
		if (categoryPanels == null)
			return;
		for (final CategoryPanel panel : categoryPanels) {
			if (panel.isHeaderHovered(x, y) && mouseButton == 0) {
				panel.setDragging(true);
				panel.setDragOffset(x - panel.getX(), y - panel.getY());
			}
			if ((panel.isExpandToggleHovered(x, y) && mouseButton == 0)
					|| (panel.isHeaderHovered(x, y) && mouseButton == 1)) {
				panel.setExpanded(!panel.isExpanded());
			}
			if (panel.isExpanded() && !panel.getComponents().isEmpty()) {
				for (final Component component : panel.getComponents()) {
					component.onClick(x, y, mouseButton);
				}
			}
		}
	}

	public void mouseReleased(final int x, final int y, final int state) {
		if (state == 0) {
			if (categoryPanels != null) {
				for (final CategoryPanel panel : categoryPanels) {
					panel.setDragging(false);
				}
				for (final CategoryPanel panel : categoryPanels) {
					if (panel.isExpanded() && !panel.getComponents().isEmpty()) {
						for (final Component component : panel.getComponents()) {
							component.onMouseRelease(x, y, state);
						}
					}
				}
			}
		}
		OpenUtils.get().getConfigHandler().saveConfiguration();
	}

	public void keyTyped(final char typedChar, final int keyCode) {
		final boolean bindingInProgress = this.isBinding();
		if (categoryPanels != null) {
			for (final CategoryPanel panel : categoryPanels) {
				if (panel.isExpanded() && !panel.getComponents().isEmpty()) {
					for (final Component component : panel.getComponents()) {
						component.onKeyTyped(typedChar, keyCode);
					}
				}
			}
		}
		if (keyCode == Keyboard.KEY_ESCAPE && !bindingInProgress) {
			this.mc.displayGuiScreen(null);
		}
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void onGuiClosed() {
		OpenUtils.get().getConfigHandler().saveConfiguration();
	}

	private boolean isBinding() {
		if (categoryPanels == null)
			return false;
		for (final CategoryPanel panel : categoryPanels) {
			if (panel.isExpanded() && !panel.getComponents().isEmpty()) {
				for (final Component component : panel.getComponents()) {
					if (component instanceof ModuleComponent
							&& ((ModuleComponent) component).isBinding()) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
