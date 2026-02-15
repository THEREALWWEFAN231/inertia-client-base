package com.inertiaclient.base.gui.components.module.values.entitycolor;

import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.registries.BuiltInRegistries;

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

    public void changeSpawnGroup(MobCategory spawnGroup) {
        this.removeAllChildren();

        if (spawnGroup == null) {//all
            for (EntityType entityType : BuiltInRegistries.ENTITY_TYPE.stream().toList()) {
                this.addChild(new EntityTypeColorComponent(entityColorPage.getEntityTypeColorValue(), entityType));
            }
            return;
        }

        for (EntityType entityType : BuiltInRegistries.ENTITY_TYPE.stream().toList()) {
            if (entityType.getCategory() != spawnGroup) {
                continue;
            }
            this.addChild(new EntityTypeColorComponent(entityColorPage.getEntityTypeColorValue(), entityType));
        }
    }


}