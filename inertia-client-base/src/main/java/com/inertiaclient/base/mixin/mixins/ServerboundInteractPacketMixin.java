package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.PlayerInteractEntityC2SPacketInterface;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerboundInteractPacket.class)
public class ServerboundInteractPacketMixin implements PlayerInteractEntityC2SPacketInterface {

    @Shadow
    @Final
    private int entityId;
    @Shadow
    @Final
    private ServerboundInteractPacket.Action action;

    @Override
    public ServerboundInteractPacket.ActionType getInteractionType() {
        return this.action.getType();
    }

    @Override
    public int getEntityId() {
        return this.entityId;
    }
}
