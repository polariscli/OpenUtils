package org.afterlike.openutils.module.api.hud;

import net.minecraft.client.Minecraft;
import org.afterlike.openutils.module.api.setting.Setting;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HudModule {
	@NotNull Position getHudPosition();

	@NotNull BooleanSetting getHudEditSetting();

	@NotNull String getHudPlaceholderText();

	default boolean useHudDropShadow() {
		return true;
	}

	default void openHudEditor() {
		Minecraft.getMinecraft()
				.displayGuiScreen(new EditorGuiScreen(this, getHudPlaceholderText()));
	}

	default void handleHudSettingChanged(@Nullable final Setting<?> setting) {
		if (setting == null) {
			return;
		}
		if (setting == getHudEditSetting()) {
			getHudEditSetting().disable();
			openHudEditor();
		}
	}
}
