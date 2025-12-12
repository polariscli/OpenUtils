package org.afterlike.openutils.module.api.setting.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.afterlike.openutils.module.api.setting.Setting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NumberSetting extends Setting<Double> {
	private final double max;
	private final double min;
	private final double step;
	public NumberSetting(@NotNull String name, double defaultValue, double min, double max,
			double step) {
		super(name);
		this.min = min;
		this.max = max;
		this.step = step;
		setValue(defaultValue);
	}

	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public void setValue(@NotNull Double newValue) {
		newValue = clamp(newValue, min, max);
		newValue = snap(newValue, step);
		this.value = newValue;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double getStep() {
		return step;
	}

	public int getInt() {
		return value.intValue();
	}

	public float getFloat() {
		return value.floatValue();
	}

	public String getDisplayValue() {
		return String.valueOf(round(value, 2));
	}

	@Override
	public @NotNull Object serializeValue() {
		return value;
	}

	@Override
	public void deserializeValue(@Nullable Object raw) {
		if (raw instanceof Number) {
			setValue(((Number) raw).doubleValue());
		}
	}

	private static double snap(double value, double step) {
		if (step <= 0.0)
			return value;
		return Math.round(value / step) * step;
	}

	private static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	private static double round(double value, int places) {
		if (places < 0)
			return value;
		return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
	}
}
