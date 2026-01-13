package org.afterlike.openutils.gui.component.impl;

import java.awt.*;
import java.util.List;
import org.afterlike.openutils.gui.component.Component;
import org.afterlike.openutils.module.api.setting.impl.DescriptionSetting;
import org.lwjgl.opengl.GL11;

public class DescriptionComponent extends Component {
	private final int textColor = new Color(226, 83, 47).getRGB();
	private final DescriptionSetting description;
	private final ModuleComponent parent;
	private int yOffset;
	public DescriptionComponent(final DescriptionSetting desc, final ModuleComponent parent,
			final int yOffset) {
		this.description = desc;
		this.parent = parent;
		this.yOffset = yOffset;
	}

	@Override
	public void render() {
		final List<String> lines = getWrappedLines();
		final int lineHeight = getLineHeight();
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		int y = (this.parent.panel.getY() + this.yOffset + 4) * 2;
		final int x = (this.parent.panel.getX() + 4) * 2;
		for (final String line : lines) {
			mc.fontRendererObj.drawString(line, x, y, this.textColor, true);
			y += lineHeight * 2;
		}
		GL11.glPopMatrix();
	}

	@Override
	public void setOffset(final int yOffset) {
		this.yOffset = yOffset;
	}

	@Override
	public int getHeight() {
		final int lineHeight = getLineHeight();
		final int lines = getWrappedLines().size();
		return Math.max(12, lines * lineHeight + 2);
	}

	private int getLineHeight() {
		return (int) Math.ceil(mc.fontRendererObj.FONT_HEIGHT * 0.5F) + 1;
	}

	private List<String> getWrappedLines() {
		int availableWidth = (this.parent.panel.getWidth() - 8) * 2;
		if (availableWidth <= 0) {
			availableWidth = 100;
		}
		return mc.fontRendererObj.listFormattedStringToWidth(this.description.getValue(),
				availableWidth);
	}
}
