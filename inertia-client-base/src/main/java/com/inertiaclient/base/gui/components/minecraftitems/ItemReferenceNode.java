package com.inertiaclient.base.gui.components.minecraftitems;

import com.inertiaclient.base.render.yoga.YogaNode;
import lombok.Getter;
import net.minecraft.world.item.Item;

public class ItemReferenceNode extends YogaNode {

    @Getter
    private Item item;

    public ItemReferenceNode(Item item, String id) {
        this.item = item;

        this.setSearchContext(id);
        this.setTooltip(() -> id);
        this.setTooltipDelay(() -> 0L);

        this.styleSetHeight(16);
        this.styleSetWidth(16);
    }

}
