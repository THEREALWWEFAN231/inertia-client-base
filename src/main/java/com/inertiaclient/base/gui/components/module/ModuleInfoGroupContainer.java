package com.inertiaclient.base.gui.components.module;

import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.gui.components.module.values._boolean.BooleanComponent;
import com.inertiaclient.base.gui.components.module.values.keybind.KeyBindComponent;
import com.inertiaclient.base.module.Module;
import com.inertiaclient.base.render.yoga.YogaNode;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class ModuleInfoGroupContainer extends AbstractGroupContainer {

    public ModuleInfoGroupContainer(Module module) {
        super(Text.translatable("icb.gui.text.module_category"), valuesContainer -> {
            valuesContainer.addChild(new KeyBindComponent(new KeyBindComponent.KeyWrapper() {
                @Override
                public InputUtil.Key get() {
                    return module.getBind();
                }

                @Override
                public void set(InputUtil.Key key) {
                    module.setBind(key);
                }
            }));
            YogaNode moduleEnabled = new YogaNode();
            new BooleanComponent(() -> "Enabled", module::isEnabled, module::setState, moduleEnabled);
            valuesContainer.addChild(moduleEnabled);
        });
    }

}
