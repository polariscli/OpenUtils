package org.afterlike.openutils.module.impl.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
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
	private final List<Module> sortedAlpha = new ArrayList<>(64);
	private final List<Module> sortedWidth = new ArrayList<>(64);
	private final Map<Module, Integer> widthByModule = new IdentityHashMap<>(64);
	private boolean cacheBuilt = false;
	private static final Comparator<Module> ALPHA_COMPARATOR = Comparator
			.comparing(Module::getName);
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
		if (!ClientUtil.notNull())
			return;
		if (mc.currentScreen != null || mc.gameSettings.showDebugInfo)
			return;
		ensureCache();
		final int x = position.getX();
		int y = position.getY();
		int delta = 0;
		final boolean shadow = useHudDropShadow();
		final int lineStep = mc.fontRendererObj.FONT_HEIGHT + 2;
		final List<Module> list = alphabeticalSort.getValue() ? sortedAlpha : sortedWidth;
		for (final Module module : list) {
			if (module == this || !module.isEnabled())
				continue;
			mc.fontRendererObj.drawString(module.getName(), x, y,
					RenderUtil.getChromaColor(2L, delta), shadow);
			y += lineStep;
			delta -= 120;
		}
	}

	private void ensureCache() {
		if (cacheBuilt)
			return;
		final List<Module> all = OpenUtils.get().getModuleHandler().getModules();
		sortedAlpha.clear();
		sortedAlpha.addAll(all);
		sortedAlpha.sort(ALPHA_COMPARATOR);
		widthByModule.clear();
		for (final Module m : all) {
			widthByModule.put(m, mc.fontRendererObj.getStringWidth(m.getName()));
		}
		sortedWidth.clear();
		sortedWidth.addAll(all);
		sortedWidth.sort((a, b) -> Integer.compare(widthByModule.get(b), widthByModule.get(a)));
		cacheBuilt = true;
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
