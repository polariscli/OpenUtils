package org.afterlike.openutils.platform.mixin.minecraftforge.fml.common.network.handshake;

import io.netty.buffer.ByteBuf;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FMLHandshakeMessage.ModList.class)
public class ModListMixin {
	@Shadow(remap = false)
	private Map<String, String> modTags;
	@Inject(method = "toBytes", at = @At("HEAD"), cancellable = true, remap = false)
	private void toBytes(final ByteBuf buffer, final CallbackInfo ci) {
		if (Minecraft.getMinecraft().isSingleplayer())
			return;
		ci.cancel();
		int count = 0;
		for (final String modId : modTags.keySet()) {
			if (!modId.equalsIgnoreCase("openutils")) {
				count++;
			}
		}
		ByteBufUtils.writeVarInt(buffer, count, 2);
		for (final Map.Entry<String, String> entry : modTags.entrySet()) {
			if (!entry.getKey().equalsIgnoreCase("openutils")) {
				ByteBufUtils.writeUTF8String(buffer, entry.getKey());
				ByteBufUtils.writeUTF8String(buffer, entry.getValue());
			}
		}
	}
}
