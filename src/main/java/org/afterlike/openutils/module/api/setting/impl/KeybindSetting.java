package org.afterlike.openutils.module.api.setting.impl;

import org.afterlike.openutils.module.api.setting.Setting;
import org.lwjgl.input.Keyboard;

public final class KeybindSetting extends Setting<Integer> {
	public KeybindSetting(String name, int defaultKey) {
		super(name);
		this.value = defaultKey;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public void setValue(Integer value) {
		super.setValue(value);
	}

	public String getDisplayName() {
		return Keyboard.getKeyName(value);
	}

	@Override
	public Object serializeValue() {
		return value;
	}

	@Override
	public void deserializeValue(Object raw) {
		if (raw instanceof Number) {
			setValue(((Number) raw).intValue());
		}
	}
}
