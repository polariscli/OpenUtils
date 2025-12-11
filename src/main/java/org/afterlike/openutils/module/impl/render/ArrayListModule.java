package org.afterlike.openutils.module.impl.render;

import java.util.Comparator;
import java.util.List;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.event.handler.EventHandler;
import org.afterlike.openutils.event.impl.RenderOverlayEvent;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.hud.HudModule;
import org.afterlike.openutils.module.api.hud.Position;
import org.afterlike.openutils.module.api.setting.Setting;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.afterlike.openutils.util.client.ClientUtil;
import org.afterlike.openutils.util.game.RenderUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArrayListModule extends Module implements HudModule {
	private final Position position = new Position(5, 70);
	private final BooleanSetting editPosition;
	private final BooleanSetting dropShadow;
	private final BooleanSetting alphabeticalSort;
	public ArrayListModule() {
		super("Array List", ModuleCategory.RENDER);
		this.registerSetting(editPosition = new BooleanSetting("Edit position", false));
		this.registerSetting(dropShadow = new BooleanSetting("Drop shadow", true));
		this.registerSetting(alphabeticalSort = new BooleanSetting("Alphabetical sort", false));
	}

	@Override
	public void onSettingChanged(@Nullable final Setting<?> setting) {
		handleHudSettingChanged(setting);
		super.onSettingChanged(setting);
	}

	@EventHandler
	private void onRenderOverlay(@NotNull final RenderOverlayEvent event) {
		if (!ClientUtil.notNull()) {
			return;
		}
		if (mc.currentScreen != null || mc.gameSettings.showDebugInfo) {
			return;
		}
		int y = position.getY();
		int delta = 0;
		for (@NotNull final Module module : getSorted()) {
			if (module.isEnabled() && module != this) {
				mc.fontRendererObj.drawString(module.getName(), position.getX(), y,
						RenderUtil.getChromaColor(2L, delta), useHudDropShadow());
				y += mc.fontRendererObj.FONT_HEIGHT + 2;
				delta -= 120;
			}
		}
	}

	private @NotNull List<@NotNull Module> getSorted() {
		final List<@NotNull Module> enabled = OpenUtils.get().getModuleHandler()
				.getEnabledModules();
		if (alphabeticalSort.getValue()) {
			enabled.sort(Comparator.comparing(Module::getName));
		} else {
			enabled.sort((m1, m2) -> mc.fontRendererObj.getStringWidth(m2.getName())
					- mc.fontRendererObj.getStringWidth(m1.getName()));
		}
		return enabled;
	}

	@Override
	public @NotNull Position getHudPosition() {
		return position;
	}

	@Override
	public @NotNull BooleanSetting getHudEditSetting() {
		return editPosition;
	}

	@Override
	public @NotNull String getHudPlaceholderText() {
		return "This is an-Array-List";
	}

	@Override
	public boolean useHudDropShadow() {
		return dropShadow.getValue();
	}
}
