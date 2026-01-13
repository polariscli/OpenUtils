package org.afterlike.openutils.util.client;

import net.minecraft.client.Minecraft;

public class TextUtil {
	private static final Minecraft mc = Minecraft.getMinecraft();
	public static String replaceColorCodes(final String message) {
		return message.replaceAll("&", "§");
	}

	public static String stripAliens(final String text) {
		StringBuilder sb = new StringBuilder();
		for (char c : text.toCharArray()) {
			if (mc.fontRendererObj.getCharWidth(c) > 0 || c == '§') {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String stripColorCodes(final String text) {
		StringBuilder sb = new StringBuilder(text.length());
		boolean skip = false;
		for (char c : text.toCharArray()) {
			if (skip) {
				skip = false;
				continue;
			}
			if (c == '§') {
				skip = true;
				continue;
			}
			sb.append(c);
		}
		return sb.toString();
	}
}
