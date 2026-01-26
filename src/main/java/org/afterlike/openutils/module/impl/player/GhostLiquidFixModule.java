package org.afterlike.openutils.module.impl.player;

import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.DescriptionSetting;

public class GhostLiquidFixModule extends Module {
	private final DescriptionSetting description;
	public GhostLiquidFixModule() {
		super("Ghost Liquid Fix", ModuleCategory.PLAYER);
		description = this.registerSetting(new DescriptionSetting(
				"Fixes issues where water or lava disappears or glitches immediately after being placed"));
	}
}
