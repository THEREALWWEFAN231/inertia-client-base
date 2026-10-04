package com.inertiaclient.base.gui.components.tabbedpage;

import com.inertiaclient.base.render.yoga.YogaNode;

public class WrappedListContainer extends AbstractWrappedListContainer<YogaNode> {

    @Override
    public YogaNode createListNodeType() {
        return new YogaNode();
    }
}
