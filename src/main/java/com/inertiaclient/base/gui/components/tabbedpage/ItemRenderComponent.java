package com.inertiaclient.base.gui.components.tabbedpage;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.render.skia.SkiaNativeRender;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.registry.Registries;

import java.awt.Color;

public class ItemRenderComponent extends YogaNode {

    private SkiaNativeRender skiaNativeRender;
    private static final ItemRenderState itemRenderState = new ItemRenderState();

    public ItemRenderComponent(Item item) {
        String id = Registries.ITEM.getEntry(item).getIdAsString();
        this.setSearchContext(id);
        this.setTooltip(() -> id);
        this.setTooltipDelay(() -> 0L);


        skiaNativeRender = new SkiaNativeRender();
        skiaNativeRender.setSetNativeRender(drawContext -> {
            ItemStack itemStack = new ItemStack(item);
            InertiaBase.mc.getItemModelManager().update(itemRenderState, itemStack, ModelTransformationMode.GUI, false, null, null, 0);

            if (itemRenderState.isEmpty()) {
                drawContext.drawTexture(RenderLayer::getGuiTextured, ModernClickGui.UNKNOWN_TEXTURE, 0, 0, 0, 0, skiaNativeRender.getNativeWidth(), skiaNativeRender.getNativeHeight(), skiaNativeRender.getNativeWidth(), skiaNativeRender.getNativeHeight());
            } else {
                drawContext.drawItem(itemStack, 0, 0);
                RenderSystem.enableBlend();
            }
        });


        this.styleSetHeight(16);
        this.styleSetWidth(16);
        //this.setDebug(true);

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {

            if (this.isHoveredAndInsideParent(globalMouseX, globalMouseY)) {
                canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), new Color(255, 255, 255, 100));
            }

            skiaNativeRender.setNativeWidth(16);
            skiaNativeRender.setNativeHeight(16);
            skiaNativeRender.update(context);
            skiaNativeRender.drawNanoImage(canvas, 0, 0);
        });
    }
}
