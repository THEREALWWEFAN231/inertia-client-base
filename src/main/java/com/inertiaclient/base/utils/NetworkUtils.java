package com.inertiaclient.base.utils;

import com.inertiaclient.base.mixin.mixins.accessors.MultiPlayerGameModeAccessor;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.multiplayer.ClientLevel;

import static com.inertiaclient.base.InertiaBase.mc;

public class NetworkUtils {

    public static void sendSequencedPacket(ClientLevel world, PredictiveAction packetCreator) {
        ((MultiPlayerGameModeAccessor) mc.gameMode).callStartPrediction(world, packetCreator);
    }

}
