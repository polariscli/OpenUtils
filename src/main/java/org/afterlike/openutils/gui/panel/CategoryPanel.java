package org.afterlike.openutils.gui.panel;

import java.awt.*;
import java.util.ArrayList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.gui.component.Component;
import org.afterlike.openutils.gui.component.impl.BooleanComponent;
import org.afterlike.openutils.gui.component.impl.ModuleComponent;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.lwjgl.opengl.GL11;

public class CategoryPanel {
	private final ArrayList<Component> components;
	private final ModuleCategory category;
	private boolean expanded;
	private int width;
	private int y;
	private int x;
	private final int headerHeight;
	private boolean dragging;
	private int dragOffsetX;
	private int dragOffsetY;
	private final int chromaSpeed;
	public CategoryPanel(final ModuleCategory category) {
		this.components = new ArrayList<>();
		this.category = category;
		this.width = 92;
		this.x = 5;
		this.y = 5;
		this.headerHeight = 13;
		this.dragOffsetX = 0;
		this.dragOffsetY = 0;
		this.expanded = false;
		this.dragging = false;
		this.chromaSpeed = 3;
		int offsetY = this.headerHeight + 3;
		for (final Module module : OpenUtils.get().getModuleHandler()
				.getModulesInCategory(this.category)) {
			final ModuleComponent moduleComponent = new ModuleComponent(module, this, offsetY);
			this.components.add(moduleComponent);
			offsetY += 16;
		}
	}

	public ArrayList<Component> getComponents() {
		return this.components;
	}

	public void setX(final int x) {
		this.x = x;
	}

	public void setY(final int y) {
		this.y = y;
	}

	public void setDragOffset(final int xOffset, final int yOffset) {
		this.dragOffsetX = xOffset;
		this.dragOffsetY = yOffset;
	}

	public void setDragging(final boolean dragging) {
		this.dragging = dragging;
	}

	public boolean isExpanded() {
		return this.expanded;
	}

	public void setExpanded(final boolean expanded) {
		this.expanded = expanded;
	}

	public void renderPanel(final FontRenderer renderer) {
		this.width = 92;
		if (!this.components.isEmpty() && this.expanded) {
			int h = 0;
			for (final Component c : this.components) {
				h += c.getHeight();
			}
			final int bg = new Color(0, 0, 0, 110).getRGB();
			Gui.drawRect(this.x - 2, this.y, this.x + this.width + 2,
					this.y + this.headerHeight + h + 4, bg);
		}
		BooleanComponent.drawQuadWithAlpha(this.x - 2, this.y, this.x + this.width + 2,
				this.y + this.headerHeight + 3, -1);
		renderer.drawString(this.category.name().toLowerCase(), this.x + 2, this.y + 4,
				Color.getHSBColor((float) (System.currentTimeMillis() % (7500L / this.chromaSpeed))
						/ (7500.0F / this.chromaSpeed), 1.0F, 1.0F).getRGB(),
				false);
		GL11.glPushMatrix();
		renderer.drawString(this.expanded ? "-" : "+", this.x + 80, (float) (this.y + 4.5),
				Color.white.getRGB(), false);
		GL11.glPopMatrix();
		if (this.expanded && !this.components.isEmpty()) {
			for (final Component component : this.components) {
				component.render();
			}
		}
	}

	public void layoutComponents() {
		int offsetY = this.headerHeight + 3;
		for (final Component component : this.components) {
			component.setOffset(offsetY);
			offsetY += component.getHeight();
		}
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getWidth() {
		return this.width;
	}

	public ModuleCategory getCategory() {
		return this.category;
	}

	public void updateDragPosition(final int x, final int y) {
		if (this.dragging) {
			this.setX(x - this.dragOffsetX);
			this.setY(y - this.dragOffsetY);
		}
	}

	public boolean isExpandToggleHovered(final int x, final int y) {
		return x >= this.x + 77 && x <= this.x + this.width - 6 && y >= this.y + 2.0F
				&& y <= this.y + this.headerHeight + 1;
	}

	public boolean isHeaderHovered(final int x, final int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y
				&& y <= this.y + this.headerHeight;
	}
}
