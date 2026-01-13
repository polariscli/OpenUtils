package org.afterlike.openutils.gui.component.impl;

import java.awt.*;
import org.afterlike.openutils.gui.component.Component;
import org.afterlike.openutils.module.api.setting.impl.KeybindSetting;
import org.afterlike.openutils.module.impl.client.GuiModule;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class KeybindComponent extends Component {
	private boolean isBinding;
	private final ModuleComponent parent;
	private int yOffset;
	private int x;
	private int y;
	public KeybindComponent(final ModuleComponent parent, final int yOffset) {
		this.parent = parent;
		this.x = parent.panel.getX() + parent.panel.getWidth();
		this.y = parent.panel.getY() + parent.yOffset;
		this.yOffset = yOffset;
	}

	@Override
	public void setOffset(final int yOffset) {
		this.yOffset = yOffset;
	}

	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		this.drawLabel(this.isBinding
				? "Press a key..."
				: "Bind: " + Keyboard.getKeyName(this.parent.module.getKeybind()));
		GL11.glPopMatrix();
	}

	@Override
	public void update(final int x, final int y) {
		this.isMouseOver(x, y);
		this.y = this.parent.panel.getY() + this.yOffset;
		this.x = this.parent.panel.getX();
	}

	@Override
	public void onClick(final int x, final int y, final int button) {
		if (this.isMouseOver(x, y) && button == 0 && this.parent.expandedSettings) {
			this.isBinding = !this.isBinding;
		}
	}

	@Override
	public void onKeyTyped(final char key, final int keyCode) {
		if (this.isBinding) {
			final KeybindSetting keySetting = this.parent.module.getKeybindSetting();
			if (keyCode == 0 || keyCode == Keyboard.KEY_ESCAPE) {
				if (this.parent.module instanceof GuiModule) {
					keySetting.setValue(Keyboard.KEY_RSHIFT);
				} else {
					keySetting.setValue(Keyboard.KEY_NONE);
				}
			} else {
				keySetting.setValue(keyCode);
			}
			this.parent.module.onSettingChanged(keySetting);
			this.isBinding = false;
		}
	}

	public boolean isBinding() {
		return this.isBinding;
	}

	public boolean isMouseOver(final int x, final int y) {
		return x > this.x && x < this.x + this.parent.panel.getWidth() && y > this.y - 1
				&& y < this.y + 12;
	}

	@Override
	public int getHeight() {
		return 12;
	}

	private void drawLabel(final String label) {
		mc.fontRendererObj.drawStringWithShadow(label, (this.parent.panel.getX() + 4) * 2,
				(this.parent.panel.getY() + this.yOffset + 3) * 2,
				Color.HSBtoRGB((float) (System.currentTimeMillis() % 3750L) / 3750.0F, 0.8F, 0.8F));
	}
}
