package com.inertiaclient.base.gui.components.tabbedpage.impl;

import com.inertiaclient.base.render.skia.SkiaNativeRender;
import com.inertiaclient.base.render.yoga.YogaNode;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.awt.Color;

public class ItemRenderComponent extends YogaNode {

    private SkiaNativeRender skiaNativeRender;
    private static final ItemStackRenderState itemRenderState = new ItemStackRenderState();

    public ItemRenderComponent(Item item) {
        String id = BuiltInRegistries.ITEM.wrapAsHolder(item).getRegisteredName();
        this.setSearchContext(id);
        this.setTooltip(() -> id);
        this.setTooltipDelay(() -> 0L);


        skiaNativeRender = new SkiaNativeRender();
        skiaNativeRender.setNativeWidth(() -> 16f);
        skiaNativeRender.setNativeHeight(() -> 16f);
        skiaNativeRender.setSetNativeRender(drawContext -> {
            //TODO: fix
            /*ItemStack itemStack = new ItemStack(item);
            InertiaBase.mc.getItemModelResolver().updateForTopItem(itemRenderState, itemStack, ItemDisplayContext.GUI, false, null, null, 0);

            if (itemRenderState.isEmpty()) {
                drawContext.blit(RenderType::guiTextured, ModernClickGui.UNKNOWN_TEXTURE, 0, 0, 0, 0, (int) skiaNativeRender.getCachedNativeWidth(), (int) skiaNativeRender.getCachedNativeHeight(), (int) skiaNativeRender.getCachedNativeWidth(), (int) skiaNativeRender.getCachedNativeHeight());
            } else {
                drawContext.renderItem(itemStack, 0, 0);
                RenderSystem.enableBlend();
            }*/
        });


        this.styleSetHeight(16);
        this.styleSetWidth(16);
        //this.setDebug(true);

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {

            if (this.isHoveredAndInsideParent(globalMouseX, globalMouseY)) {
                canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), new Color(255, 255, 255, 100));
            }

            skiaNativeRender.update();
            skiaNativeRender.drawImageWithSkia(canvas, 0, 0);
        });
    }
}
