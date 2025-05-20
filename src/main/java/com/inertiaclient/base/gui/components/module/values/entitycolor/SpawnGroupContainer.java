package com.inertiaclient.base.gui.components.module.values.entitycolor;

import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;

public class SpawnGroupContainer extends YogaNode {

    private EntityColorPage entityColorPage;

    public SpawnGroupContainer(EntityColorPage entityColorPage) {
        this.entityColorPage = entityColorPage;

        this.styleSetFlexDirection(FlexDirection.ROW);
        this.styleSetFlexWrap(FlexWrap.WRAP);
        this.styleSetJustifyContent(JustifyContent.SPACE_AROUND);
        this.styleSetGap(GapGutter.ROW, 5);
        this.styleSetFlexShrink(0);
        this.styleSetFlexGrow(0);
    }

    public void changeSpawnGroup(SpawnGroup spawnGroup) {
        this.removeAllChildren();

        if (spawnGroup == null) {//all
            for (EntityType entityType : Registries.ENTITY_TYPE.stream().toList()) {
                this.addChild(new EntityTypeColorComponent(entityColorPage.getEntityTypeColorValue(), entityType));
            }
            return;
        }

        for (EntityType entityType : Registries.ENTITY_TYPE.stream().toList()) {
            if (entityType.getSpawnGroup() != spawnGroup) {
                continue;
            }
            this.addChild(new EntityTypeColorComponent(entityColorPage.getEntityTypeColorValue(), entityType));
        }
    }


}