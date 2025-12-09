package org.afterlike.openutils.module.impl.movement;

import org.afterlike.openutils.event.api.EventPhase;
import org.afterlike.openutils.event.handler.EventHandler;
import org.afterlike.openutils.event.impl.GameTickEvent;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.platform.mixin.minecraft.client.settings.KeyBindingAccessor;
import org.afterlike.openutils.util.client.ClientUtil;
import org.jetbrains.annotations.NotNull;

public class SprintModule extends Module {
	public SprintModule() {
		super("Sprint", ModuleCategory.MOVEMENT);
	}

	@EventHandler
	private void onTick(final @NotNull GameTickEvent event) {
		if (event.getPhase() != EventPhase.PRE)
			return;
		if (!ClientUtil.notNull())
			return;
		((KeyBindingAccessor) mc.gameSettings.keyBindSprint).ou$setPressed(true);
	}

	@Override
	public void onDisable() {
		if (!ClientUtil.notNull())
			return;
		((KeyBindingAccessor) mc.gameSettings.keyBindSprint).ou$setPressed(false);
	}
}
