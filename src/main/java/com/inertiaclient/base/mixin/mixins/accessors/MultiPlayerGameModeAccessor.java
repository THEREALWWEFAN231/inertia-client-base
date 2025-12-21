package com.inertiaclient.base.mixin.mixins.accessors;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeAccessor {

    @Invoker("startPrediction")
    void callStartPrediction(ClientLevel world, PredictiveAction packetCreator);
}
