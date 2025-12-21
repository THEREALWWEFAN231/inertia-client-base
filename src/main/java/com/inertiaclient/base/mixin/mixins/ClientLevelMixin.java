package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.EntityRemoveEvent;
import com.inertiaclient.base.event.impl.EntitySpawnEvent;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Inject(method = "addEntity", at = @At("RETURN"))
    public void addEntity(Entity entity, CallbackInfo callbackInfo) {
        EventManager.fire(new EntitySpawnEvent(entity, ((ClientLevel) (Object) this)));
    }

    @Inject(method = "removeEntity", at = @At("RETURN"))
    public void removeEntity(int entityId, Entity.RemovalReason removalReason, CallbackInfo callbackInfo, @Local LocalRef<Entity> entityRef) {
        Entity entity;
        if ((entity = entityRef.get()) != null) {
            EventManager.fire(new EntityRemoveEvent(entity, ((ClientLevel) (Object) this)));
        }
    }
}
