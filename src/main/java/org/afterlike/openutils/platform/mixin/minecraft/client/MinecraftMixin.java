package org.afterlike.openutils.platform.mixin.minecraft.client;

import net.minecraft.client.Minecraft;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.event.api.EventPhase;
import org.afterlike.openutils.event.impl.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Inject(method = "startGame", at = @At("HEAD"))
	private void startGame$head(final @NotNull CallbackInfo callbackInfo) {
		OpenUtils.get().initialize();
	}

	@Inject(method = "startGame", at = @At(value = "CONSTANT", args = "stringValue=Post startup"))
	private void ou$startGame$postStartup(@NotNull final CallbackInfo ci) {
		OpenUtils.get().lateInitialize();
	}

	@Inject(method = "updateFramebufferSize", at = @At("RETURN"))
	private void ou$updateFramebufferSize(@NotNull final CallbackInfo ci) {
		OpenUtils.get().getEventBus().post(new ResizeWindowEvent());
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE",
			target = "Lorg/lwjgl/input/Keyboard;next()Z", shift = At.Shift.AFTER, remap = false))
	private void ou$runTick$Keyboard$next(CallbackInfo ci) {
		int keyCode = Keyboard.getEventKey();
		boolean pressed = Keyboard.getEventKeyState();
		OpenUtils.get().getEventBus().post(new KeyPressEvent(keyCode, pressed));
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE",
			target = "Lorg/lwjgl/input/Mouse;next()Z", shift = At.Shift.AFTER, remap = false))
	private void ou$runTick$Mouse$next(CallbackInfo ci) {
		int button = Mouse.getEventButton();
		boolean state = Mouse.getEventButtonState();
		int dWheel = Mouse.getEventDWheel();
		if (dWheel != 0) {
			OpenUtils.get().getEventBus().post(new MouseScrollEvent(dWheel));
		}
		if (button >= 0) {
			OpenUtils.get().getEventBus().post(new MouseButtonEvent(button, state));
		}
	}

	@Inject(method = "runTick", at = @At("HEAD"))
	private void ou$runTick$head(@NotNull final CallbackInfo ci) {
		OpenUtils.get().getEventBus().post(new GameTickEvent(EventPhase.PRE));
	}

	@Inject(method = "runTick", at = @At("RETURN"))
	private void ou$runTick$return(@NotNull final CallbackInfo ci) {
		OpenUtils.get().getEventBus().post(new GameTickEvent(EventPhase.POST));
	}
}
