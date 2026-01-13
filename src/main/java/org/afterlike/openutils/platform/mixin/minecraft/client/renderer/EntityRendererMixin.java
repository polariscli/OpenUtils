package org.afterlike.openutils.platform.mixin.minecraft.client.renderer;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.event.impl.RenderWorldEvent;
import org.afterlike.openutils.module.api.setting.impl.NumberSetting;
import org.afterlike.openutils.module.impl.render.CameraModule;
import org.afterlike.openutils.module.impl.render.FreeLookModule;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
	@ModifyArg(method = "hurtCameraEffect",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V"),
			index = 0)
	private float ou$modifyHurtCameraEffect(float angle) {
		if (OpenUtils.get().getModuleHandler().isEnabled(CameraModule.class)) {
			return angle / 14 * Objects
					.requireNonNull(
							OpenUtils.get().getModuleHandler().getModuleClass(CameraModule.class)
									.getSetting("Hurt Shake Multiplier", NumberSetting.class))
					.getFloat();
		}
		return angle;
	}

	@Redirect(method = "orientCamera", at = @At(value = "FIELD",
			target = "Lnet/minecraft/entity/Entity;rotationYaw:F", opcode = Opcodes.GETFIELD))
	private float ou$orientCamera$rotationYaw(final Entity instance) {
		return OpenUtils.get().getModuleHandler().isEnabled(FreeLookModule.class)
				? OpenUtils.get().getModuleHandler().getModuleClass(FreeLookModule.class)
						.getCameraYaw()
				: instance.rotationYaw;
	}

	@Redirect(method = "orientCamera", at = @At(value = "FIELD",
			target = "Lnet/minecraft/entity/Entity;prevRotationYaw:F", opcode = Opcodes.GETFIELD))
	private float ou$orientCamera$prevRotationYaw(final Entity instance) {
		return OpenUtils.get().getModuleHandler().isEnabled(FreeLookModule.class)
				? OpenUtils.get().getModuleHandler().getModuleClass(FreeLookModule.class)
						.getCameraYaw()
				: instance.prevRotationYaw;
	}

	@Redirect(method = "orientCamera", at = @At(value = "FIELD",
			target = "Lnet/minecraft/entity/Entity;rotationPitch:F", opcode = Opcodes.GETFIELD))
	private float ou$orientCamera$rotationPitch(final Entity instance) {
		return OpenUtils.get().getModuleHandler().isEnabled(FreeLookModule.class)
				? OpenUtils.get().getModuleHandler().getModuleClass(FreeLookModule.class)
						.getCameraPitch()
				: instance.rotationPitch;
	}

	@Redirect(method = "orientCamera", at = @At(value = "FIELD",
			target = "Lnet/minecraft/entity/Entity;prevRotationPitch:F", opcode = Opcodes.GETFIELD))
	private float ou$orientCamera$prevRotationPitch(final Entity instance) {
		return OpenUtils.get().getModuleHandler().isEnabled(FreeLookModule.class)
				? OpenUtils.get().getModuleHandler().getModuleClass(FreeLookModule.class)
						.getCameraPitch()
				: instance.prevRotationPitch;
	}

	@Redirect(method = "updateCameraAndRender", at = @At(value = "FIELD",
			target = "Lnet/minecraft/client/Minecraft;inGameHasFocus:Z", opcode = Opcodes.GETFIELD))
	private boolean ou$updateCameraAndRender$overrideMouse(final Minecraft instance) {
		if (OpenUtils.get().getModuleHandler().isEnabled(FreeLookModule.class)) {
			return OpenUtils.get().getModuleHandler().getModuleClass(FreeLookModule.class)
					.overrideMouse();
		}
		return instance.inGameHasFocus;
	}

	@Inject(method = "renderWorldPass", at = @At("RETURN"))
	private void ou$renderWorldPass(final int pass, final float partialTicks,
			final long finishTimeNano, final CallbackInfo ci) {
		OpenUtils.get().getEventBus().post(new RenderWorldEvent(partialTicks));
	}
}
