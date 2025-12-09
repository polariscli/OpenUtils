package org.afterlike.openutils.module.handler;

import java.util.*;
import net.minecraft.client.Minecraft;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.event.handler.EventHandler;
import org.afterlike.openutils.event.impl.KeyPressEvent;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.afterlike.openutils.module.impl.client.GuiModule;
import org.afterlike.openutils.module.impl.minigames.ResourceTrackerModule;
import org.afterlike.openutils.module.impl.movement.NullMoveModule;
import org.afterlike.openutils.module.impl.movement.SprintModule;
import org.afterlike.openutils.module.impl.other.NameHiderModule;
import org.afterlike.openutils.module.impl.player.NoBreakDelayModule;
import org.afterlike.openutils.module.impl.render.AntiShuffleModule;
import org.afterlike.openutils.module.impl.render.HUDModule;
import org.afterlike.openutils.module.impl.world.AntiBotModule;
import org.afterlike.openutils.util.client.ClientUtil;
import org.jetbrains.annotations.NotNull;

public class ModuleHandler {
	private final @NotNull Minecraft mc = Minecraft.getMinecraft();
	private final @NotNull List<@NotNull Module> moduleList = new ArrayList<>();
	public void initialize() {
		OpenUtils.get().getEventBus().subscribe(this);
		// movement
		this.register(new NullMoveModule());
		this.register(new SprintModule());
		// player
		this.register(new NoBreakDelayModule());
		// render
		this.register(new AntiShuffleModule());
		this.register(new HUDModule());
		// world
		this.register(new AntiBotModule());
		// minigames
		this.register(new ResourceTrackerModule());
		// other
		this.register(new NameHiderModule());
		// client
		this.register(new GuiModule());
	}

	private void register(@NotNull final Module module) {
		moduleList.add(module);
	}

	public @NotNull List<@NotNull Module> getModules() {
		return Collections.unmodifiableList(moduleList);
	}

	public boolean isEnabled(@NotNull final Class<? extends Module> moduleClass) {
		for (@NotNull final Module module : getEnabledModules()) {
			if (moduleClass.isInstance(module)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T extends Module> @NotNull T getModuleClass(@NotNull final Class<T> moduleClass) {
		for (Module module : moduleList) {
			if (moduleClass.isInstance(module)) {
				return (T) module;
			}
		}
		throw new IllegalStateException("Module not registered: " + moduleClass.getName());
	}

	public @NotNull List<@NotNull Module> getModulesInCategory(
			@NotNull final ModuleCategory category) {
		final List<@NotNull Module> modulesInCategory = new ArrayList<>();
		for (@NotNull final Module module : this.getModules()) {
			if (module.getCategory().equals(category)) {
				modulesInCategory.add(module);
			}
		}
		return modulesInCategory;
	}

	public @NotNull List<@NotNull Module> getEnabledModules() {
		final List<@NotNull Module> enabled = new ArrayList<>();
		for (@NotNull final Module module : moduleList) {
			if (module.isEnabled()) {
				enabled.add(module);
			}
		}
		return enabled;
	}

	public @NotNull List<@NotNull Module> getEnabledModulesSorted() {
		final List<@NotNull Module> enabled = getEnabledModules();
		final HUDModule hud = getModuleClass(HUDModule.class);
		final BooleanSetting alphabeticalSetting = hud.getSetting("alphabeticalSort");
		final boolean alphabeticalSort = alphabeticalSetting != null
				&& Boolean.TRUE.equals(alphabeticalSetting.getValue());
		if (alphabeticalSort) {
			enabled.sort(Comparator.comparing(Module::getName));
		} else {
			enabled.sort((m1, m2) -> mc.fontRendererObj.getStringWidth(m2.getName())
					- mc.fontRendererObj.getStringWidth(m1.getName()));
		}
		return enabled;
	}

	@EventHandler
	private void onKeyPress(@NotNull final KeyPressEvent event) {
		if (!event.isPressed()) {
			return;
		}
		if (mc.currentScreen != null || !ClientUtil.notNull()) {
			return;
		}
		final int keyCode = event.getKeyCode();
		if (keyCode == 0) {
			return;
		}
		for (@NotNull final Module module : getModules()) {
			if (module.getKeybind() == keyCode) {
				module.toggle();
			}
		}
	}
}
