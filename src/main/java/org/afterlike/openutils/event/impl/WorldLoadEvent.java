package org.afterlike.openutils.event.impl;

import net.minecraft.client.multiplayer.WorldClient;
import org.afterlike.openutils.event.api.Event;

public class WorldLoadEvent implements Event {
	private final WorldClient world;
	public WorldLoadEvent(final WorldClient world) {
		this.world = world;
	}

	public WorldClient getWorld() {
		return world;
	}
}
