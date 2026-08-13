package org.afterlike.openutils.util.client;

import net.minecraft.client.Minecraft;

public final class SoundUtil {
	private static final Minecraft mc = Minecraft.getMinecraft();
	private SoundUtil() {
	}

	public static void playSound(final String soundName, final float volume, final float pitch) {
		mc.addScheduledTask(() -> {
			if (ClientUtil.notNull()) {
				mc.thePlayer.playSound(soundName, volume, pitch);
			}
		});
	}
}
