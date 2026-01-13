package org.afterlike.openutils.event.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.afterlike.openutils.event.api.Event;

public class AttackEntityEvent implements Event {
	private final EntityPlayer playerIn;
	private final Entity target;
	public AttackEntityEvent(EntityPlayer playerIn, Entity target) {
		this.playerIn = playerIn;
		this.target = target;
	}

	public EntityPlayer getPlayerIn() {
		return playerIn;
	}

	public Entity getTarget() {
		return target;
	}
}
