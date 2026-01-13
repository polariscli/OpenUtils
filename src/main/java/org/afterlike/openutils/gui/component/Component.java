package org.afterlike.openutils.gui.component;

import net.minecraft.client.Minecraft;

public class Component {
	protected static final Minecraft mc = Minecraft.getMinecraft();
	public void render() {
	}

	public void update(final int mouseX, final int mouseY) {
	}

	public void onClick(final int mouseX, final int mouseY, final int button) {
	}

	public void onMouseRelease(final int mouseX, final int mouseY, final int state) {
	}

	public void onKeyTyped(final char key, final int keyCode) {
	}

	public void setOffset(final int yOffset) {
	}

	public int getHeight() {
		return 0;
	}
}
