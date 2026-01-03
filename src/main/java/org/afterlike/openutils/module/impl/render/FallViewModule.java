package org.afterlike.openutils.module.impl.render;

import java.awt.Color;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import org.afterlike.openutils.event.handler.EventHandler;
import org.afterlike.openutils.event.impl.RenderOverlayEvent;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.hud.HudModule;
import org.afterlike.openutils.module.api.hud.Position;
import org.afterlike.openutils.module.api.setting.Setting;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.afterlike.openutils.module.api.setting.impl.DescriptionSetting;
import org.afterlike.openutils.module.api.setting.impl.NumberSetting;
import org.afterlike.openutils.util.client.ClientUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FallViewModule extends Module implements HudModule {
	// this position default will be fixed when i add anchor points
	private final Position position = new Position(200, 95);
	private final DescriptionSetting description;
	private final BooleanSetting editPosition;
	private final BooleanSetting dropShadow;
	private final NumberSetting damageThreshold;
	private final BooleanSetting disableWhileFlying;
	private final BooleanSetting onlyWhileSneaking;
	private final BooleanSetting showAsHearts;
	private final BooleanSetting showHeartSymbol;
	private final BooleanSetting showDistance;
	private double fallStartY = -1;
	private double groundY = -1;
	private float cachedFallDistance = 0;
	private int cachedEnchantmentModifier = -1;
	private final ItemStack[] cachedArmorInventory = new ItemStack[4];
	private boolean armorCacheValid = false;
	public FallViewModule() {
		super("Fall View", ModuleCategory.RENDER);
		description = this.registerSetting(new DescriptionSetting("Shows fall distance damage."));
		editPosition = this.registerSetting(new BooleanSetting("Edit position", false));
		dropShadow = this.registerSetting(new BooleanSetting("Drop shadow", true));
		damageThreshold = this
				.registerSetting(new NumberSetting("Damage threshold %", 0.0, 0.0, 100.0, 5.0));
		disableWhileFlying = this.registerSetting(new BooleanSetting("Disable while flying", true));
		onlyWhileSneaking = this.registerSetting(new BooleanSetting("Only while sneaking", false));
		showAsHearts = this.registerSetting(new BooleanSetting("Show as hearts", true));
		showHeartSymbol = this.registerSetting(new BooleanSetting("Show heart symbol", true));
		showDistance = this.registerSetting(new BooleanSetting("Show distance", false));
	}

	@Override
	protected void onDisable() {
		resetState();
	}

	private void resetState() {
		fallStartY = -1;
		groundY = -1;
		cachedFallDistance = 0;
		cachedEnchantmentModifier = -1;
		for (int i = 0; i < 4; i++) {
			cachedArmorInventory[i] = null;
		}
		armorCacheValid = false;
	}

	@EventHandler
	private void onRender(@NotNull final RenderOverlayEvent event) {
		if (!ClientUtil.notNull())
			return;
		if (mc.currentScreen != null)
			return;
		if (mc.gameSettings.showDebugInfo)
			return;
		if (mc.thePlayer.capabilities.isCreativeMode)
			return;
		if (disableWhileFlying.getValue() && mc.thePlayer.capabilities.allowFlying)
			return;
		if (onlyWhileSneaking.getValue() && !mc.thePlayer.isSneaking())
			return;
		final boolean onGround = mc.thePlayer.onGround;
		if (onGround) {
			fallStartY = -1;
			groundY = -1;
			cachedFallDistance = 0;
		} else {
			if (fallStartY == -1) {
				fallStartY = mc.thePlayer.posY;
				groundY = findGroundY(mc.thePlayer.posX, mc.thePlayer.posZ);
			} else {
				final double newGroundY = findGroundY(mc.thePlayer.posX, mc.thePlayer.posZ);
				if (newGroundY != groundY) {
					groundY = newGroundY;
					cachedFallDistance = 0;
				}
			}
		}
		final float fallDistance = calculateFallDistance();
		if (fallDistance <= 2.5f)
			return;
		final PotionEffect jumpEffect = mc.thePlayer.getActivePotionEffect(Potion.jump);
		final float jumpAmplifier = (jumpEffect != null) ? (jumpEffect.getAmplifier() + 1) : 0.0f;
		final PotionEffect resistanceEffect = mc.thePlayer.getActivePotionEffect(Potion.resistance);
		final boolean hasResistance = resistanceEffect != null;
		final int resistanceLevel = hasResistance ? resistanceEffect.getAmplifier() + 1 : 0;
		final ItemStack[] armorInventory = new ItemStack[4];
		boolean armorChanged = false;
		for (int i = 0; i < 4; i++) {
			final ItemStack currentArmor = mc.thePlayer.inventory.armorItemInSlot(i);
			armorInventory[i] = currentArmor;
			if (cachedArmorInventory[i] != currentArmor) {
				armorChanged = true;
			}
		}
		int enchantmentModifier = cachedEnchantmentModifier;
		if (armorChanged || !armorCacheValid) {
			long totalModifier = 0;
			for (int i = 0; i < 100; i++) {
				int mod = EnchantmentHelper.getEnchantmentModifierDamage(armorInventory,
						DamageSource.fall);
				if (mod > 20)
					mod = 20;
				totalModifier += mod;
			}
			enchantmentModifier = (int) Math.round(totalModifier / 100.0);
			cachedEnchantmentModifier = enchantmentModifier;
			System.arraycopy(armorInventory, 0, cachedArmorInventory, 0, 4);
			armorCacheValid = true;
		}
		float damagePoints = fallDistance - 3.0f - jumpAmplifier;
		double damage = Math.max(0, MathHelper.ceiling_double_int(damagePoints));
		if (hasResistance && damage > 0) {
			final int i = resistanceLevel * 5;
			final int j = 25 - i;
			damage = j * damage / 25.0;
		}
		if (damage > 0 && enchantmentModifier > 0) {
			damage = (25 - enchantmentModifier) * damage / 25.0;
		}
		final int finalDamage = MathHelper.ceiling_double_int(damage);
		if (finalDamage <= 0)
			return;
		final double currentHealth = mc.thePlayer.getHealth();
		final double damagePercent = (double) finalDamage / currentHealth * 100.0;
		if (damagePercent <= damageThreshold.getValue())
			return;
		final int x = position.getX();
		final int y = position.getY();
		float displayDamage = finalDamage;
		if (showAsHearts.getValue()) {
			displayDamage = finalDamage / 2.0f;
			displayDamage = round(displayDamage, 1);
		}
		final double percent = (double) finalDamage / currentHealth;
		final String colorCode;
		if (finalDamage >= currentHealth) {
			colorCode = "§4";
		} else if (percent >= 0.7) {
			colorCode = "§c";
		} else if (percent >= 0.5) {
			colorCode = "§6";
		} else if (percent >= 0.3) {
			colorCode = "§e";
		} else {
			colorCode = "§a";
		}
		String damageStr = colorCode + formatNumber(displayDamage);
		if (showHeartSymbol.getValue()) {
			damageStr += "§c❤§r";
		}
		mc.fontRendererObj.drawString(damageStr, x, y, Color.WHITE.getRGB(), useHudDropShadow());
		if (showDistance.getValue()) {
			final Color distanceColor = getDistanceColor(fallDistance);
			final String distanceStr = formatNumber(round(fallDistance, 2)) + "m";
			mc.fontRendererObj.drawString(distanceStr, x, y + mc.fontRendererObj.FONT_HEIGHT + 2,
					distanceColor.getRGB(), useHudDropShadow());
		}
	}

	private float calculateFallDistance() {
		if (fallStartY == -1 || groundY == -1) {
			final double currentY = mc.thePlayer.posY;
			final double ground = findGroundY(mc.thePlayer.posX, mc.thePlayer.posZ);
			if (ground == -1) {
				return 0;
			}
			return (float) Math.max(0, currentY - ground);
		}
		if (cachedFallDistance == 0) {
			cachedFallDistance = (float) Math.max(0, fallStartY - groundY);
		}
		return cachedFallDistance;
	}

	private double findGroundY(final double x, final double z) {
		final int startY = (int) Math.floor(mc.thePlayer.posY);
		for (int y = startY; y > -1; y--) {
			final BlockPos pos = new BlockPos(Math.floor(x), y, Math.floor(z));
			final Block block = mc.theWorld.getBlockState(pos).getBlock();
			if (block.getMaterial() != Material.air && block.isCollidable()) {
				return y + 1;
			}
		}
		return -1;
	}

	private Color getDistanceColor(final float distance) {
		final float minDistance = 2.5f;
		final float maxDistance = 20.0f;
		final float normalized = MathHelper
				.clamp_float((distance - minDistance) / (maxDistance - minDistance), 0.0f, 1.0f);
		final int red = 255;
		final int green = (int) (255 * (1.0f - normalized));
		return new Color(red, green, 0);
	}

	private static float round(final float value, final int places) {
		if (places < 0)
			return value;
		final float factor = (float) Math.pow(10, places);
		return Math.round(value * factor) / factor;
	}

	private static @NotNull String formatNumber(final float val) {
		return val == (long) val ? Long.toString((long) val) : Float.toString(val);
	}

	@Override
	public void onSettingChanged(@Nullable final Setting<?> setting) {
		handleHudSettingChanged(setting);
		super.onSettingChanged(setting);
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
		return "§c5❤";
	}

	@Override
	public boolean useHudDropShadow() {
		return dropShadow.getValue();
	}
}
