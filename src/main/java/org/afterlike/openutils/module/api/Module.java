package org.afterlike.openutils.module.api;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.module.api.setting.Setting;
import org.afterlike.openutils.module.api.setting.impl.KeybindSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

public class Module {
	protected static final @NotNull Minecraft mc = Minecraft.getMinecraft();
	protected final @NotNull List<@NotNull Setting<?>> settings;
	private final @NotNull String name;
	private final @NotNull ModuleCategory category;
	private boolean enabled;
	private final @NotNull KeybindSetting keybindSetting;
	public Module(@NotNull String name, @NotNull ModuleCategory category, int keyCode) {
		this.name = name;
		this.category = category;
		this.enabled = false;
		this.settings = new ArrayList<>();
		this.keybindSetting = new KeybindSetting("Keybind", keyCode);
		registerSetting(this.keybindSetting);
	}

	public Module(@NotNull String name, @NotNull ModuleCategory category) {
		this(name, category, Keyboard.KEY_NONE);
	}

	protected final <T extends Setting<?>> T registerSetting(@NotNull T setting) {
		this.settings.add(setting);
		return setting;
	}

	public @NotNull List<@NotNull Setting<?>> getSettings() {
		return this.settings;
	}

	@SuppressWarnings("unchecked")
	public <S extends Setting<?>> @Nullable S getSetting(String name) {
		for (Setting<?> setting : settings) {
			if (setting.getName().equalsIgnoreCase(name)) {
				return (S) setting;
			}
		}
		return null;
	}

	public void toggle() {
		setEnabled(!enabled);
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled)
			return;
		this.enabled = enabled;
		if (enabled) {
			onEnable();
			OpenUtils.get().getEventBus().subscribe(this);
		} else {
			onDisable();
			OpenUtils.get().getEventBus().unsubscribe(this);
		}
		OpenUtils.get().getConfigHandler().saveConfiguration();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public @NotNull String getName() {
		return name;
	}

	public @NotNull ModuleCategory getCategory() {
		return category;
	}

	public int getKeybind() {
		return keybindSetting.getValue();
	}

	public void setKeybind(int keybind) {
		keybindSetting.setValue(keybind);
	}

	public @NotNull KeybindSetting getKeybindSetting() {
		return keybindSetting;
	}

	// override the following if needed
	protected void onEnable() {
	}

	protected void onDisable() {
	}

	public void onSettingChanged(@Nullable Setting<?> setting) {
		OpenUtils.get().getConfigHandler().saveConfiguration();
	}
}
