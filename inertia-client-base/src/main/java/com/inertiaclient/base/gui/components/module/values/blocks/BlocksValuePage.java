package com.inertiaclient.base.gui.components.module.values.blocks;

import com.inertiaclient.base.gui.components.NativeRenderComponent;
import com.inertiaclient.base.gui.components.minecraftitems.BlockReferenceNode;
import com.inertiaclient.base.gui.components.minecraftitems.ItemRenderComponent;
import com.inertiaclient.base.gui.components.tabbedpage.AbstractWrappedListContainer;
import com.inertiaclient.base.gui.components.tabbedpage.Tab;
import com.inertiaclient.base.gui.components.tabbedpage.impl.HashsetPage;
import com.inertiaclient.base.render.skia.instances.SkiaNativeRender;
import com.inertiaclient.base.render.yoga.layouts.Display;
import com.inertiaclient.base.value.HashsetValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.awt.Color;

/**
 * This is pretty wild lmao
 * hashset page creates a wrapped container, which is NativeBatchedNode
 * <p>
 * NativeBatchedNode has a list node({@link NativeRenderComponent}) which contains {@link BlockReferenceNode}, which are just a reference, they are added to the layout, but themselves, do not render anything
 * <p>
 * the list node then goes through all of its children {@link BlockReferenceNode} and renders an item/block where the {@link BlockReferenceNode} would render
 * <p>
 * this allows for us to have one Native image rendering all the items rather than an outrageous amount, which helps performance, we also only update the native image 20 times a second, which also helps performance
 */
public class BlocksValuePage extends HashsetPage<Block> {

    public BlocksValuePage(HashsetValue<Block> hashsetValue) {
        super(hashsetValue, block -> new BlockReferenceNode(block));
    }

    @Override
    public AbstractWrappedListContainer<?> createTabWrappedContainer() {
        return new NativeBatchedNode();
    }

    @Override
    public void onModified(ModificationType modificationType, Tab<AbstractWrappedListContainer<?>> tab) {
        ((NativeBatchedNode) tab.getYogaNode()).getListNode().getSkiaNativeRender().getFrameBuffer().forceUpdateNextFrame();
    }

    public static class NativeBatchedNode extends AbstractWrappedListContainer<NativeRenderComponent> {

        public NativeBatchedNode() {
            this.getListNode().setBeforeNativeRenderCallback((graphics, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
                this.getListNode().getChildren().forEach(yogaNode -> {
                    if (yogaNode.isHoveredAndInsideParent(globalMouseX, globalMouseY)) {
                        canvas.drawRect(yogaNode.getRelativeX(), yogaNode.getRelativeY(), yogaNode.getWidth(), yogaNode.getHeight(), new Color(255, 255, 255, 100));
                    }
                });
            });
            this.setScrolledCallback((scrollReason, scrollDirection, relativeMouseX, relativeMouseY, amount) -> {
                this.getListNode().getSkiaNativeRender().getFrameBuffer().forceUpdateNextFrame();
            });

        }

        public NativeRenderComponent createListNodeType() {
            SkiaNativeRender nativeRender = new SkiaNativeRender().setSetNativeRender(guiGraphicsExtractor -> {
                this.getListNode().getChildren().forEach(yogaNode -> {
                    if (yogaNode.getDisplay() == Display.FLEX && yogaNode instanceof BlockReferenceNode refNode && this.getListNode().isChildInRenderBounds(yogaNode)) {
                        guiGraphicsExtractor.pose().pushMatrix();
                        guiGraphicsExtractor.pose().translate(yogaNode.getRelativeX(), yogaNode.getRelativeY());
                        ItemRenderComponent.renderItemForGui(new ItemStack(refNode.getItem()), guiGraphicsExtractor);
                        guiGraphicsExtractor.pose().popMatrix();
                    }
                });
            });
            nativeRender.getFrameBuffer().setFps(() -> 20);
            return new NativeRenderComponent(nativeRender);
        }
    }

}
