package org.afterlike.openutils.config.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.config.api.ConfigData;
import org.afterlike.openutils.config.api.HudEntry;
import org.afterlike.openutils.config.api.ModuleEntry;
import org.afterlike.openutils.config.api.PanelEntry;
import org.afterlike.openutils.gui.ClickGuiScreen;
import org.afterlike.openutils.gui.panel.CategoryPanel;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.hud.HudModule;
import org.afterlike.openutils.module.api.hud.Position;
import org.afterlike.openutils.module.api.setting.Setting;

public class ConfigHandler {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Type CONFIG_TYPE = new TypeToken<ConfigData>() {
	}.getType();
	private static ConfigData cachedPanels;
	private boolean loading = false;
	private static Path getConfigPath() {
		final Minecraft mc = Minecraft.getMinecraft();
		final Path dir = Paths.get(mc.mcDataDir.getAbsolutePath(), "config", "openutils");
		try {
			Files.createDirectories(dir);
		} catch (final IOException ignored) {
		}
		return dir.resolve("config.json");
	}

	public void loadAndApply() {
		final Path path = getConfigPath();
		if (!Files.exists(path)) {
			return;
		}
		loading = true;
		try (final Reader reader = Files.newBufferedReader(path)) {
			final ConfigData data = GSON.fromJson(reader, CONFIG_TYPE);
			if (data == null)
				return;
			cachedPanels = data;
			applyModules(data);
		} catch (final IOException ignored) {
		} finally {
			loading = false;
		}
	}

	private void applyModules(final ConfigData data) {
		for (final Module module : OpenUtils.get().getModuleHandler().getModules()) {
			final ModuleEntry entry = data.modules.get(module.getName());
			if (entry == null)
				continue;
			if (entry.enabled && !module.isEnabled()) {
				module.setEnabled(true);
			} else if (!entry.enabled && module.isEnabled()) {
				module.setEnabled(false);
			}
			final Map<String, Object> settings = entry.settings;
			if (settings == null)
				continue;
			for (final Setting<?> setting : module.getSettings()) {
				final Object value = settings.get(setting.getName());
				if (value == null)
					continue;
				setting.deserializeValue(value);
			}
			if (module instanceof HudModule && entry.hud != null) {
				final Position position = ((HudModule) module).getHudPosition();
				position.setPosition(entry.hud.x, entry.hud.y);
			}
		}
	}

	public void applyPanels(final List<CategoryPanel> panels) {
		if (cachedPanels == null)
			return;
		final Map<String, PanelEntry> panelMap = cachedPanels.panels;
		for (final CategoryPanel panel : panels) {
			final PanelEntry entry = panelMap.get(panel.getCategory().name());
			if (entry == null)
				continue;
			panel.setX(entry.x);
			panel.setY(entry.y);
			panel.setExpanded(entry.expanded);
			panel.layoutComponents();
		}
	}

	public void saveConfiguration() {
		if (loading) {
			return;
		}
		final ConfigData data = new ConfigData();
		data.modules = new HashMap<>();
		for (final Module module : OpenUtils.get().getModuleHandler().getModules()) {
			final ModuleEntry entry = new ModuleEntry();
			entry.enabled = module.isEnabled();
			entry.settings = new HashMap<>();
			for (final Setting<?> setting : module.getSettings()) {
				final Object serialized = setting.serializeValue();
				if (serialized != null) {
					entry.settings.put(setting.getName(), serialized);
				}
			}
			if (module instanceof HudModule) {
				final Position position = ((HudModule) module).getHudPosition();
				final HudEntry hudEntry = new HudEntry();
				hudEntry.x = position.getX();
				hudEntry.y = position.getY();
				entry.hud = hudEntry;
			}
			data.modules.put(module.getName(), entry);
		}
		if (cachedPanels != null) {
			data.panels = new HashMap<>(cachedPanels.panels);
		} else {
			data.panels = new HashMap<>();
		}
		if (ClickGuiScreen.categoryPanels != null) {
			for (final CategoryPanel panel : ClickGuiScreen.categoryPanels) {
				final PanelEntry entry = new PanelEntry();
				entry.x = panel.getX();
				entry.y = panel.getY();
				entry.expanded = panel.isExpanded();
				data.panels.put(panel.getCategory().name(), entry);
			}
		}
		final Path path = getConfigPath();
		try (final Writer writer = Files.newBufferedWriter(path)) {
			GSON.toJson(data, writer);
			cachedPanels = data;
		} catch (final IOException ignored) {
		}
	}
}
