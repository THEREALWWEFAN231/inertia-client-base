package com.inertiaclient.base.gui.components.module.values.entitytype;

import com.inertiaclient.base.gui.components.tabbedpage.Tab;
import com.inertiaclient.base.gui.components.tabbedpage.TabbedPage;
import com.inertiaclient.base.gui.components.tabbedpage.WrappedListContainer;
import com.inertiaclient.base.value.impl.EntityTypeValue;
import lombok.AllArgsConstructor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

@AllArgsConstructor
public class EntityTypePage extends TabbedPage {

    private EntityTypeValue entityTypeValue;

    public ArrayList<Tab> createTabs() {
        var tabs = new ArrayList<Tab>();

        WrappedListContainer allWrappedListContainer = new WrappedListContainer();
        //allWrappedListContainer.getListNode().styleSetJustifyContent(Yoga.YGJustifySpaceAround);
        Tab<WrappedListContainer> all = new Tab(TabbedPage.getTextForPage("entitytype", "all"), allWrappedListContainer);
        tabs.add(all);

        for (EntityType entityType : BuiltInRegistries.ENTITY_TYPE.stream().toList()) {
            all.getYogaNode().getListNode().addChild(new EntityTypeComponent(entityTypeValue, entityType));
        }

        for (MobCategory spawnGroup : MobCategory.values()) {
            SpawnGroupContainer wrappedListContainer = new SpawnGroupContainer(entityTypeValue, spawnGroup);
            Tab<SpawnGroupContainer> tab = new Tab(Component.literal(spawnGroup.getName()), wrappedListContainer);
            tabs.add(tab);
        }

        /*for (EntityType entityType : Registries.ENTITY_TYPE.stream().toList()) {
            Tab<TestContainer> tab = tabs.get(1 + entityType.getSpawnGroup().ordinal());//1 for all
            tab.getYogaNode().getWrappedListContainer().getListNode().addChild(new EntityTypeComponent(entityTypeValue, entityType));
        }*/


        return tabs;
    }
}
