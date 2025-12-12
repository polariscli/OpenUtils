package org.afterlike.openutils.module.api.setting.impl;

import org.afterlike.openutils.module.api.setting.Setting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DescriptionSetting extends Setting<String> {
	public DescriptionSetting(@Nullable final String value) {
		super(value == null ? "" : value);
		this.value = value;
	}

	@Override
	public @Nullable String getValue() {
		return this.value;
	}

	@Override
	public void setValue(@NotNull String value) {
		super.setValue(value);
	}

	@Override
	public @Nullable Object serializeValue() {
		return null;
	}

	@Override
	public void deserializeValue(@Nullable Object raw) {
		// ignore
	}
}
