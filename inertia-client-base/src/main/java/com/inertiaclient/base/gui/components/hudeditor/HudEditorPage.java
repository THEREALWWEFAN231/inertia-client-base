package com.inertiaclient.base.gui.components.hudeditor;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.components.helpers.VerticalListContainer;
import com.inertiaclient.base.hud.HudComponent;
import com.inertiaclient.base.hud.HudGroup;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.AbsoulteYogaNode;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class HudEditorPage extends VerticalListContainer {

    @Setter
    private HudComponent previewingComponent;

    //previewingComponent is being changed by a child before we reset it to its real group
    private HudComponent currentFrameComponent;
    private HudGroup realGroup;

    private HudGroup fakePreviewGroup = new HudGroup() {
        //doesn't remove add from it's original/real group
        @Override
        public HudComponent addComponent(HudComponent add, int atIndex) {
            this.components.add(atIndex, add);
            add.setGroup(this);
            return add;
        }

        @Override
        public void removeComponent(HudComponent hudComponent) {
            this.components.remove(hudComponent);
        }

        @Override
        public void doComponentsBeforeRender(float mouseX, float mouseY, float tickDelta, boolean editor, CanvasWrapper canvas) {
            this.enabledComponents.clear();
            this.enabledComponents.addAll(this.components);

            for (HudComponent component : this.enabledComponents) {
                component.beforeRender(true, editor, canvas);
            }
        }
    };

    public HudEditorPage() {
        for (HudComponent element : InertiaBase.instance.getHudManager().getComponents()) {
            this.addToList(new HudElementComponent(this, element));
        }

        AbsoulteYogaNode previewNode = new AbsoulteYogaNode() {
            public void setGlobalPositions(float currentX, float currentY, GuiGraphicsExtractor graphics, float globalMouseX, float globalMouseY, float delta) {
                if (HudEditorPage.this.currentFrameComponent != null) {
                    this.setX((globalMouseX - currentX) - (this.getWidth() / 2));
                    this.setY((globalMouseY - currentY) - this.getHeight() - 5);
                }
                super.setGlobalPositions(currentX, currentY, graphics, globalMouseX, globalMouseY, delta);
            }
        };
        previewNode.setPreLayoutCalculationsCallback((graphics, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            this.currentFrameComponent = this.previewingComponent;

            if (this.currentFrameComponent != null) {

                this.realGroup = this.currentFrameComponent.getGroup();
                this.fakePreviewGroup.addComponent(this.currentFrameComponent, 0);
                this.fakePreviewGroup.doComponentsBeforeRender(0, 0, 0, false, canvas);
                this.fakePreviewGroup.prepareForRender(0, 0, 0, false);
                previewNode.setWidth(this.fakePreviewGroup.getScaledWidth() + 5);
                previewNode.setHeight(this.fakePreviewGroup.getScaledHeight() + 5);
            }
        });
        previewNode.setRenderCallback((graphics, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            if (this.currentFrameComponent == null) {
                return;
            }
            //lmao why would I do it like this... "janky" but I feel like its fine
            var highlightedComponent = InertiaBase.instance.getHudManager().getHighlightedComponent();
            InertiaBase.instance.getHudManager().setHighlightedComponent(null);
            //"shouldn't" happen but just in case, don't mess up the group/s
            try {

                //this.group.setPositionBasedOfScreen(graphics.guiWidth(), graphics.guiHeight());
                this.fakePreviewGroup.setX(5 / 2f);
                this.fakePreviewGroup.setY(5 / 2f);

                canvas.drawTooltip(0, 0, previewNode.getWidth(), previewNode.getHeight(), 2, previewNode.getWidth() / 2, 2);
                canvas.save();
                this.fakePreviewGroup.renderGroup(graphics, 0, 0, 0, false, canvas);
                canvas.restore();
            } catch (Exception e) {
                InertiaBase.LOGGER.error("Error when previewing hud component {}", this.currentFrameComponent.getName(), e);
            } finally {
                this.fakePreviewGroup.removeComponent(this.currentFrameComponent);
                this.currentFrameComponent.setGroup(this.realGroup);
                InertiaBase.instance.getHudManager().setHighlightedComponent(highlightedComponent);
            }
        });
        this.addAbsoluteChild(previewNode);


    }

}
