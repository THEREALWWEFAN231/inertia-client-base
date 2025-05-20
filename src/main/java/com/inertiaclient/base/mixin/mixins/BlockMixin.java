package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.BlockSlipperinessEvent;
import com.inertiaclient.base.event.impl.BlockVelocityMultiplierEvent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Block.class)
public class BlockMixin {

    @ModifyReturnValue(method = "getSlipperiness", at = @At("RETURN"))
    public float getSlipperiness(float orginal) {

        BlockSlipperinessEvent event = new BlockSlipperinessEvent((Block) (Object) this, orginal);
        EventManager.fire(event);
        if (event.isCancelled()) {
            return event.getSlipperiness();
        }
        return orginal;
    }

    @ModifyReturnValue(method = "getVelocityMultiplier", at = @At("RETURN"))
    public float getVelocityMultiplier(float orginal) {

        BlockVelocityMultiplierEvent event = new BlockVelocityMultiplierEvent((Block) (Object) this, orginal);
        EventManager.fire(event);
        if (event.isCancelled()) {
            return event.getVelocityMultiplier();
        }
        return orginal;
    }


}
