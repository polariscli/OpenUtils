package org.afterlike.openutils.module.api.setting;

public class Setting<T> {
	protected final String name;
	protected T value;
	public Setting(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public Object serializeValue() {
		return value;
	}

	@SuppressWarnings("unchecked")
	public void deserializeValue(Object raw) {
		if (raw == null)
			return;
		try {
			this.value = (T) raw;
		} catch (ClassCastException ignored) {
		}
	}
}
