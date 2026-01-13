package org.afterlike.openutils.gui.component.impl;

import java.awt.*;
import org.afterlike.openutils.gui.component.Component;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.lwjgl.opengl.GL11;

public class BooleanComponent extends Component {
	private final int enabledColor = new Color(20, 255, 0).getRGB();
	private final Module module;
	private final BooleanSetting setting;
	private final ModuleComponent parent;
	private int yOffset;
	private int x;
	private int y;
	public BooleanComponent(final Module module, final BooleanSetting setting,
			final ModuleComponent parent, final int yOffset) {
		this.module = module;
		this.setting = setting;
		this.parent = parent;
		this.x = parent.panel.getX() + parent.panel.getWidth();
		this.y = parent.panel.getY() + parent.yOffset;
		this.yOffset = yOffset;
	}

	public static void enableRenderState() {
		GL11.glDisable(2929);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glDepthMask(true);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
	}

	public static void disableRenderState() {
		GL11.glEnable(3553);
		GL11.glEnable(2929);
		GL11.glDisable(2848);
		GL11.glHint(3154, 4352);
		GL11.glHint(3155, 4352);
	}

	public static void drawQuadWithAlpha(final float x, final float y, final float x1,
			final float y1, final int color) {
		enableRenderState();
		applyAlphaColor(color);
		drawQuad(x, y, x1, y1);
		disableRenderState();
	}

	public static void drawQuad(final float x, final float y, final float x1, final float y1) {
		GL11.glBegin(7);
		GL11.glVertex2f(x, y1);
		GL11.glVertex2f(x1, y1);
		GL11.glVertex2f(x1, y);
		GL11.glVertex2f(x, y);
		GL11.glEnd();
	}

	public static void applyAlphaColor(final int color) {
		final float alpha = (color >> 24 & 0xFF) / 350.0F;
		GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
	}

	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		mc.fontRendererObj.drawString(
				this.setting.getValue()
						? "[+]  " + this.setting.getName()
						: "[-]  " + this.setting.getName(),
				(this.parent.panel.getX() + 4) * 2,
				(this.parent.panel.getY() + this.yOffset + 4) * 2,
				this.setting.getValue() ? this.enabledColor : -1, false);
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
	}

	@Override
	public void onClick(final int x, final int y, final int button) {
		if (this.isMouseOver(x, y) && button == 0 && this.parent.expandedSettings) {
			this.setting.toggle();
			this.module.onSettingChanged(this.setting);
		}
	}

	public boolean isMouseOver(final int x, final int y) {
		return x > this.x && x < this.x + this.parent.panel.getWidth() && y > this.y
				&& y < this.y + 11;
	}

	@Override
	public int getHeight() {
		return 12;
	}
}
