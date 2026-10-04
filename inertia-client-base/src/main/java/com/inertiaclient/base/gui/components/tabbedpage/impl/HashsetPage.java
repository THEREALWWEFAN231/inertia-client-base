package com.inertiaclient.base.gui.components.tabbedpage.impl;

import com.inertiaclient.base.gui.components.tabbedpage.*;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.value.HashsetValue;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
public class HashsetPage<T> extends TabbedPage {

    private HashsetValue<T> hashsetValue;
    private Function<T, YogaNode> createComponentForEntryFunction;

    public ArrayList<Tab> createTabs() {
        var tabs = new ArrayList<Tab>();

        Tab<AbstractWrappedListContainer<?>> add = new Tab<>(TabbedPage.getTextForPage("hashset", "add"), this.createTabWrappedContainer());
        Tab<AbstractWrappedListContainer<?>> remove = new Tab<>(TabbedPage.getTextForPage("hashset", "remove"), this.createTabWrappedContainer());
        tabs.add(add);
        tabs.add(remove);
        
        for (T entry : hashsetValue.getAllPossibleEntries()) {
            YogaNode component = createComponentForEntryFunction.apply(entry);
            component.setHoverCursorToIndicateClick();
            component.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
                if (button == ButtonIdentifier.LEFT) {

                    int addIndex = 0;
                    int removeIndex = 0;
                    List<T> entriesBeforeThis = hashsetValue.getAllPossibleEntriesStream().takeWhile(entry1 -> !entry1.equals(entry)).toList();
                    for (T entry1 : entriesBeforeThis) {
                        if (hashsetValue.getValue().contains(entry1)) {
                            removeIndex++;
                        } else {
                            addIndex++;
                        }
                    }

                    if (component.getParent() == add.getYogaNode().getListNode()) {
                        hashsetValue.addHard(entry);
                        add.getYogaNode().getListNode().removeChild(component);
                        remove.getYogaNode().getListNode().insertChild(component, removeIndex);
                        this.onModified(ModificationType.ADD, add);
                    } else {
                        hashsetValue.removeHard(entry);
                        remove.getYogaNode().getListNode().removeChild(component);
                        add.getYogaNode().getListNode().insertChild(component, addIndex);
                        this.onModified(ModificationType.REMOVE, remove);
                    }
                    return true;
                }
                return false;
            });

            if (hashsetValue.getValue().contains(entry)) {
                remove.getYogaNode().getListNode().addChild(component);
            } else {
                add.getYogaNode().getListNode().addChild(component);
            }
        }
        return tabs;
    }

    public AbstractWrappedListContainer<?> createTabWrappedContainer() {
        return new WrappedListContainer();
    }

    public void onModified(ModificationType modificationType, Tab<AbstractWrappedListContainer<?>> tab) {

    }

    public enum ModificationType {
        ADD, REMOVE;
    }

}