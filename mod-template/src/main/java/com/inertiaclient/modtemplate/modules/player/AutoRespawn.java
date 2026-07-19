package com.inertiaclient.modtemplate.modules.player;

import com.inertiaclient.base.event.EventListener;
import com.inertiaclient.base.event.EventTarget;
import com.inertiaclient.base.event.impl.PlayerUpdateEvent;
import com.inertiaclient.base.module.Category;
import com.inertiaclient.base.module.Module;
import net.minecraft.client.gui.screens.DeathScreen;

public class AutoRespawn extends Module {

    @EventTarget
    private final EventListener<PlayerUpdateEvent> playerUpdateListener = this::onEvent;

    public AutoRespawn() {
        super("auto_respawn", Category.Player);
    }

    public void onEvent(PlayerUpdateEvent event) {
        if (this.mc.gui.screen() instanceof DeathScreen && this.mc.player != null) {
            this.mc.player.respawn();
            this.mc.gui.setScreen(null);
        }
    }

}