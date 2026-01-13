package org.afterlike.openutils.module.impl.client;

import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.gui.ClickGuiScreen;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.afterlike.openutils.module.api.setting.impl.ModeSetting;
import org.afterlike.openutils.util.client.ClientUtil;
import org.lwjgl.input.Keyboard;

public class GuiModule extends Module {
	private final ModeSetting theme;
	private final BooleanSetting background;
	public GuiModule() {
		super("GUI", ModuleCategory.CLIENT, Keyboard.KEY_RSHIFT);
		background = this.registerSetting(new BooleanSetting("Background", true));
		theme = this.registerSetting(
				new ModeSetting("Theme", "raven b3", "raven b1", "raven b2", "raven b3"));
	}

	@Override
	protected void onEnable() {
		if (ClientUtil.notNull() && !(mc.currentScreen instanceof ClickGuiScreen)) {
			final ClickGuiScreen screen = OpenUtils.get().getClickGuiScreen();
			mc.displayGuiScreen(screen);
		}
		this.setEnabled(false);
	}
}
