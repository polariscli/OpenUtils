package org.afterlike.openutils.module.api.setting.impl;

import org.afterlike.openutils.module.api.setting.Setting;

public class TextFieldSetting extends Setting<String> {
	private final String placeholder;
	public TextFieldSetting(String name, String defaultValue, String placeholder) {
		super(name);
		this.value = defaultValue != null ? defaultValue : "";
		this.placeholder = placeholder != null ? placeholder : "";
	}

	public TextFieldSetting(String name, String defaultValue) {
		this(name, defaultValue, "");
	}

	public String getPlaceholder() {
		return placeholder;
	}

	@Override
	public void setValue(String value) {
		this.value = value != null ? value : "";
	}

	@Override
	public Object serializeValue() {
		return value;
	}

	@Override
	public void deserializeValue(Object raw) {
		if (raw instanceof String) {
			this.value = (String) raw;
		}
	}
}
