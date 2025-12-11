package org.afterlike.openutils.config.api;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModuleEntry {
	public boolean enabled;
	public int keybind;
	public @Nullable Map<@NotNull String, @Nullable Object> settings;
	public @Nullable HudEntry hud;
}
