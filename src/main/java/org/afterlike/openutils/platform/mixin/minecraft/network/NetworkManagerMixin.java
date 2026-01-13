package org.afterlike.openutils.platform.mixin.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.event.impl.ReceiveChatEvent;
import org.afterlike.openutils.event.impl.ReceivePacketEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class NetworkManagerMixin {
	@Inject(method = "channelRead0*", at = @At("HEAD"))
	public void ou$channelRead0(final ChannelHandlerContext ctx, final Packet<?> packet,
			final CallbackInfo ci) {
		OpenUtils.get().getEventBus().post(new ReceivePacketEvent(packet));
		if (packet instanceof S02PacketChat) {
			final String formatted = ((S02PacketChat) packet).getChatComponent().getFormattedText();
			OpenUtils.get().getEventBus().post(new ReceiveChatEvent(formatted));
		}
	}
}
