package org.afterlike.openutils.module.handler;

import java.util.*;
import net.minecraft.client.Minecraft;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.event.handler.EventHandler;
import org.afterlike.openutils.event.impl.KeyPressEvent;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.impl.bedwars.*;
import org.afterlike.openutils.module.impl.client.*;
import org.afterlike.openutils.module.impl.hypixel.*;
import org.afterlike.openutils.module.impl.movement.*;
import org.afterlike.openutils.module.impl.player.*;
import org.afterlike.openutils.module.impl.render.*;
import org.afterlike.openutils.module.impl.world.*;
import org.afterlike.openutils.util.client.ClientUtil;

public class ModuleHandler {
	private final Minecraft mc = Minecraft.getMinecraft();
	private final List<Module> moduleList = Collections.synchronizedList(new ArrayList<>());
	private final List<Module> enabledModules = Collections.synchronizedList(new ArrayList<>());
	public void initialize() {
		OpenUtils.get().getEventBus().subscribe(this);
		// movement
		this.register(new NoJumpDelayModule());
		this.register(new NullMoveModule());
		this.register(new SprintModule());
		// player
		this.register(new ActionSoundsModule());
		this.register(new GhostLiquidFixModule());
		this.register(new NoBreakDelayModule());
		this.register(new NoHitDelayModule());
		// render
		this.register(new AnimationsModule());
		this.register(new AntiDebuffModule());
		this.register(new AntiShuffleModule());
		this.register(new ArrayListModule());
		this.register(new CameraModule());
		this.register(new CapeModule());
		this.register(new DamageTagsModule());
		this.register(new FallViewModule());
		this.register(new FreeLookModule());
		this.register(new NameHiderModule());
		this.register(new TargetHudModule());
		this.register(new ThickRodsModule());
		// world
		this.register(new TimeChangerModule());
		this.register(new WeatherModule()); // TODO: impl
		// hypixel
		this.register(new AutoGGModule());
		this.register(new DenickerModule());
		this.register(new NickBotModule()); // TODO: impl
		this.register(new QuickMathModule());
		// bed wars
		this.register(new ArmorAlertsModule());
		this.register(new FinalKillsHudModule());
		this.register(new ItemAlertsModule());
		this.register(new QuickShopModule());
		this.register(new ResourceCountModule());
		this.register(new TimersHudModule());
		this.register(new UpgradeAlertsModule());
		this.register(new UpgradesHudModule());
		// client
		this.register(new DebugModule());
		this.register(new GuiModule());
		this.register(new VPNStatusModule());
	}

	private void register(final Module module) {
		moduleList.add(module);
	}

	public void updateEnabledModules() {
		synchronized (moduleList) {
			enabledModules.clear();
			for (final Module module : moduleList) {
				if (module.isEnabled()) {
					enabledModules.add(module);
				}
			}
		}
	}

	public List<Module> getModules() {
		return moduleList;
	}

	public boolean isEnabled(final Class<? extends Module> moduleClass) {
		synchronized (enabledModules) {
			for (final Module module : enabledModules) {
				if (moduleClass.isInstance(module)) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T extends Module> T getModuleClass(final Class<T> moduleClass) {
		synchronized (moduleList) {
			for (Module module : moduleList) {
				if (moduleClass.isInstance(module)) {
					return (T) module;
				}
			}
		}
		throw new IllegalStateException("Module not registered: " + moduleClass.getName());
	}

	public List<Module> getModulesInCategory(final ModuleCategory category) {
		final List<Module> modulesInCategory = new ArrayList<>();
		synchronized (moduleList) {
			for (final Module module : moduleList) {
				if (module.getCategory().equals(category)) {
					modulesInCategory.add(module);
				}
			}
		}
		return modulesInCategory;
	}

	public List<Module> getEnabledModules() {
		return enabledModules;
	}

	@EventHandler
	private void onKeyPress(final KeyPressEvent event) {
		if (mc.currentScreen != null || !ClientUtil.notNull()) {
			return;
		}
		final int keyCode = event.getKeyCode();
		if (keyCode == 0) {
			return;
		}
		final boolean pressed = event.isPressed();
		for (final Module module : getModules()) {
			if (module.getKeybind() == keyCode) {
				// Free Look requires keybind to be held
				if (module instanceof FreeLookModule) {
					final FreeLookModule freeLook = (FreeLookModule) module;
					if (pressed) {
						if (!freeLook.isEnabled()) {
							freeLook.setEnabled(true);
						}
					} else {
						if (freeLook.isEnabled()) {
							freeLook.setEnabled(false);
						}
					}
				} else {
					if (pressed) {
						module.toggle();
					}
				}
			}
		}
	}
}
