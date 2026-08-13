package org.afterlike.openutils.feature.impl.render;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.afterlike.openutils.event.api.EventPhase;
import org.afterlike.openutils.event.impl.GameTickEvent;
import org.afterlike.openutils.event.impl.RenderOverlayEvent;
import org.afterlike.openutils.event.impl.WorldLoadEvent;
import org.afterlike.openutils.feature.api.FeatureCategory;
import org.afterlike.openutils.feature.api.ToggleableFeature;
import org.afterlike.openutils.feature.api.hud.Anchor;
import org.afterlike.openutils.feature.api.hud.HudFeature;
import org.afterlike.openutils.feature.api.hud.Position;
import org.afterlike.openutils.util.client.ClientUtil;
import org.lwjgl.opengl.GL11;
import re.tsuku.confikure.annotations.Mode;
import re.tsuku.confikure.annotations.Option;
import re.tsuku.confikure.annotations.Range;
import re.tsuku.fastbus.Subscribe;

public class FallViewFeature extends ToggleableFeature implements HudFeature {
	private static final float MIN_FALL_DISTANCE = 2.5F;
	private static final float MAX_DISTANCE_COLOR = 20.0F;
	private static final int DAMAGE_OFFSET_Y = 0;
	private static final int DISTANCE_OFFSET_Y = 21;
	private static final int HUD_WIDTH = 40;
	private static final int HUD_HEIGHT = 30;
	private static final int ENCHANTMENT_SAMPLES = 100;
	private static final int NO_CACHED_DAMAGE = Integer.MIN_VALUE;
	private static final double LOOK_TRACE_DISTANCE = 256.0D;
	private static final double LOOK_TRACE_SEGMENT_LENGTH = 96.0D;
	private final Position position = new Position(-HUD_WIDTH / 2, -HUD_HEIGHT / 2, Anchor.CENTER);
	@Option(name = "Enable Fall View",
			description = "Show predicted fall damage and landing distance in a movable HUD.",
			order = 0)
	public boolean enabled;
	@Option(name = "Drop shadow", description = "Draw Fall View text with Minecraft's text shadow.",
			order = 1)
	public boolean dropShadow = true;
	@Option(name = "Fade duration",
			description = "Fade duration in milliseconds. Set to 0 for instant.", order = 2)
	@Range(min = 0.0D, max = 250.0D, step = 10.0D)
	public int fadeDuration = 250;
	@Option(name = "Show damage", description = "Show the damage expected when you land.",
			order = 3)
	public boolean showDamage = true;
	@Option(name = "Show distance", description = "Show the predicted distance to the ground.",
			order = 4)
	public boolean showDistance;
	@Option(name = "Units",
			description = "Display predicted damage as hearts or raw health points.", order = 5)
	@Mode(values = {"Hearts", "Health Points"})
	public String units = "Hearts";
	@Option(name = "Show heart symbol",
			description = "Append a heart symbol to the predicted damage value.", order = 6)
	public boolean showHeartSymbol;
	@Option(name = "Damage threshold",
			description = "Minimum damage percentage of your current health.", order = 7)
	@Range(min = 0.0D, max = 100.0D, step = 5.0D)
	public double damageThreshold;
	@Option(name = "Only while sneaking",
			description = "Only show Fall View while you are holding sneak.", order = 8)
	public boolean onlyWhileSneaking;
	@Option(name = "Hide with flight",
			description = "Hide Fall View whenever the player has permission to fly.", order = 9)
	public boolean hideWithFlight = true;
	private double fallStartY = -1.0D;
	private double groundY = -1.0D;
	private float cachedFallDistance;
	private boolean armorCacheValid;
	private int cachedArmorSignature;
	private int cachedEnchantmentModifier;
	private int cachedDamageFallDistance = NO_CACHED_DAMAGE;
	private int cachedDamageJumpBoost;
	private int cachedDamageResistance;
	private int cachedDamageEnchantment;
	private int cachedDamage;
	private String damageText;
	private String distanceText;
	private int distanceTextColor = 0xFFFFFFFF;
	private boolean overlayVisible;
	private float fadeStartOpacity;
	private long fadeStartNanos;
	public FallViewFeature() {
		super("Fall View", FeatureCategory.RENDER);
	}

