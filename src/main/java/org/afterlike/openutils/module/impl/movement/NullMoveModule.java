package org.afterlike.openutils.module.impl.movement;

import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.DescriptionSetting;

public class NullMoveModule extends Module {
	private final DescriptionSetting description;
	private long leftLastPressTime;
	private long rightLastPressTime;
	private long forwardLastPressTime;
	private long backwardLastPressTime;
	public NullMoveModule() {
		super("NullMove", ModuleCategory.MOVEMENT);
		description = this.registerSetting(new DescriptionSetting(
				"Prevents opposite movement inputs from canceling each other"));
	}
}
