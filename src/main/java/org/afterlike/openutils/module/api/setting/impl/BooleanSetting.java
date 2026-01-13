package org.afterlike.openutils.module.api.setting.impl;

import org.afterlike.openutils.module.api.setting.Setting;

public class BooleanSetting extends Setting<Boolean> {
	public BooleanSetting(final String label, final boolean value) {
		super(label);
		this.value = value;
	}

	@Override
	public Boolean getValue() {
		return this.value;
	}

	@Override
	public void setValue(Boolean value) {
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
	public Object serializeValue() {
		return value;
	}

	@Override
	public void deserializeValue(Object raw) {
		if (raw instanceof Boolean) {
			setValue((Boolean) raw);
		}
	}
}
