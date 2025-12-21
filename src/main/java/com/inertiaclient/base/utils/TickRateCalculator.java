package com.inertiaclient.base.utils;

import com.inertiaclient.base.event.EventListener;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.EventTarget;
import com.inertiaclient.base.event.impl.PlayerUpdateEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Mth;

import java.util.ArrayDeque;
import java.util.Deque;

public class TickRateCalculator {

    @Getter
    private float tickRate = 20;

    private long lastUpdateTime = -1;
    private Deque<Long> samples = new ArrayDeque<>();
    private int maxSamples = 5;
    @Setter
    private boolean isTickRateActive;

    private long lastSample;

    public TickRateCalculator() {
        EventManager.register(this);
    }

    @EventTarget
    private final EventListener<PlayerUpdateEvent> playerUpdateListener = this::onEvent;

    private void onEvent(PlayerUpdateEvent event) {
        if (!isTickRateActive) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        long timeSinceLastTimeUpdate = currentTime - lastUpdateTime;

        float lastSampleTickRate = 20f / (lastSample / 1000f);
        float ticksFromLastTimeUpdate = timeSinceLastTimeUpdate / (1000 / 20f);

        float factor = 0;//higher the factor the slower tick rate moves
        if (lastSampleTickRate > 15) {
            factor = 10;
        } else if (lastSampleTickRate > 10) {
            factor = 30;
        } else if (lastSampleTickRate > 5) {
            factor = 40;
        } else {
            factor = 50;
        }

        tickRate = ((lastSampleTickRate * factor) - ticksFromLastTimeUpdate) / factor;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();

        if (lastUpdateTime == -1) {
            lastUpdateTime = currentTime;
            return;
        }

        long millisecondsFromLastTimeUpdate = currentTime - lastUpdateTime;
        samples.push(millisecondsFromLastTimeUpdate);//adds to the first of the queue/list

        if (samples.size() > maxSamples) {
            samples.removeLast();
        }

        if (!isTickRateActive) {
            float averageMillisecondsFromUpdates = 0;
            for (long sample : samples) {
                averageMillisecondsFromUpdates += sample;
            }
            averageMillisecondsFromUpdates /= samples.size();
            float tickRate = 20 / (averageMillisecondsFromUpdates / 1000);
            this.tickRate = Mth.clamp(tickRate, 0, 20);
        }

        lastUpdateTime = currentTime;
        lastSample = millisecondsFromLastTimeUpdate;
    }

    public void changeNumberOfSamples(int numberOfSamples) {
        if (numberOfSamples < 1) {
            return;
        }
        maxSamples = numberOfSamples;
        while (samples.size() > maxSamples) {
            samples.removeLast();
        }

    }

}
