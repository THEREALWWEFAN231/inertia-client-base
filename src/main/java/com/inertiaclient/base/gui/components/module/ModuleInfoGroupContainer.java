package com.inertiaclient.base.gui.components.module;

import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.gui.components.module.values._boolean.BooleanComponent;
import com.inertiaclient.base.gui.components.module.values.keybind.KeyBindComponent;
import com.inertiaclient.base.module.Module;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ModuleInfoGroupContainer extends AbstractGroupContainer {

    private static final MutableComponent enabled = Component.translatable("icb.gui.pages.modules.module.enabled");

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
            new BooleanComponent(enabled::getString, module::isEnabled, module::setState, moduleEnabled);
            valuesContainer.addChild(moduleEnabled);
        });
    }

}
