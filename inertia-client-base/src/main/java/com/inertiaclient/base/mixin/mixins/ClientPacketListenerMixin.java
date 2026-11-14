package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.BlockUpdateEvent;
import com.inertiaclient.base.event.impl.ChunkLoadEvent;
import com.inertiaclient.base.event.impl.ChunkUnloadEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Unique
    private static final String icb$ENSURE_RUNNING_ON_SAME_THREAD = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V";

    @Shadow
    private ClientLevel level;

    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String content, CallbackInfo callbackInfo) {
        if (content.startsWith(InertiaBase.instance.getCommandManager().getPrefix())) {
            InertiaBase.instance.getCommandManager().runCommand(content);
            callbackInfo.cancel();
        }
    }

    @Inject(method = "handleLevelChunkWithLight", at = @At("RETURN"))
    public void handleLevelChunkWithLight(ClientboundLevelChunkWithLightPacket packet, CallbackInfo callbackInfo) {
        LevelChunk chunk = this.level.getChunkSource().getChunk(packet.x(), packet.z(), false);
        if (chunk != null) {
            EventManager.fire(new ChunkLoadEvent(chunk, this.level));
        }
    }

    @Inject(method = "handleForgetLevelChunk", at = @At(value = "INVOKE", target = icb$ENSURE_RUNNING_ON_SAME_THREAD, shift = At.Shift.AFTER))
    public void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket packet, CallbackInfo callbackInfo) {
        EventManager.fire(new ChunkUnloadEvent(packet.pos()));
    }

    @Inject(method = "handleBlockUpdate", at = @At(value = "INVOKE", target = icb$ENSURE_RUNNING_ON_SAME_THREAD, shift = At.Shift.AFTER))
    public void handleBlockUpdate(ClientboundBlockUpdatePacket packet, CallbackInfo callbackInfo) {
        EventManager.fire(new BlockUpdateEvent(packet.getPos(), this.level.getBlockState(packet.getPos()), packet.getBlockState()));
    }

    @Inject(method = "handleChunkBlocksUpdate", at = @At(value = "INVOKE", target = icb$ENSURE_RUNNING_ON_SAME_THREAD, shift = At.Shift.AFTER))
    public void handleChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket packet, CallbackInfo callbackInfo) {
        packet.runUpdates((pos, state) -> {
            EventManager.fire(new BlockUpdateEvent(pos.immutable(), this.level.getBlockState(pos), state));
        });
    }

}
