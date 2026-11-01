package com.inertiaclient.base.hud;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.mods.InertiaMod;
import com.inertiaclient.base.render.skia.instances.SkiaInstance;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class HudManager {

    @Getter
    private ArrayList<HudComponent> components = new ArrayList<>();
    @Getter
    private final HashMap<String, HudComponent> idCachedComponentsMap = new HashMap<>();
    @Getter
    private HashMap<InertiaMod, ArrayList<HudComponent>> componentsByMods = new HashMap<InertiaMod, ArrayList<HudComponent>>();
    @Getter
    private ArrayList<HudGroup> groups = new ArrayList<>();
    private SkiaInstance skiaInstance;
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

            modsComponents.forEach(hudComponent -> {
                hudComponent.setMod(inertiaMod);

                if (idCachedComponentsMap.containsKey(hudComponent.getId())) {
                    HudComponent alreadyRegistered = idCachedComponentsMap.get(hudComponent.getId());
                    hudComponent.setId(inertiaMod.getId() + "_" + hudComponent.getId());

                    InertiaBase.LOGGER.warn("Hud Component with id {} was already registered by {}, prefixing one of them with their mod id", alreadyRegistered.getId(), alreadyRegistered.getMod().getName());
                }
            });

            this.groups.addAll(modsGroups);
            this.components.addAll(modsComponents);
            this.componentsByMods.put(inertiaMod, modsComponents);
            modsComponents.forEach(hudComponent -> this.idCachedComponentsMap.put(hudComponent.getId(), hudComponent));
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

    public void beforeRender(SkiaInstance skiaInstance, boolean editor) {
        this.skiaInstance = skiaInstance;
        for (HudGroup hudGroup : this.groups) {
            hudGroup.doComponentsBeforeRender(0, 0, 0, editor, this.skiaInstance.getCanvasWrapper());
        }
    }

    public void renderGroups(GuiGraphicsExtractor graphics, float screenWidth, float screenHeight, boolean editor) {
        for (HudGroup hudGroup : this.groups) {
            hudGroup.prepareForRender(0, 0, 0, editor);
        }

        for (HudGroup hudGroup : this.groups) {
            hudGroup.setPositionBasedOfScreen(screenWidth, screenHeight);
        }

        this.skiaInstance.getCanvas().save();
        for (HudGroup hudGroup : this.groups) {
            hudGroup.renderGroup(graphics, 0, 0, 0, editor, this.skiaInstance.getCanvasWrapper());
        }
        this.skiaInstance.getCanvas().restore();

    }

}
