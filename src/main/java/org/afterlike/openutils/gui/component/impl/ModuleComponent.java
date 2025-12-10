package org.afterlike.openutils.gui.component.impl;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.gui.component.Component;
import org.afterlike.openutils.gui.panel.CategoryPanel;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.setting.Setting;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.afterlike.openutils.module.api.setting.impl.DescriptionSetting;
import org.afterlike.openutils.module.api.setting.impl.ModeSetting;
import org.afterlike.openutils.module.api.setting.impl.NumberSetting;
import org.afterlike.openutils.module.impl.client.GUIModule;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class ModuleComponent extends Component {
	private final int enabledTextColor = new Color(0, 85, 255).getRGB();
	private final int enabledFillColor = new Color(154, 2, 255).getRGB();
	public final @NotNull Module module;
	public final @NotNull CategoryPanel panel;
	public int yOffset;
	private final @NotNull List<@NotNull Component> settingComponents;
	public boolean expandedSettings;
	public ModuleComponent(@NotNull final Module module, @NotNull final CategoryPanel panel,
			final int yOffset) {
		this.module = module;
		this.panel = panel;
		this.yOffset = yOffset;
		this.settingComponents = new ArrayList<>();
		this.expandedSettings = false;
		int y = yOffset + 12;
		if (!module.getSettings().isEmpty()) {
			for (@NotNull final Setting<?> setting : module.getSettings()) {
				if (setting instanceof NumberSetting) {
					final NumberSetting numberSetting = (NumberSetting) setting;
					final SliderComponent slider = new SliderComponent(numberSetting, this, y);
					this.settingComponents.add(slider);
					y += 12;
				} else if (setting instanceof ModeSetting) {
					final ModeSetting modeSetting = (ModeSetting) setting;
					final SliderComponent modeSlider = new SliderComponent(modeSetting, this, y);
					this.settingComponents.add(modeSlider);
					y += 12;
				} else if (setting instanceof BooleanSetting) {
					final BooleanSetting boolSetting = (BooleanSetting) setting;
					final BooleanComponent toggle = new BooleanComponent(module, boolSetting, this,
							y);
					this.settingComponents.add(toggle);
					y += 12;
				} else if (setting instanceof DescriptionSetting) {
					final DescriptionSetting descSetting = (DescriptionSetting) setting;
					final DescriptionComponent description = new DescriptionComponent(descSetting,
							this, y);
					this.settingComponents.add(description);
					y += 12;
				}
			}
		}
		this.settingComponents.add(new KeybindComponent(this, y));
	}

	@Override
	public void setOffset(final int yOffset) {
		this.yOffset = yOffset;
		int y = this.yOffset + 16;
		for (@NotNull final Component comp : this.settingComponents) {
			comp.setOffset(y);
			y += comp.getHeight();
		}
	}

	public static void enableGlStates() {
		GL11.glDisable(2929);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glDepthMask(true);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
	}

	public static void disableGlStates() {
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glEnable(2929);
		GL11.glDisable(2848);
		GL11.glHint(3154, 4352);
		GL11.glHint(3155, 4352);
		GL11.glEdgeFlag(true);
	}

	public static void setColorFromTheme(final int h) {
		float a = 0.0F;
		float r = 0.0F;
		float g = 0.0F;
		float b = 0.0F;
		final String theme = getThemeSetting().getValue();
		if ("Raven B1".equalsIgnoreCase(theme)) {
			a = (h >> 14 & 0xFF) / 255.0F;
			r = (h >> 5 & 0xFF) / 255.0F;
			g = (h >> 5 & 0xFF) / 2155.0F;
			b = h & 0xFF;
		} else if ("Raven B2".equalsIgnoreCase(theme)) {
			a = (h >> 14 & 0xFF) / 255.0F;
			r = (h >> 5 & 0xFF) / 2155.0F;
			g = (h >> 5 & 0xFF) / 255.0F;
			b = h & 0xFF;
		}
		GL11.glColor4f(r, g, b, a);
	}

	public static void drawGradientQuad(final float x, final float y, final float x1,
			final float y1, final int t, final int b) {
		enableGlStates();
		GL11.glShadeModel(7425);
		GL11.glBegin(7);
		setColorFromTheme(t);
		GL11.glVertex2f(x, y1);
		GL11.glVertex2f(x1, y1);
		setColorFromTheme(b);
		GL11.glVertex2f(x1, y);
		GL11.glVertex2f(x, y);
		GL11.glEnd();
		GL11.glShadeModel(7424);
		disableGlStates();
	}

	@Override
	public void render() {
		drawGradientQuad(this.panel.getX(), this.panel.getY() + this.yOffset,
				this.panel.getX() + this.panel.getWidth(), this.panel.getY() + 15 + this.yOffset,
				this.module.isEnabled() ? this.enabledFillColor : -12829381,
				this.module.isEnabled() ? this.enabledFillColor : -12302777);
		GL11.glPushMatrix();
		final boolean themeHighlight = getThemeSetting().getValue().equalsIgnoreCase("Raven B3");
		final int button_rgb = themeHighlight
				? (this.module.isEnabled() ? this.enabledTextColor : Color.lightGray.getRGB())
				: Color.lightGray.getRGB();
		mc.fontRendererObj.drawStringWithShadow(this.module.getName(),
				this.panel.getX() + (float) this.panel.getWidth() / 2
						- (float) mc.fontRendererObj.getStringWidth(this.module.getName()) / 2,
				this.panel.getY() + this.yOffset + 4, button_rgb);
		GL11.glPopMatrix();
		if (this.expandedSettings && !this.settingComponents.isEmpty()) {
			for (@NotNull final Component component : this.settingComponents) {
				component.render();
			}
		}
	}

	@Override
	public int getHeight() {
		if (!this.expandedSettings) {
			return 16;
		} else {
			int h = 16;
			for (@NotNull final Component component : this.settingComponents) {
				h += component.getHeight();
			}
			return h;
		}
	}

	@Override
	public void update(final int x, final int y) {
		if (!this.settingComponents.isEmpty()) {
			for (@NotNull final Component c : this.settingComponents) {
				c.update(x, y);
			}
		}
	}

	@Override
	public void onClick(final int x, final int y, final int button) {
		if (this.isMouseOverButton(x, y) && button == 0) {
			this.module.toggle();
		}
		if (this.isMouseOverButton(x, y) && button == 1) {
			this.expandedSettings = !this.expandedSettings;
			this.panel.layoutComponents();
		}
		for (@NotNull final Component c : this.settingComponents) {
			c.onClick(x, y, button);
		}
	}

	@Override
	public void onMouseRelease(final int x, final int y, final int m) {
		for (@NotNull final Component c : this.settingComponents) {
			c.onMouseRelease(x, y, m);
		}
	}

	@Override
	public void onKeyTyped(final char key, final int k) {
		for (@NotNull final Component c : this.settingComponents) {
			c.onKeyTyped(key, k);
		}
	}

	public boolean isMouseOverButton(final int x, final int y) {
		return x > this.panel.getX() && x < this.panel.getX() + this.panel.getWidth()
				&& y > this.panel.getY() + this.yOffset
				&& y < this.panel.getY() + 16 + this.yOffset;
	}

	private static ModeSetting getThemeSetting() {
		return OpenUtils.get().getModuleHandler().getModuleClass(GUIModule.class)
				.getSetting("theme");
	}
}
