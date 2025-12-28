package org.afterlike.openutils.module.impl.render;

import net.minecraft.potion.Potion;
import org.afterlike.openutils.event.handler.EventHandler;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;

public class AntiDebuffModule extends Module {
	private final BooleanSetting nausea;
	private final BooleanSetting blindness;
	public AntiDebuffModule() {
		super("Anti Debuff", ModuleCategory.RENDER);
		nausea = this.registerSetting(new BooleanSetting("Remove nausea", true));
		blindness = this.registerSetting(new BooleanSetting("Remove blindness", true));
	}

	@EventHandler
	private void onUpdate() {
		if (mc.thePlayer == null) return;

		if (nausea.getValue() && mc.thePlayer.isPotionActive(Potion.confusion)) {
			mc.thePlayer.removePotionEffectClient(Potion.confusion.getId());
		}

		if (blindness.getValue() && mc.thePlayer.isPotionActive(Potion.blindness)) {
			mc.thePlayer.removePotionEffectClient(Potion.blindness.getId());
		}
	}
}
