package org.afterlike.openutils.event.impl;

import org.afterlike.openutils.event.api.Event;
import org.afterlike.openutils.event.api.EventPhase;

public class GameTickEvent implements Event {
	private final EventPhase phase;
	public GameTickEvent(final EventPhase phase) {
		this.phase = phase;
	}

	public EventPhase getPhase() {
		return phase;
	}
}
