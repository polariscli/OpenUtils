package org.afterlike.openutils.module.api.hud;

public final class Position {
	private final int defaultX;
	private final int defaultY;
	private int x;
	private int y;
	public Position(final int defaultX, final int defaultY) {
		this.defaultX = defaultX;
		this.defaultY = defaultY;
		this.x = defaultX;
		this.y = defaultY;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setPosition(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public void reset() {
		setPosition(defaultX, defaultY);
	}

	public Position copy() {
		return new Position(this.x, this.y);
	}
}
