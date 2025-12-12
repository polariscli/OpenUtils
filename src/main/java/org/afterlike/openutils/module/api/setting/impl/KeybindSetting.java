package org.afterlike.openutils.module.api.setting.impl;

import org.afterlike.openutils.module.api.setting.Setting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

public final class KeybindSetting extends Setting<Integer> {
	public KeybindSetting(@NotNull String name, int defaultKey) {
		super(name);
		this.value = defaultKey;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public void setValue(@NotNull Integer value) {
		super.setValue(value);
	}

	public @NotNull String getDisplayName() {
		return Keyboard.getKeyName(value);
	}

	@Override
	public @NotNull Object serializeValue() {
		return value;
	}

	@Override
	public void deserializeValue(@Nullable Object raw) {
		if (raw instanceof Number) {
			setValue(((Number) raw).intValue());
		}
	}
}
