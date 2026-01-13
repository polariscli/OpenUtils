package org.afterlike.openutils.module.impl.world;

import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.DescriptionSetting;

public class WeatherModule extends Module {
	private final DescriptionSetting todo;
	public WeatherModule() {
		super("Weather", ModuleCategory.WORLD);
		todo = this.registerSetting(new DescriptionSetting("Not implemented yet"));
	}
}