	@Subscribe
	private void onTick(final GameTickEvent event) {
		if (event.getPhase() != EventPhase.POST) {
			return;
		}
		if (mc.currentScreen != null || !ClientUtil.notNull()
				|| mc.thePlayer.capabilities.isCreativeMode) {
			setOverlayVisible(false);
			return;
		}
		if (hideWithFlight && mc.thePlayer.capabilities.allowFlying) {
			setOverlayVisible(false);
			return;
		}
		if (onlyWhileSneaking && !mc.thePlayer.isSneaking()) {
			setOverlayVisible(false);
			return;
		}
		if (!showDamage && !showDistance) {
			setOverlayVisible(false);
			return;
		}
		final float fallDistance = getPredictedFallDistance();
		if (fallDistance <= MIN_FALL_DISTANCE) {
			setOverlayVisible(false);
			return;
		}
		String nextDamageText = null;
		if (showDamage) {
			final PotionEffect jumpEffect = mc.thePlayer.getActivePotionEffect(Potion.jump);
			final int jumpBoostLevel = jumpEffect != null ? jumpEffect.getAmplifier() + 1 : 0;
			final PotionEffect resistanceEffect = mc.thePlayer
					.getActivePotionEffect(Potion.resistance);
			final int resistanceLevel = resistanceEffect != null
					? resistanceEffect.getAmplifier() + 1
					: 0;
			final int enchantmentModifier = getEnchantmentModifier();
			final int finalDamage = calculateDamage(fallDistance, jumpBoostLevel, resistanceLevel,
					enchantmentModifier);
			final float currentHealth = mc.thePlayer.getHealth();
			final double damagePercent = currentHealth > 0.0F
					? finalDamage / (double) currentHealth * 100.0D
					: 0.0D;
			if (finalDamage > 0 && damagePercent > damageThreshold) {
				nextDamageText = formatDamage(finalDamage, currentHealth);
			}
		}
		String nextDistanceText = null;
		int nextDistanceTextColor = 0xFFFFFFFF;
		if (showDistance) {
			nextDistanceText = formatNumber(round(fallDistance, 2)) + "m";
			nextDistanceTextColor = getDistanceColor(fallDistance);
		}
		if (nextDamageText == null && nextDistanceText == null) {
			setOverlayVisible(false);
			return;
		}
		damageText = nextDamageText;
		distanceText = nextDistanceText;
		distanceTextColor = nextDistanceTextColor;
		setOverlayVisible(true);
	}

