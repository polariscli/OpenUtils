package org.afterlike.openutils.platform.mixin.minecraft.client.settings;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
	@Accessor("pressed")
	void ou$setPressed(boolean pressed);
}
