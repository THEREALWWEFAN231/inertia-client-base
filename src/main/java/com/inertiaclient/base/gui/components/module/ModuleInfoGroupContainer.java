package com.inertiaclient.base.gui.components.module;

import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.gui.components.module.values._boolean.BooleanComponent;
import com.inertiaclient.base.gui.components.module.values.keybind.KeyBindComponent;
import com.inertiaclient.base.module.Module;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;

public class ModuleInfoGroupContainer extends AbstractGroupContainer {

    public ModuleInfoGroupContainer(Module module) {
        super(Component.translatable("icb.gui.text.module_category"), valuesContainer -> {
            valuesContainer.addChild(new KeyBindComponent(new KeyBindComponent.KeyWrapper() {
                @Override
                public InputConstants.Key get() {
                    return module.getBind();
                }

                @Override
                public void set(InputConstants.Key key) {
                    module.setBind(key);
                }
            }));
            YogaNode moduleEnabled = new YogaNode();
            new BooleanComponent(() -> "Enabled", module::isEnabled, module::setState, moduleEnabled);
            valuesContainer.addChild(moduleEnabled);
        });
    }

}
