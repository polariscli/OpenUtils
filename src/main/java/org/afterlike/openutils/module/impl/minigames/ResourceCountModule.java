package org.afterlike.openutils.module.impl.minigames;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.afterlike.openutils.event.api.EventPhase;
import org.afterlike.openutils.event.handler.EventHandler;
import org.afterlike.openutils.event.impl.GameTickEvent;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.afterlike.openutils.module.api.setting.impl.DescriptionSetting;
import org.afterlike.openutils.util.client.ClientUtil;
import org.afterlike.openutils.util.game.GameModeUtil;
import org.jetbrains.annotations.NotNull;

public class ResourceCountModule extends Module {
	private final DescriptionSetting description;
	private final BooleanSetting trackIron;
	private final BooleanSetting trackGold;
	private final BooleanSetting trackDiamonds;
	private final BooleanSetting trackEmeralds;
	public ResourceCountModule() {
		super("Resource Count", ModuleCategory.MINIGAMES);
		description = this
				.registerSetting(new DescriptionSetting("Tracks resources in your inventory"));
		trackIron = this.registerSetting(new BooleanSetting("Track Iron", false));
		trackGold = this.registerSetting(new BooleanSetting("Track Gold", false));
		trackDiamonds = this.registerSetting(new BooleanSetting("Track Diamonds", true));
		trackEmeralds = this.registerSetting(new BooleanSetting("Track Emeralds", true));
	}
	private final Map<Item, Integer> lastCounts = new HashMap<>();
	@EventHandler
	private void onTick(final @NotNull GameTickEvent event) {
		if (event.getPhase() != EventPhase.POST)
			return;
		if (!GameModeUtil.onHypixel())
			return;
		if (GameModeUtil.getBedWarsStatus() != 3)
			return;
		if (!ClientUtil.notNull())
			return;
		Map<Item, Integer> current = new HashMap<>();
		initCounts(current);
		for (ItemStack stack : mc.thePlayer.inventory.mainInventory) {
			if (stack == null)
				continue;
			Item item = stack.getItem();
			if (!current.containsKey(item))
				continue;
			current.put(item, current.get(item) + stack.stackSize);
		}
		handleChanges(current);
		lastCounts.clear();
		lastCounts.putAll(current);
	}

	private void handleChanges(final @NotNull Map<@NotNull Item, @NotNull Integer> current) {
		for (Map.Entry<Item, Integer> entry : current.entrySet()) {
			Item item = entry.getKey();
			int newCount = entry.getValue();
			int oldCount = lastCounts.getOrDefault(item, 0);
			if (newCount == oldCount)
				continue;
			if (!isTracking(item))
				continue;
			boolean gained = newCount > oldCount;
			String prefix = gained
					? EnumChatFormatting.GREEN + "[+] "
					: EnumChatFormatting.RED + "[-] ";
			ClientUtil.sendMessage(prefix + getItemDisplayName(item) + EnumChatFormatting.DARK_GRAY
					+ " (" + newCount + ")");
			// TODO: add sound utils
			// if (pingSound.getValue()) {
			// SoundUtil.playPing();
			// }
		}
	}

	private boolean isTracking(final @NotNull Item item) {
		if (item == Items.iron_ingot)
			return trackIron.getValue();
		if (item == Items.gold_ingot)
			return trackGold.getValue();
		if (item == Items.diamond)
			return trackDiamonds.getValue();
		if (item == Items.emerald)
			return trackEmeralds.getValue();
		return false;
	}

	private String getItemDisplayName(final @NotNull Item item) {
		if (item == Items.iron_ingot)
			return EnumChatFormatting.WHITE + "Iron";
		if (item == Items.gold_ingot)
			return EnumChatFormatting.GOLD + "Gold";
		if (item == Items.diamond)
			return EnumChatFormatting.AQUA + "Diamond";
		if (item == Items.emerald)
			return EnumChatFormatting.DARK_GREEN + "Emerald";
		return "Unknown";
	}

	private void initCounts(final @NotNull Map<@NotNull Item, @NotNull Integer> map) {
		map.put(Items.iron_ingot, 0);
		map.put(Items.gold_ingot, 0);
		map.put(Items.diamond, 0);
		map.put(Items.emerald, 0);
	}

	private void resetCounts() {
		lastCounts.clear();
		initCounts(lastCounts);
	}

	@Override
	protected void onDisable() {
		resetCounts();
	}
}
