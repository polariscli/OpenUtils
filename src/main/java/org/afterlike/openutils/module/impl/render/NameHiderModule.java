package org.afterlike.openutils.module.impl.render;

import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.DescriptionSetting;

public class NameHiderModule extends Module {
	private final DescriptionSetting todo;
	public NameHiderModule() {
		super("Name Hider", ModuleCategory.RENDER);
		todo = this.registerSetting(new DescriptionSetting("Not implemented yet"));
	}
}
