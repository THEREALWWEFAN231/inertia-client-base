package com.inertiaclient.base.hud;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.SkiaOpenGLInstance;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.utils.InputUtils;
import com.inertiaclient.base.utils.UIUtils;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.ArrayList;

@RequiredArgsConstructor
public class HudEditor {

    @NonNull
    private Screen parentScreen;
    private HudGroup draggingGroup;
    private float dragStartX;
    private float dragStartY;
    private HudGroup dragLinkedToComponent;//the component we are going to link to when the mouse is released
    private int dragLinkToGroupAtIndex = -1;

    public void beforeRender(SkiaOpenGLInstance skiaInstance) {
        InertiaBase.instance.getHudManager().beforeRender(skiaInstance, true);
    }

    public void render(GuiGraphics context, float mouseX, float mouseY, float delta, CanvasWrapper canvas) {

        if (draggingGroup != null) {

            boolean isLeftShiftDown = InputUtils.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT);

            Color segmentsColor = new Color(255, 69, 69);
            try (Paint paint = SkiaUtils.createPaintForColor(segmentsColor)) {
                for (int i = 0; i < 2; i++) {
                    canvas.drawRect(Rect.makeXYWH(this.parentScreen.width * ((i + 1) / 3f) - .5f, 0, 1, this.parentScreen.height), paint);
                }
                for (int i = 0; i < 2; i++) {
                    canvas.drawRect(Rect.makeXYWH(0, this.parentScreen.height * ((i + 1) / 3f) - .5f, this.parentScreen.width, 1), paint);
                }

                //center
                canvas.drawRect(Rect.makeXYWH(this.parentScreen.width * (.5f) - .5f, 0, 1, this.parentScreen.height), paint);
                canvas.drawRect(Rect.makeXYWH(0, this.parentScreen.height * (.5f) - .5f, this.parentScreen.width, 1), paint);
            }

            this.draggingGroup.setAutomaticAlignmentStates(mouseX, mouseY, this.parentScreen.width, this.parentScreen.height);

            float snap = 5;
            float x = mouseX - dragStartX;
            float y = mouseY - dragStartY;
            if (snap != 1 && false) {
                x = (Math.round(x / (snap * 2)) * (snap * 2));
                y = (Math.round(y / (snap * 2)) * (snap * 2));
            }


            if (!isLeftShiftDown) {
                int snapAmount = 4;

                int xAnchors[] = {0, (int) (this.parentScreen.width * (1 / 3f)), (int) (this.parentScreen.width * (.5f)), (int) (this.parentScreen.width * (2 / 3f))};
                int xEndAnchors[] = {this.parentScreen.width, xAnchors[1], xAnchors[2], xAnchors[3]};


                for (int xAnchor : xAnchors) {
                    if (Math.abs(x - xAnchor) < snapAmount) {
                        x = xAnchor;
                        break;
                    }
                }
                for (int xAnchor : xEndAnchors) {
                    if (Math.abs(x + this.draggingGroup.getScaledWidth() - xAnchor) < snapAmount) {
                        x = xAnchor - this.draggingGroup.getScaledWidth();
                        break;
                    }
                }

                int[] yAnchors = {0, (int) (this.parentScreen.height * (1 / 3f)), (int) (this.parentScreen.height * (.5f)), (int) (this.parentScreen.height * (2 / 3f))};
                int[] yEndAnchors = {this.parentScreen.height, yAnchors[1], yAnchors[2], yAnchors[3]};

                for (int yAnchor : yAnchors) {
                    if (Math.abs(y - yAnchor) < snapAmount) {
                        y = yAnchor;
                        break;
                    }
                }
                for (int yAnchor : yEndAnchors) {
                    if (Math.abs(y + this.draggingGroup.getScaledHeight() - yAnchor) < snapAmount) {
                        y = yAnchor - this.draggingGroup.getScaledHeight();
                        break;
                    }
                }
            }

            this.draggingGroup.setXPercentage(x, parentScreen.width);
            this.draggingGroup.setYPercentage(y, parentScreen.height);
            InertiaBase.instance.getFileManager().getHudQueuedSave().queue();

            this.dragLinkedToComponent = null;
            this.dragLinkToGroupAtIndex = -1;

            if (!isLeftShiftDown) {
                HudGroup closestGroup = this.getClosestGroup(mouseX, mouseY);
                if (closestGroup != null) {
                    float snapAmount = 4;

                    int addAtIndex = -1;
                    if (mouseX >= closestGroup.getX() && mouseX < closestGroup.getX() + closestGroup.getScaledWidth()) {

                        if (mouseY >= closestGroup.getY() - snapAmount && mouseY < closestGroup.getY()) {
                            addAtIndex = 0;
                        } else if (mouseY >= closestGroup.getY() + closestGroup.getScaledHeight() && mouseY < closestGroup.getY() + closestGroup.getScaledHeight() + snapAmount) {
                            addAtIndex = closestGroup.getComponents().size();
                        }

                    }

                    if (closestGroup.isHovered(mouseX, mouseY)) {
                        addAtIndex = closestGroup.getHudComponentIndexAtY(mouseY - closestGroup.getY());
                    }

                    if (addAtIndex != -1) {
                        float heightOffset = closestGroup.getHeightForIndex(addAtIndex);
                        try (Paint paint = new Paint().setARGB(255, 246, 230, 0)) {
                            canvas.drawRect(Rect.makeXYWH(0, closestGroup.getY() + heightOffset, this.parentScreen.width, 1), paint);
                        }

                        this.dragLinkedToComponent = closestGroup;
                        this.dragLinkToGroupAtIndex = addAtIndex;
                    }

                }
            }

        } else {
            for (int i = InertiaBase.instance.getHudManager().getGroups().size() - 1; i >= 0; i--) {
                HudGroup group = InertiaBase.instance.getHudManager().getGroups().get(i);
                if (UIUtils.isHovered(mouseX, mouseY, group.getX(), group.getY(), group.getScaledWidth(), group.getScaledHeight())) {

                    Color hoverColor = new Color(145, 145, 145, 116);
                    int indexOfHoveredComponent = group.getHudComponentIndexAtY(mouseY - group.getY());
                    if (indexOfHoveredComponent == 0) {
                        try (Paint paint = SkiaUtils.createPaintForColor(hoverColor)) {
                            canvas.drawRect(Rect.makeXYWH(group.getX(), group.getY(), group.getScaledWidth(), group.getScaledHeight()), paint);
                        }
                    } else {
                        float height = 0;
                        for (int i1 = indexOfHoveredComponent; i1 < group.getEnabledComponents().size(); i1++) {
                            height += group.getHeightForComponent(i1, group.getEnabledComponents().get(i1));
                        }
                        try (Paint paint = SkiaUtils.createPaintForColor(hoverColor)) {
                            canvas.drawRect(Rect.makeXYWH(group.getX(), group.getY() + group.getHeightForIndex(indexOfHoveredComponent), group.getScaledWidth(), height), paint);
                        }
                    }

                    break;
                }
            }
        }
        InertiaBase.instance.getHudManager().render(context, parentScreen.width, parentScreen.height, true);
    }

    public boolean mouseClicked(float mouseX, float mouseY, int button) {

        if (button == 0) {
            for (int i = InertiaBase.instance.getHudManager().getGroups().size() - 1; i >= 0; i--) {
                HudGroup group = InertiaBase.instance.getHudManager().getGroups().get(i);
                if (group.isHovered(mouseX, mouseY)) {
                    dragStartX = mouseX - group.getX();
                    dragStartY = mouseY - group.getY();

                    int indexOfHoveredComponent = group.getHudComponentIndexAtY(mouseY - group.getY());
                    if (indexOfHoveredComponent != 0) {//seperate the groups
                        ArrayList<HudComponent> componentsToMove = new ArrayList<HudComponent>();
                        for (int i1 = indexOfHoveredComponent; i1 < group.getEnabledComponents().size(); i1++) {
                            componentsToMove.add(group.getEnabledComponents().get(i1));

                            //group.getComponents().remove(i1);
                            //i1--;
                        }
                        group.getComponents().removeAll(componentsToMove);

                        this.dragStartY -= group.getHeightForIndex(indexOfHoveredComponent);

                        HudGroup newGroup = new HudGroup();
                        //newGroup.copyPropertiesFrom(group);
                        //newGroup.isRendered = true;

                        for (HudComponent component : componentsToMove) {
                            newGroup.getComponents().add(component);
                            component.setGroup(newGroup);
                        }

                        InertiaBase.instance.getHudManager().getGroups().add(newGroup);
                        this.draggingGroup = newGroup;//no need to remove and readd it, it's already the newest
                        break;
                    }

                    draggingGroup = group;
                    //remove and pop to top
                    InertiaBase.instance.getHudManager().getGroups().remove(i);
                    InertiaBase.instance.getHudManager().getGroups().add(this.draggingGroup);
                }
            }
        }


        return false;
    }

    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        if (button == 0) {
            if (dragLinkedToComponent != null) {
                dragLinkedToComponent.addComponentsFromGroup(this.draggingGroup, this.dragLinkToGroupAtIndex);
                InertiaBase.instance.getFileManager().getHudQueuedSave().queue();
            }

            draggingGroup = null;
            dragLinkedToComponent = null;
            dragLinkToGroupAtIndex = -1;
        }
        return false;
    }

    /*public HudGroup getClosestGroup(float mouseX, float mouseY) {

        HudGroup closestComponent = null;
        float closestDistance = Float.MAX_VALUE;
        for (HudGroup group : InertiaClient.instance.getHudManager().getGroups()) {
            if (group == null || group == this.draggingGroup) {
                continue;
            }

            //double xDiff = this.draggingGroup.x + (this.draggingGroup.scaledWidth / 2) - (group.x + (group.scaledWidth / 2));
            //double yDiff = this.draggingGroup.y + (this.draggingGroup.scaledHeight / 2) - (group.y + (group.scaledHeight / 2));
            float xDiff = mouseX - (group.getX() + (group.getScaledWidth() / 2));
            float yDiff = mouseY - (group.getY() + (group.getScaledHeight() / 2));

            float distance = (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
            if (closestComponent == null) {
                closestComponent = group;
                closestDistance = distance;
                continue;
            }

            if (distance < closestDistance) {
                closestComponent = group;
                closestDistance = distance;
            }
        }

        return closestComponent;
    }*/
    public HudGroup getClosestGroup(float mouseX, float mouseY) {

        HudGroup closestComponent = null;
        float closestDistance = Float.MAX_VALUE;
        for (HudGroup group : InertiaBase.instance.getHudManager().getGroups()) {
            if (group == null || group == this.draggingGroup) {
                continue;
            }

            float xDiff = mouseX - (group.getX() + (group.getScaledWidth() / 2));
            float yDiffTop = mouseY - (group.getY());
            float yDiffBottom = mouseY - (group.getY() + (group.getScaledHeight()));

            float distanceToTop = (float) Math.sqrt(xDiff * xDiff + yDiffTop * yDiffTop);
            float distanceToBottom = (float) Math.sqrt(xDiff * xDiff + yDiffBottom * yDiffBottom);
            if (closestComponent == null) {
                closestComponent = group;
                closestDistance = Math.min(distanceToTop, distanceToBottom);
                continue;
            }

            if (distanceToTop < closestDistance || distanceToBottom < closestDistance) {
                closestComponent = group;
                closestDistance = Math.min(distanceToTop, distanceToBottom);
            }
        }

        return closestComponent;
    }

}
