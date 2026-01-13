package org.afterlike.openutils.module.impl.hypixel;

import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.DescriptionSetting;

public class NickBotModule extends Module {
	private final DescriptionSetting todo;
	public NickBotModule() {
		super("Nick Bot", ModuleCategory.HYPIXEL);
		todo = this.registerSetting(new DescriptionSetting("Not implemented yet"));
	}
}
