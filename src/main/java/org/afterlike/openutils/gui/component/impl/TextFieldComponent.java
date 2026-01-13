package org.afterlike.openutils.gui.component.impl;

import java.awt.*;
import net.minecraft.client.gui.Gui;
import org.afterlike.openutils.gui.component.Component;
import org.afterlike.openutils.module.api.setting.impl.TextFieldSetting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class TextFieldComponent extends Component {
	private static final int BACKGROUND_COLOR = new Color(30, 30, 30).getRGB();
	private static final int BORDER_COLOR = new Color(60, 60, 60).getRGB();
	private static final int FOCUSED_BORDER_COLOR = new Color(100, 150, 255).getRGB();
	private static final int TEXT_COLOR = Color.WHITE.getRGB();
	private static final int PLACEHOLDER_COLOR = new Color(128, 128, 128).getRGB();
	private static final int CURSOR_COLOR = Color.WHITE.getRGB();
	private final TextFieldSetting setting;
	private final ModuleComponent parent;
	private int yOffset;
	private int x;
	private int y;
	private boolean focused;
	private int cursorPosition;
	private long lastBlinkTime;
	private boolean cursorVisible;
	public TextFieldComponent(final TextFieldSetting setting, final ModuleComponent parent,
			final int yOffset) {
		this.setting = setting;
		this.parent = parent;
		this.x = parent.panel.getX();
		this.y = parent.panel.getY() + parent.yOffset;
		this.yOffset = yOffset;
		this.focused = false;
		this.cursorPosition = setting.getValue().length();
		this.lastBlinkTime = System.currentTimeMillis();
		this.cursorVisible = true;
	}

	@Override
	public void render() {
		final int panelX = this.parent.panel.getX();
		final int panelY = this.parent.panel.getY() + this.yOffset;
		final int panelWidth = this.parent.panel.getWidth();
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		mc.fontRendererObj.drawStringWithShadow(this.setting.getName() + ":", (panelX + 4) * 2,
				(panelY + 2) * 2, TEXT_COLOR);
		GL11.glPopMatrix();
		final int fieldX = panelX + 4;
		final int fieldY = panelY + 8;
		final int fieldWidth = panelWidth - 8;
		final int fieldHeight = 10;
		// border
		Gui.drawRect(fieldX - 1, fieldY - 1, fieldX + fieldWidth + 1, fieldY + fieldHeight + 1,
				this.focused ? FOCUSED_BORDER_COLOR : BORDER_COLOR);
		// bg
		Gui.drawRect(fieldX, fieldY, fieldX + fieldWidth, fieldY + fieldHeight, BACKGROUND_COLOR);
		// text
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		final String value = this.setting.getValue();
		final String displayText;
		final int textColor;
		if (value.isEmpty() && !this.focused) {
			displayText = this.setting.getPlaceholder();
			textColor = PLACEHOLDER_COLOR;
		} else {
			displayText = value;
			textColor = TEXT_COLOR;
		}
		// clip
		final int maxChars = (fieldWidth - 4) / 3; // approx.
		String visibleText = displayText;
		int textOffset = 0;
		if (this.focused && this.cursorPosition > maxChars) {
			textOffset = this.cursorPosition - maxChars;
			visibleText = displayText.substring(Math.min(textOffset, displayText.length()));
		}
		if (visibleText.length() > maxChars) {
			visibleText = visibleText.substring(0, maxChars);
		}
		mc.fontRendererObj.drawString(visibleText, (fieldX + 2) * 2, (fieldY + 2) * 2, textColor,
				false);
		// cursor
		if (this.focused) {
			updateCursorBlink();
			if (this.cursorVisible) {
				final int cursorX = (fieldX + 2 + mc.fontRendererObj.getStringWidth(
						value.substring(textOffset, Math.min(this.cursorPosition, value.length())))
						/ 2) * 2;
				final int cursorY1 = (fieldY + 1) * 2;
				final int cursorY2 = (fieldY + fieldHeight - 1) * 2;
				Gui.drawRect(cursorX, cursorY1, cursorX + 1, cursorY2, CURSOR_COLOR);
			}
		}
		GL11.glPopMatrix();
	}

	private void updateCursorBlink() {
		final long now = System.currentTimeMillis();
		if (now - this.lastBlinkTime > 500) {
			this.cursorVisible = !this.cursorVisible;
			this.lastBlinkTime = now;
		}
	}

	@Override
	public void setOffset(final int yOffset) {
		this.yOffset = yOffset;
	}

	@Override
	public void update(final int mouseX, final int mouseY) {
		this.y = this.parent.panel.getY() + this.yOffset;
		this.x = this.parent.panel.getX();
	}

	@Override
	public void onClick(final int mouseX, final int mouseY, final int button) {
		if (button == 0 && this.parent.expandedSettings) {
			final boolean wasInside = this.isMouseOver(mouseX, mouseY);
			if (wasInside) {
				this.focused = true;
				this.cursorPosition = this.setting.getValue().length();
				this.cursorVisible = true;
				this.lastBlinkTime = System.currentTimeMillis();
			} else if (this.focused) {
				this.focused = false;
				this.parent.module.onSettingChanged(this.setting);
			}
		}
	}

	@Override
	public void onKeyTyped(final char key, final int keyCode) {
		if (!this.focused) {
			return;
		}
		final String value = this.setting.getValue();
		switch (keyCode) {
			case Keyboard.KEY_RETURN :
			case Keyboard.KEY_NUMPADENTER :
				this.focused = false;
				this.parent.module.onSettingChanged(this.setting);
				break;
			case Keyboard.KEY_BACK :
				if (this.cursorPosition > 0) {
					final String newValue = value.substring(0, this.cursorPosition - 1)
							+ value.substring(this.cursorPosition);
					this.setting.setValue(newValue);
					this.cursorPosition--;
				}
				break;
			case Keyboard.KEY_DELETE :
				if (this.cursorPosition < value.length()) {
					final String newValue = value.substring(0, this.cursorPosition)
							+ value.substring(this.cursorPosition + 1);
					this.setting.setValue(newValue);
				}
				break;
			case Keyboard.KEY_LEFT :
				if (this.cursorPosition > 0) {
					this.cursorPosition--;
				}
				break;
			case Keyboard.KEY_RIGHT :
				if (this.cursorPosition < value.length()) {
					this.cursorPosition++;
				}
				break;
			case Keyboard.KEY_HOME :
				this.cursorPosition = 0;
				break;
			case Keyboard.KEY_END :
				this.cursorPosition = value.length();
				break;
			default :
				if (isPrintableChar(key)) {
					final String newValue = value.substring(0, this.cursorPosition) + key
							+ value.substring(this.cursorPosition);
					this.setting.setValue(newValue);
					this.cursorPosition++;
				}
				break;
		}
		this.cursorVisible = true;
		this.lastBlinkTime = System.currentTimeMillis();
	}

	private static boolean isPrintableChar(final char c) {
		return c >= 32 && c < 127;
	}

	public boolean isMouseOver(final int mouseX, final int mouseY) {
		return mouseX > this.x && mouseX < this.x + this.parent.panel.getWidth() && mouseY > this.y
				&& mouseY < this.y + 20;
	}

	public boolean isFocused() {
		return this.focused;
	}

	@Override
	public int getHeight() {
		return 20;
	}
}
