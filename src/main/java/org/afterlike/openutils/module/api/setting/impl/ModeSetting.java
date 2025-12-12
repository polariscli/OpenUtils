package org.afterlike.openutils.module.api.setting.impl;

import java.util.Arrays;
import java.util.List;
import org.afterlike.openutils.module.api.setting.Setting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModeSetting extends Setting<String> {
	private final @NotNull List<@NotNull String> modes;
	private int index;
	public ModeSetting(@NotNull String name, @NotNull String defaultMode,
			@NotNull String... modes) {
		super(name);
		this.modes = Arrays.asList(modes);
		this.index = Math.max(0, this.modes.indexOf(defaultMode));
		this.value = this.modes.isEmpty() ? "" : this.modes.get(this.index);
	}

	public @NotNull List<@NotNull String> getModes() {
		return modes;
	}

	@Override
	public @NotNull String getValue() {
		return value;
	}

	@Override
	public void setValue(@NotNull String mode) {
		int found = modes.indexOf(mode);
		if (found >= 0) {
			setIndex(found);
		}
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		if (modes.isEmpty()) {
			this.index = 0;
			this.value = "";
			return;
		}
		if (index < 0) {
			index = 0;
		} else if (index >= modes.size()) {
			index = modes.size() - 1;
		}
		this.index = index;
		this.value = modes.get(index);
	}

	public void next() {
		setIndex((index + 1) % modes.size());
	}

	public void previous() {
		setIndex((index - 1 + modes.size()) % modes.size());
	}

	@Override
	public @NotNull Object serializeValue() {
		return value;
	}

	@Override
	public void deserializeValue(@Nullable Object raw) {
		if (raw instanceof String) {
			setValue((String) raw);
		}
	}
}
