package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.PlayerInteractEntityC2SPacketInterface;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerInteractEntityC2SPacket.class)
public class PlayerInteractEntityC2SPacketMixin implements PlayerInteractEntityC2SPacketInterface {

    @Shadow
    @Final
    private int entityId;
    @Shadow
    @Final
    private PlayerInteractEntityC2SPacket.InteractTypeHandler type;

    @Override
    public PlayerInteractEntityC2SPacket.InteractType getInteractionType() {
        return this.type.getType();
    }

    @Override
    public int getEntityId() {
        return this.entityId;
    }
}
