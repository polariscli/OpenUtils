package org.afterlike.openutils.event.impl;

import org.afterlike.openutils.event.api.Event;

public class MouseScrollEvent implements Event {
	private final int dWheel;
	public MouseScrollEvent(int dWheel) {
		this.dWheel = dWheel;
	}

	public int getDWheel() {
		return dWheel;
	}
}
