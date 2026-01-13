package org.afterlike.openutils.module.impl.render;

import net.minecraft.util.ResourceLocation;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.Setting;
import org.afterlike.openutils.module.api.setting.impl.ModeSetting;

public class CapeModule extends Module {
	private final ModeSetting cape;
	private ResourceLocation location;
	public CapeModule() {
		super("Cape", ModuleCategory.RENDER);
		cape = this.registerSetting(
				new ModeSetting("Cape", "2016", "2011", "2012", "2013", "2015", "2016", "daisy"));
	}

	@Override
	protected void onEnable() {
		setLocation();
	}

	@Override
	public void onSettingChanged(Setting<?> setting) {
		setLocation();
	}

	private void setLocation() {
		location = new ResourceLocation("openutils", "capes/" + cape.getValue() + ".png");
	}

	public ResourceLocation getCapeLocation() {
		return location;
	}
}