	@Subscribe
	private void onRender(final RenderOverlayEvent event) {
		if (mc.currentScreen != null || !ClientUtil.notNull()) {
			return;
		}
		final float opacity = getOverlayOpacity(System.nanoTime());
		if (opacity <= 0.0F) {
			if (!overlayVisible) {
				clearOverlay();
			}
			return;
		}
		if (damageText == null && distanceText == null) {
			return;
		}
		final int x = position.getX(HUD_WIDTH);
		final int y = position.getY(HUD_HEIGHT);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		if (damageText != null) {
			drawCentered(damageText, x, y + DAMAGE_OFFSET_Y, withOpacity(0xFFFFFFFF, opacity));
		}
		if (distanceText != null) {
			drawCentered(distanceText, x, y + DISTANCE_OFFSET_Y,
					withOpacity(distanceTextColor, opacity));
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Subscribe
	private void onWorldLoad(final WorldLoadEvent event) {
		resetState();
	}

	@Override
	protected void onDisable() {
		resetState();
	}

	private float getPredictedFallDistance() {
		updateLandingPosition();
		return mc.thePlayer.onGround ? calculateLookDownFallDistance() : calculateFallDistance();
	}

	private void updateLandingPosition() {
		if (mc.thePlayer.onGround) {
			fallStartY = -1.0D;
			groundY = -1.0D;
			cachedFallDistance = 0.0F;
			return;
		}
		if (fallStartY == -1.0D) {
			fallStartY = mc.thePlayer.posY;
			groundY = findGroundY(mc.thePlayer.posX, mc.thePlayer.posZ);
			return;
		}
		final double newGroundY = findGroundY(mc.thePlayer.posX, mc.thePlayer.posZ);
		if (newGroundY != groundY) {
			groundY = newGroundY;
			cachedFallDistance = 0.0F;
		}
	}

	private float calculateFallDistance() {
		if (fallStartY == -1.0D || groundY == -1.0D) {
			return 0.0F;
		}
		if (cachedFallDistance == 0.0F) {
			cachedFallDistance = (float) Math.max(0.0D, fallStartY - groundY);
		}
		return cachedFallDistance;
	}

	private float calculateLookDownFallDistance() {
		if (mc.thePlayer.rotationPitch <= 0.0F) {
			return 0.0F;
		}
		final MovingObjectPosition target = rayTraceLookDirection();
		if (target == null || target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK
				|| target.getBlockPos() == null) {
			return 0.0F;
		}
		final BlockPos targetPosition = target.getBlockPos();
		final double landingY = findGroundY(targetPosition.getX() + 0.5D,
				targetPosition.getZ() + 0.5D);
		return landingY == -1.0D ? 0.0F : (float) Math.max(0.0D, mc.thePlayer.posY - landingY);
	}

	private MovingObjectPosition rayTraceLookDirection() {
		final Vec3 direction = mc.thePlayer.getLook(1.0F);
		Vec3 segmentStart = mc.thePlayer.getPositionEyes(1.0F);
		double remainingDistance = LOOK_TRACE_DISTANCE;
		while (remainingDistance > 0.0D) {
			final double segmentLength = Math.min(remainingDistance, LOOK_TRACE_SEGMENT_LENGTH);
			final Vec3 segmentEnd = segmentStart.addVector(direction.xCoord * segmentLength,
					direction.yCoord * segmentLength, direction.zCoord * segmentLength);
			final MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(segmentStart, segmentEnd,
					false, true, false);
			if (hit != null) {
				return hit;
			}
			segmentStart = segmentEnd;
			remainingDistance -= segmentLength;
		}
		return null;
	}

	private double findGroundY(final double x, final double z) {
		final int blockX = MathHelper.floor_double(x);
		final int blockZ = MathHelper.floor_double(z);
		final BlockPos.MutableBlockPos blockPosition = new BlockPos.MutableBlockPos();
		for (int y = MathHelper.floor_double(mc.thePlayer.posY); y >= 0; y--) {
			blockPosition.set(blockX, y, blockZ);
			final IBlockState state = mc.theWorld.getBlockState(blockPosition);
			final Block block = state.getBlock();
			final Material material = block.getMaterial();
			if (material == Material.water) {
				return -1.0D;
			}
			if (!block.isReplaceable(mc.theWorld, blockPosition) && material != Material.lava) {
				return y + 1.0D;
			}
		}
		return -1.0D;
	}

	private int getEnchantmentModifier() {
		final ItemStack[] armor = mc.thePlayer.inventory.armorInventory;
		int armorSignature = 1;
		int totalProtectionLevel = 0;
		for (final ItemStack stack : armor) {
			final int protection = stack == null
					? 0
					: EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
			final int featherFalling = stack == null
					? 0
					: EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId,
							stack);
			armorSignature = 31 * armorSignature + protection;
			armorSignature = 31 * armorSignature + featherFalling;
			totalProtectionLevel += protection + featherFalling;
		}
		if (armorCacheValid && armorSignature == cachedArmorSignature) {
			return cachedEnchantmentModifier;
		}
		if (totalProtectionLevel == 0) {
			cachedEnchantmentModifier = 0;
		} else {
			long totalModifier = 0L;
			for (int sample = 0; sample < ENCHANTMENT_SAMPLES; sample++) {
				totalModifier += Math.min(20,
						EnchantmentHelper.getEnchantmentModifierDamage(armor, DamageSource.fall));
			}
			cachedEnchantmentModifier = (int) Math
					.round(totalModifier / (double) ENCHANTMENT_SAMPLES);
		}
		cachedArmorSignature = armorSignature;
		armorCacheValid = true;
		cachedDamageFallDistance = NO_CACHED_DAMAGE;
		return cachedEnchantmentModifier;
	}

	private int calculateDamage(final float fallDistance, final int jumpBoostLevel,
			final int resistanceLevel, final int enchantmentModifier) {
		final int roundedFallDistance = Math.round(fallDistance * 100.0F);
		if (roundedFallDistance == cachedDamageFallDistance
				&& jumpBoostLevel == cachedDamageJumpBoost
				&& resistanceLevel == cachedDamageResistance
				&& enchantmentModifier == cachedDamageEnchantment) {
			return cachedDamage;
		}
		final float damagePoints = fallDistance - 3.0F - jumpBoostLevel;
		double damage = Math.max(0, MathHelper.ceiling_double_int(damagePoints));
		if (resistanceLevel > 0 && damage > 0.0D) {
			damage = (25 - resistanceLevel * 5) * damage / 25.0D;
		}
		if (enchantmentModifier > 0 && damage > 0.0D) {
			damage = (25 - enchantmentModifier) * damage / 25.0D;
		}
		cachedDamageFallDistance = roundedFallDistance;
		cachedDamageJumpBoost = jumpBoostLevel;
		cachedDamageResistance = resistanceLevel;
		cachedDamageEnchantment = enchantmentModifier;
		cachedDamage = MathHelper.ceiling_double_int(damage);
		return cachedDamage;
	}

	private String formatDamage(final int finalDamage, final float currentHealth) {
		final double damage = "Hearts".equals(units) ? round(finalDamage / 2.0D, 1) : finalDamage;
		final double percentage = finalDamage / (double) currentHealth;
		final String color = finalDamage >= currentHealth
				? "§4"
				: percentage >= 0.7D
						? "§c"
						: percentage >= 0.5D ? "§6" : percentage >= 0.3D ? "§e" : "§a";
		return color + formatNumber(damage) + (showHeartSymbol ? "§c❤§r" : "");
	}

	private static int getDistanceColor(final float distance) {
		final float normalized = MathHelper.clamp_float(
				(distance - MIN_FALL_DISTANCE) / (MAX_DISTANCE_COLOR - MIN_FALL_DISTANCE), 0.0F,
				1.0F);
		final int green = (int) (255.0F * (1.0F - normalized));
		return 0xFFFF0000 | (green << 8);
	}

	private void drawCentered(final String text, final int x, final int y, final int color) {
		mc.fontRendererObj.drawString(text,
				x + HUD_WIDTH / 2 - mc.fontRendererObj.getStringWidth(text) / 2, y, color,
				useHudDropShadow());
	}

	private void setOverlayVisible(final boolean visible) {
		if (overlayVisible == visible) {
			return;
		}
		final long now = System.nanoTime();
		fadeStartOpacity = getOverlayOpacity(now);
		fadeStartNanos = now;
		overlayVisible = visible;
	}

	private float getOverlayOpacity(final long now) {
		if (fadeDuration <= 0) {
			return overlayVisible ? 1.0F : 0.0F;
		}
		if (fadeStartNanos == 0L) {
			return overlayVisible ? 1.0F : 0.0F;
		}
		final long fadeDurationNanos = fadeDuration * 1_000_000L;
		final float progress = MathHelper
				.clamp_float((now - fadeStartNanos) / (float) fadeDurationNanos, 0.0F, 1.0F);
		final float targetOpacity = overlayVisible ? 1.0F : 0.0F;
		if (progress >= 1.0F) {
			return targetOpacity;
		}
		return fadeStartOpacity + (targetOpacity - fadeStartOpacity) * progress;
	}

	private static int withOpacity(final int color, final float opacity) {
		int alpha = MathHelper.clamp_int(Math.round(opacity * 255.0F), 0, 255);
		if (opacity > 0.0F) {
			alpha = Math.max(4, alpha);
		}
		return (color & 0x00FFFFFF) | (alpha << 24);
	}

	private void clearOverlay() {
		damageText = null;
		distanceText = null;
		distanceTextColor = 0xFFFFFFFF;
	}

	private void resetState() {
		fallStartY = -1.0D;
		groundY = -1.0D;
		cachedFallDistance = 0.0F;
		armorCacheValid = false;
		cachedArmorSignature = 0;
		cachedEnchantmentModifier = 0;
		cachedDamageFallDistance = NO_CACHED_DAMAGE;
		overlayVisible = false;
		fadeStartOpacity = 0.0F;
		fadeStartNanos = 0L;
		clearOverlay();
	}

	private static double round(final double value, final int decimalPlaces) {
		final double scale = Math.pow(10.0D, decimalPlaces);
		return Math.round(value * scale) / scale;
	}

	private static String formatNumber(final double value) {
		return value == (long) value ? Long.toString((long) value) : Double.toString(value);
	}

	@Override
	public Position getHudPosition() {
		return position;
	}

	@Override
	public String[] getHudPreviewLines() {
		if (showDamage && showDistance) {
			return new String[]{previewDamageText(), "10m"};
		}
		if (showDamage) {
			return new String[]{previewDamageText()};
		}
		if (showDistance) {
			return new String[]{"10m"};
		}
		return new String[]{"Fall View"};
	}

	@Override
	public int getHudPreviewWidth() {
		return HUD_WIDTH;
	}

	@Override
	public int getHudPreviewHeight() {
		return HUD_HEIGHT;
	}

	@Override
	public void renderHudPreview(final int x, final int y) {
		if (!showDamage && !showDistance) {
			drawCentered("§7Fall View", x, y + 10, 0xFFFFFFFF);
			return;
		}
		if (showDamage) {
			drawCentered(previewDamageText(), x, y + DAMAGE_OFFSET_Y, 0xFFFFFFFF);
		}
		if (showDistance) {
			drawCentered("10m", x, y + DISTANCE_OFFSET_Y, getDistanceColor(10.0F));
		}
	}

	private String previewDamageText() {
		return "§6" + ("Hearts".equals(units) ? "5" : "10") + (showHeartSymbol ? "§c❤§r" : "");
	}

	@Override
	public boolean useHudDropShadow() {
		return dropShadow;
	}
}
