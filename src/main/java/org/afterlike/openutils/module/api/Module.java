package org.afterlike.openutils.module.api;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.module.api.setting.Setting;
import org.afterlike.openutils.module.api.setting.impl.KeybindSetting;
import org.lwjgl.input.Keyboard;

public class Module {
	protected static final Minecraft mc = Minecraft.getMinecraft();
	protected final List<Setting<?>> settings;
	private final String name;
	private final ModuleCategory category;
	private boolean enabled;
	private final KeybindSetting keybindSetting;
	public Module(String name, ModuleCategory category, int keyCode) {
		this.name = name;
		this.category = category;
		this.enabled = false;
		this.settings = new ArrayList<>();
		this.keybindSetting = new KeybindSetting("Keybind", keyCode);
		registerSetting(this.keybindSetting);
	}

	public Module(String name, ModuleCategory category) {
		this(name, category, Keyboard.KEY_NONE);
	}

	protected final <T extends Setting<?>> T registerSetting(T setting) {
		this.settings.add(setting);
		return setting;
	}

	public List<Setting<?>> getSettings() {
		return this.settings;
	}

	@SuppressWarnings("unchecked")
	private <S extends Setting<?>> S getSetting(String name) {
		for (Setting<?> setting : settings) {
			if (setting.getName().equalsIgnoreCase(name)) {
				return (S) setting;
			}
		}
		return null;
	}

	public <S extends Setting<?>> S getSetting(final String name, final Class<S> type) {
		final Setting<?> setting = getSetting(name);
		if (type.isInstance(setting)) {
			return type.cast(setting);
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

	public String getName() {
		return name;
	}

	public ModuleCategory getCategory() {
		return category;
	}

	public int getKeybind() {
		return keybindSetting.getValue();
	}

	public void setKeybind(int keybind) {
		keybindSetting.setValue(keybind);
	}

	public KeybindSetting getKeybindSetting() {
		return keybindSetting;
	}

	// override the following if needed
	protected void onEnable() {
	}

	protected void onDisable() {
	}

	public void onSettingChanged(Setting<?> setting) {
		OpenUtils.get().getConfigHandler().saveConfiguration();
	}
}
