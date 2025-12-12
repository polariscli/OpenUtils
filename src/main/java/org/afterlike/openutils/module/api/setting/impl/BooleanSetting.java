package org.afterlike.openutils.module.api.setting.impl;

import org.afterlike.openutils.module.api.setting.Setting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BooleanSetting extends Setting<Boolean> {
	public BooleanSetting(@NotNull final String label, final boolean value) {
		super(label);
		this.value = value;
	}

	@Override
	public Boolean getValue() {
		return this.value;
	}

	@Override
	public void setValue(@NotNull Boolean value) {
		super.setValue(value);
	}

	public void toggle() {
		this.value = !this.value;
	}

	public void enable() {
		this.value = true;
	}

	public void disable() {
		this.value = false;
	}

	@Override
	public @NotNull Object serializeValue() {
		return value;
	}

	@Override
	public void deserializeValue(@Nullable Object raw) {
		if (raw instanceof Boolean) {
			setValue((Boolean) raw);
		}
	}
}
