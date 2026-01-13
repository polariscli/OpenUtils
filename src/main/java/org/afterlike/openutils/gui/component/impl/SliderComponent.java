package org.afterlike.openutils.gui.component.impl;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.client.gui.Gui;
import org.afterlike.openutils.gui.component.Component;
import org.afterlike.openutils.module.api.setting.impl.ModeSetting;
import org.afterlike.openutils.module.api.setting.impl.NumberSetting;
import org.lwjgl.opengl.GL11;

public class SliderComponent extends Component {
	private final NumberSetting numberSetting;
	private final ModeSetting modeSetting;
	private final ModuleComponent parent;
	private int yOffset;
	private int x;
	private int y;
	private boolean dragging;
	private double width;
	public SliderComponent(final NumberSetting setting, final ModuleComponent parent,
			final int yOffset) {
		this(setting, null, parent, yOffset);
	}

	public SliderComponent(final ModeSetting setting, final ModuleComponent parent,
			final int yOffset) {
		this(null, setting, parent, yOffset);
	}

	private SliderComponent(final NumberSetting numberSetting, final ModeSetting modeSetting,
			final ModuleComponent parent, final int yOffset) {
		this.dragging = false;
		this.numberSetting = numberSetting;
		this.modeSetting = modeSetting;
		this.parent = parent;
		this.x = parent.panel.getX() + parent.panel.getWidth();
		this.y = parent.panel.getY() + parent.yOffset;
		this.yOffset = yOffset;
	}

	@Override
	public void render() {
		Gui.drawRect(this.parent.panel.getX() + 4, this.parent.panel.getY() + this.yOffset + 11,
				this.parent.panel.getX() + 4 + this.parent.panel.getWidth() - 8,
				this.parent.panel.getY() + this.yOffset + 15, -12302777);
		final int l = this.parent.panel.getX() + 4;
		int r = this.parent.panel.getX() + 4 + (int) this.width;
		final int maxSliderLength = 84;
		if (r - l > maxSliderLength) {
			r = l + maxSliderLength;
		}
		Gui.drawRect(l, this.parent.panel.getY() + this.yOffset + 11, r,
				this.parent.panel.getY() + this.yOffset + 15,
				Color.getHSBColor((float) (System.currentTimeMillis() % 11000L) / 11000.0F, 0.75F,
						0.9F).getRGB());
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		mc.fontRendererObj.drawStringWithShadow(this.getLabel(),
				(int) ((this.parent.panel.getX() + 4) * 2.0F),
				(int) ((this.parent.panel.getY() + this.yOffset + 3) * 2.0F), -1);
		GL11.glPopMatrix();
	}

	@Override
	public void setOffset(final int yOffset) {
		this.yOffset = yOffset;
	}

	@Override
	public void update(final int x, final int y) {
		this.y = this.parent.panel.getY() + this.yOffset;
		this.x = this.parent.panel.getX();
		final double clampedMouse = Math.min(this.parent.panel.getWidth() - 8,
				Math.max(0, x - this.x));
		if (numberSetting != null) {
			this.width = (this.parent.panel.getWidth() - 8)
					* (numberSetting.getValue() - numberSetting.getMin())
					/ Math.max(0.0001, numberSetting.getMax() - numberSetting.getMin());
			if (this.dragging) {
				final double before = numberSetting.getValue();
				if (clampedMouse == 0.0) {
					this.numberSetting.setValue(this.numberSetting.getMin());
				} else {
					final double newValue = roundToDecimals(
							clampedMouse / (this.parent.panel.getWidth() - 8)
									* (this.numberSetting.getMax() - this.numberSetting.getMin())
									+ this.numberSetting.getMin(),
							3);
					this.numberSetting.setValue(newValue);
				}
				if (numberSetting.getValue() != before) {
					this.parent.module.onSettingChanged(this.numberSetting);
				}
			}
		} else if (modeSetting != null) {
			final int max = Math.max(1, this.modeSetting.getModes().size() - 1);
			this.width = (this.parent.panel.getWidth() - 8) * (double) this.modeSetting.getIndex()
					/ max;
			if (this.dragging) {
				final int before = this.modeSetting.getIndex();
				final int newIndex = (int) Math
						.round((clampedMouse / (this.parent.panel.getWidth() - 8)) * max);
				this.modeSetting.setIndex(newIndex);
				if (this.modeSetting.getIndex() != before) {
					this.parent.module.onSettingChanged(this.modeSetting);
				}
			}
		}
	}

	private static double roundToDecimals(final double value, final int precision) {
		if (precision < 0) {
			return 0.0;
		} else {
			BigDecimal bd = new BigDecimal(value);
			bd = bd.setScale(precision, RoundingMode.HALF_UP);
			return bd.doubleValue();
		}
	}

	@Override
	public void onClick(final int x, final int y, final int button) {
		if (this.isLeftHalfHovered(x, y) && button == 0 && this.parent.expandedSettings) {
			this.dragging = true;
		}
		if (this.isRightHalfHovered(x, y) && button == 0 && this.parent.expandedSettings) {
			this.dragging = true;
		}
	}

	@Override
	public void onMouseRelease(final int x, final int y, final int state) {
		this.dragging = false;
	}

	private String getLabel() {
		if (numberSetting != null) {
			return numberSetting.getName() + ": " + numberSetting.getDisplayValue();
		} else if (modeSetting != null) {
			return modeSetting.getName() + ": " + modeSetting.getValue();
		}
		return "";
	}

	public boolean isLeftHalfHovered(final int x, final int y) {
		return x > this.x && x < this.x + this.parent.panel.getWidth() / 2 + 1 && y > this.y
				&& y < this.y + 16;
	}

	public boolean isRightHalfHovered(final int x, final int y) {
		return x > this.x + this.parent.panel.getWidth() / 2
				&& x < this.x + this.parent.panel.getWidth() && y > this.y && y < this.y + 16;
	}

	@Override
	public int getHeight() {
		return 16;
	}
}
