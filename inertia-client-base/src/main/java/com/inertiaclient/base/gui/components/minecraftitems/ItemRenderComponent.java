package com.inertiaclient.base.gui.components.minecraftitems;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.render.skia.instances.SkiaNativeRender;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.awt.Color;

public class ItemRenderComponent extends ItemReferenceNode {

    private static final ItemStackRenderState ITEM_RENDER_STATE = new ItemStackRenderState();

    private SkiaNativeRender skiaNativeRender;

    public ItemRenderComponent(Item item) {
        this(item, BuiltInRegistries.ITEM.wrapAsHolder(item).getRegisteredName());
    }

    public ItemRenderComponent(Item item, String id) {
        super(item, id);

        skiaNativeRender = new SkiaNativeRender();
        skiaNativeRender.setNativeWidth(this::getWidth);
        skiaNativeRender.setNativeHeight(this::getHeight);
        skiaNativeRender.setSetNativeRender(graphics -> {
            ItemRenderComponent.renderItemForGui(new ItemStack(item), graphics);
        });

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {

            if (this.isHoveredAndInsideParent(globalMouseX, globalMouseY)) {
                canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), new Color(255, 255, 255, 100));
            }

            skiaNativeRender.update();
            skiaNativeRender.drawImageWithSkia(canvas, 0, 0);
        });
    }

    /**
     * Renders the item or UNKNOWN_TEXTURE at 0 0, width 16 height 16
     *
     * @param itemStack
     * @param graphics
     */
    public static void renderItemForGui(ItemStack itemStack, GuiGraphicsExtractor graphics) {
        InertiaBase.mc.getItemModelResolver().updateForTopItem(ITEM_RENDER_STATE, itemStack, ItemDisplayContext.GUI, null, null, 0);

        if (ITEM_RENDER_STATE.isEmpty()) {
            graphics.blit(ModernClickGui.UNKNOWN_TEXTURE, 0, 0, 16, 16, 0, 1, 0, 1);
        } else {
            graphics.item(itemStack, 0, 0);
        }
    }

}
