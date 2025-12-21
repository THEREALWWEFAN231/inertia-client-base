package com.inertiaclient.base.hud;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.render.skia.SkiaOpenGLInstance;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class HudManager {

    @Getter
    private ArrayList<HudComponent> components = new ArrayList<>();
    @Getter
    private ArrayList<HudGroup> groups = new ArrayList<>();
    private SkiaOpenGLInstance skiaInstance;
    @Nullable
    @Setter
    @Getter
    private HudComponent highlightedComponent;

    @Getter
    private HudGroup topLeft;
    @Getter
    private HudGroup topRight;
    @Getter
    private HudGroup bottomLeft;
    @Getter
    private HudGroup bottomRight;
    @Getter
    private HudGroup topCenter;

    public HudManager() {
        this.topLeft = this.createTopLeft();
        this.topRight = this.createTopRight();
        this.bottomLeft = this.createBottomLeft();
        this.bottomRight = this.createBottomRight();
        this.topCenter = this.createTopCenter();

        InertiaBase.instance.getModLoader().getMods().forEach(inertiaMod -> {
            ArrayList<HudComponent> modsComponents = new ArrayList<>();
            ArrayList<HudGroup> modsGroups = new ArrayList<>();

            inertiaMod.initializeHudComponents(modsComponents, modsGroups, this);

            this.groups.addAll(modsGroups);
            this.components.addAll(modsComponents);
        });


        //purge created groups with no components
        for (int i = 0; i < this.groups.size(); i++) {
            HudGroup hudGroup = this.groups.get(i);

            if (hudGroup.getComponents().isEmpty()) {
                this.groups.remove(i);
                i--;
            }
        }

        for (HudComponent hudComponent : this.components) {
            if (hudComponent.getGroup() == null) {
                HudGroup independentGroup = new HudGroup();
                this.groups.add(independentGroup);

                independentGroup.addComponent(hudComponent);
            }
        }

    }

    private HudGroup createTopLeft() {
        HudGroup group = new HudGroup();
        this.groups.add(group);
        return group;
    }

    private HudGroup createTopRight() {
        HudGroup group = new HudGroup();
        this.groups.add(group);
        group.setRightAlignmentPercentage(1);
        group.setComponentsXAlignmentState(HudGroup.XAlignment.RIGHT);
        return group;
    }

    private HudGroup createBottomLeft() {
        HudGroup group = new HudGroup();
        this.groups.add(group);
        group.setBottomAlignmentPercentage(1);
        group.setComponentsYAlignmentState(HudGroup.YAlignment.BOTTOM);
        return group;
    }

    private HudGroup createBottomRight() {
        HudGroup group = new HudGroup();
        this.groups.add(group);
        group.setRightAlignmentPercentage(1);
        group.setComponentsXAlignmentState(HudGroup.XAlignment.RIGHT);
        group.setBottomAlignmentPercentage(1);
        group.setComponentsYAlignmentState(HudGroup.YAlignment.BOTTOM);
        return group;
    }

    private HudGroup createTopCenter() {
        HudGroup group = new HudGroup();
        this.groups.add(group);
        group.setMiddleXAlignmentPercentage(.5f);
        group.setComponentsXAlignmentState(HudGroup.XAlignment.MIDDLE);
        group.setTopAlignmentPercentage(.15f);
        group.setComponentsYAlignmentState(HudGroup.YAlignment.TOP);
        return group;
    }

    public void beforeRender(SkiaOpenGLInstance skiaInstance, boolean editor) {
        this.skiaInstance = skiaInstance;
        for (HudGroup hudGroup : this.groups) {
            hudGroup.doComponentsBeforeRender(0, 0, 0, editor, this.skiaInstance.getCanvasWrapper());
        }
    }

    public void render(GuiGraphics drawContext, float screenWidth, float screenHeight, boolean editor) {
        Runnable draw = () -> {
            for (HudGroup hudGroup : this.groups) {
                hudGroup.doStuff(0, 0, 0);
            }

            for (HudGroup hudGroup : this.groups) {
                if (hudGroup.getComponentsXAlignmentState() == HudGroup.XAlignment.LEFT) {
                    hudGroup.setX(hudGroup.getLeftAlignmentPercentage() * screenWidth);
                } else if (hudGroup.getComponentsXAlignmentState() == HudGroup.XAlignment.MIDDLE) {
                    hudGroup.setX((hudGroup.getMiddleXAlignmentPercentage() * screenWidth) - (hudGroup.getScaledWidth() / 2f));
                } else if (hudGroup.getComponentsXAlignmentState() == HudGroup.XAlignment.RIGHT) {
                    hudGroup.setX((hudGroup.getRightAlignmentPercentage() * screenWidth) - hudGroup.getScaledWidth());
                }

                if (hudGroup.getComponentsYAlignmentState() == HudGroup.YAlignment.TOP) {
                    hudGroup.setY(hudGroup.getTopAlignmentPercentage() * screenHeight);
                } else if (hudGroup.getComponentsYAlignmentState() == HudGroup.YAlignment.MIDDLE) {
                    hudGroup.setY((hudGroup.getMiddleYAlignmentPercentage() * screenHeight) - (hudGroup.getScaledHeight() / 2f));
                } else if (hudGroup.getComponentsYAlignmentState() == HudGroup.YAlignment.BOTTOM) {
                    hudGroup.setY((hudGroup.getBottomAlignmentPercentage() * screenHeight) - hudGroup.getScaledHeight());
                }
            }

            this.skiaInstance.getCanvas().save();
            for (HudGroup hudGroup : this.groups) {
                hudGroup.renderGroup(drawContext, 0, 0, 0, editor, this.skiaInstance.getCanvasWrapper());
            }
            this.skiaInstance.getCanvas().restore();
        };
        if (editor) {
            draw.run();
        } else {
            skiaInstance.setup(draw);
        }

    }

}
