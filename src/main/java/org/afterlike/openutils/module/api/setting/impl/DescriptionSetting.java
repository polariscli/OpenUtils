package org.afterlike.openutils.module.api.setting.impl;

import org.afterlike.openutils.module.api.setting.Setting;

public class DescriptionSetting extends Setting<String> {
	public DescriptionSetting(final String value) {
		super(value == null ? "" : value);
		this.value = value;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
	}

	@Override
	public Object serializeValue() {
		return null;
	}

	@Override
	public void deserializeValue(Object raw) {
		// ignore
	}
}
