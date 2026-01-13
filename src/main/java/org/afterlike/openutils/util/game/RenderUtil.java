package org.afterlike.openutils.util.game;

import java.awt.*;
import net.minecraft.client.gui.FontRenderer;

public class RenderUtil {
	public static void drawChromaText(final String text, final char lineSplit, int x, int y,
			final long speed, final long shift, final boolean shadow,
			final FontRenderer fontRenderer) {
		final int bX = x;
		int l = 0;
		long r = 0L;
		for (int i = 0; i < text.length(); i++) {
			final char c = text.charAt(i);
			if (c == lineSplit) {
				l++;
				x = bX;
				y += fontRenderer.FONT_HEIGHT + 5;
				r = shift * l;
			} else {
				fontRenderer.drawString(String.valueOf(c), x, y, getChromaColor(speed, r), shadow);
				x += fontRenderer.getCharWidth(c);
				if (c != ' ') {
					r -= 90L;
				}
			}
		}
	}

	public static int getChromaColor(final long speed, final long... delay) {
		final long time = System.currentTimeMillis() + (delay.length > 0 ? delay[0] : 0L);
		return Color.getHSBColor((float) (time % (15000L / speed)) / (15000.0F / (float) speed),
				1.0F, 1.0F).getRGB();
	}
}
