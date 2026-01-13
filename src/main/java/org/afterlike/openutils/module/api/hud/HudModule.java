package org.afterlike.openutils.module.api.hud;

import net.minecraft.client.Minecraft;
import org.afterlike.openutils.gui.EditorGuiScreen;
import org.afterlike.openutils.module.api.setting.Setting;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;

public interface HudModule {
	Position getHudPosition();

	BooleanSetting getHudEditSetting();

	String getHudPlaceholderText();

	default boolean useHudDropShadow() {
		return true;
	}

	default void openHudEditor() {
		Minecraft.getMinecraft()
				.displayGuiScreen(new EditorGuiScreen(this, getHudPlaceholderText()));
	}

	default void handleHudSettingChanged(final Setting<?> setting) {
		if (setting == null) {
			return;
		}
		if (setting == getHudEditSetting()) {
			getHudEditSetting().disable();
			openHudEditor();
		}
	}
}
