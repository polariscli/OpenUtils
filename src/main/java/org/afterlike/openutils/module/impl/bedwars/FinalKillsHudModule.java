package org.afterlike.openutils.module.impl.bedwars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.afterlike.openutils.event.api.EventPhase;
import org.afterlike.openutils.event.handler.EventHandler;
import org.afterlike.openutils.event.impl.GameTickEvent;
import org.afterlike.openutils.event.impl.ReceiveChatEvent;
import org.afterlike.openutils.event.impl.RenderOverlayEvent;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.hud.HudModule;
import org.afterlike.openutils.module.api.hud.Position;
import org.afterlike.openutils.module.api.setting.Setting;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.afterlike.openutils.module.api.setting.impl.DescriptionSetting;
import org.afterlike.openutils.util.client.ClientUtil;
import org.afterlike.openutils.util.client.TextUtil;
import org.afterlike.openutils.util.game.GameModeUtil;
import org.afterlike.openutils.util.game.RenderUtil;

public class FinalKillsHudModule extends Module implements HudModule {
	private final Position position = new Position(5, 50);
	private final DescriptionSetting disclaimer;
	private final BooleanSetting editPosition;
	private final BooleanSetting dropShadow;
	private final BooleanSetting showVoidKills;
	private final Map<String, Integer> finalKills = new HashMap<>();
	private static final String VOID_KEY = "§8Void";
	// TODO: implement teammates only filter
	private static final Pattern NAME_CHUNK_PATTERN = Pattern
			.compile("§([0-9a-fk-or])([A-Za-z0-9_]+)");
	public FinalKillsHudModule() {
		super("Final Kills HUD", ModuleCategory.BEDWARS);
		disclaimer = this
				.registerSetting(new DescriptionSetting("Hypixel language must be ENGLISH!"));
		editPosition = this.registerSetting(new BooleanSetting("Edit position", false));
		dropShadow = this.registerSetting(new BooleanSetting("Drop shadow", true));
		showVoidKills = this.registerSetting(new BooleanSetting("Show void kills", true));
	}

	@EventHandler
	private void onChatReceived(final ReceiveChatEvent event) {
		if (!ClientUtil.notNull())
			return;
		if (GameModeUtil.getBedWarsStatus() != 3)
			return;
		final String message = event.getMessage();
		if (!message.contains("§b§lFINAL KILL!§r"))
			return;
		final String body = message.substring(0, message.indexOf("§b§lFINAL KILL!§r")).trim();
		final Matcher matcher = NAME_CHUNK_PATTERN.matcher(body);
		String lastColorCode = null;
		String lastPlayerName = null;
		while (matcher.find()) {
			lastColorCode = "§" + matcher.group(1);
			lastPlayerName = matcher.group(2);
		}
		if (lastColorCode == null || lastPlayerName == null) {
			ClientUtil.sendDebugMessage("no name chunks found in: " + body);
			return;
		}
		final String unformattedName = TextUtil.stripColorCodes(lastColorCode + lastPlayerName);
		if (unformattedName.equalsIgnoreCase("void")) {
			if (showVoidKills.getValue()) {
				finalKills.put(VOID_KEY, finalKills.getOrDefault(VOID_KEY, 0) + 1);
				ClientUtil.sendDebugMessage("counted void kill: " + finalKills.get(VOID_KEY));
			}
			return;
		}
		final String displayName = lastColorCode + lastPlayerName;
		finalKills.put(displayName, finalKills.getOrDefault(displayName, 0) + 1);
		ClientUtil.sendDebugMessage(
				"counted kill for " + displayName + ": " + finalKills.get(displayName));
	}

	@EventHandler
	private void onRender(final RenderOverlayEvent event) {
		if (!ClientUtil.notNull())
			return;
		if (mc.gameSettings.showDebugInfo)
			return;
		if (GameModeUtil.getBedWarsStatus() != 3)
			return;
		if (finalKills.isEmpty())
			return;
		int y = position.getY();
		int delta = 0;
		final List<Map.Entry<String, Integer>> sortedKills = new ArrayList<>(finalKills.entrySet());
		sortedKills.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
		for (final Map.Entry<String, Integer> entry : sortedKills) {
			if (entry.getKey().equals(VOID_KEY) && !showVoidKills.getValue())
				continue;
			final String displayName = entry.getKey().equals(VOID_KEY) ? VOID_KEY : entry.getKey();
			final String line = "§r" + displayName + ": §f" + entry.getValue();
			mc.fontRendererObj.drawString(line, position.getX(), y,
					RenderUtil.getChromaColor(2L, delta), useHudDropShadow());
			y += mc.fontRendererObj.FONT_HEIGHT + 2;
			delta -= 90;
		}
	}

	@EventHandler
	private void onTick(final GameTickEvent event) {
		if (event.getPhase() != EventPhase.POST)
			return;
		if (GameModeUtil.getBedWarsStatus() != 3) {
			resetTracking();
		}
	}

	private void resetTracking() {
		finalKills.clear();
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		resetTracking();
	}

	@Override
	protected void onDisable() {
		resetTracking();
		super.onDisable();
	}

	@Override
	public void onSettingChanged(final Setting<?> setting) {
		handleHudSettingChanged(setting);
		super.onSettingChanged(setting);
	}

	@Override
	public Position getHudPosition() {
		return position;
	}

	@Override
	public BooleanSetting getHudEditSetting() {
		return editPosition;
	}

	@Override
	public String getHudPlaceholderText() {
		return "Player:-Player:-Player:";
	}

	@Override
	public boolean useHudDropShadow() {
		return dropShadow.getValue();
	}
}
