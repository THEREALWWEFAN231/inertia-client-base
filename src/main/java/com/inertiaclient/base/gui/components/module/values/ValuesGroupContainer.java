package com.inertiaclient.base.gui.components.module.values;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.Page;
import com.inertiaclient.base.gui.components.module.values._boolean.ValueBooleanComponent;
import com.inertiaclient.base.gui.components.module.values.animation.AnimationComponent;
import com.inertiaclient.base.gui.components.module.values.blockentity.BlockEntityTypePage;
import com.inertiaclient.base.gui.components.module.values.blockentitycolor.BlockEntityColorsPage;
import com.inertiaclient.base.gui.components.module.values.blocks.BlocksValuePage;
import com.inertiaclient.base.gui.components.module.values.color.ColorValueComponent;
import com.inertiaclient.base.gui.components.module.values.entitycolor.EntityColorPage;
import com.inertiaclient.base.gui.components.module.values.entitytype.EntityTypePage;
import com.inertiaclient.base.gui.components.module.values.hashset.HashsetValueComponent;
import com.inertiaclient.base.gui.components.module.values.keybind.KeyBindComponent;
import com.inertiaclient.base.gui.components.module.values.number.FloatComponent;
import com.inertiaclient.base.gui.components.module.values.string.StringComponent;
import com.inertiaclient.base.value.Value;
import com.inertiaclient.base.value.group.ValueGroup;
import com.inertiaclient.base.value.impl.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class ValuesGroupContainer extends AbstractGroupContainer {

    private static final Text ENTITY_COLOR_LABEL = Text.translatable("icb.gui.pages.entity_color");

    private ValueGroup valueGroup;

    public ValuesGroupContainer(ValueGroup valueGroup) {
        super(valueGroup.getName(), valuesContainer -> {
            for (Value value : valueGroup.getValues()) {
                if (value instanceof BooleanValue bv) {
                    valuesContainer.addChild(new ValueBooleanComponent(bv));
                }
                if (value instanceof NumberValue nv) {
                    valuesContainer.addChild(new FloatComponent(nv));
                }
                if (value instanceof ModeValue sv) {
                    valuesContainer.addChild(new StringComponent(sv));
                }
                if (value instanceof EntityTypeValue etv) {
                    valuesContainer.addChild(new HashsetValueComponent(etv, () -> new EntityTypePage(etv)));
                }
                if (value instanceof EntityTypeColorValue etcv) {
                    valuesContainer.addChild(new SimpleSelectButton(etcv, null, () -> MainFrame.pageHolder.addPage(new Page(ENTITY_COLOR_LABEL, new EntityColorPage(etcv)))));
                }
                if (value instanceof BlocksValue bv) {
                    valuesContainer.addChild(new HashsetValueComponent(bv, () -> new BlocksValuePage(bv)));
                }
                if (value instanceof ColorValue cv) {
                    valuesContainer.addChild(new ColorValueComponent(cv));
                }
                if (value instanceof AnimationValue av) {
                    valuesContainer.addChild(new AnimationComponent(av));
                }
                if (value instanceof BlockEntityValue bev) {
                    valuesContainer.addChild(new HashsetValueComponent(bev, () -> new BlockEntityTypePage(bev)));
                }
                if (value instanceof BlockEntityColorValue becv) {
                    valuesContainer.addChild(new SimpleSelectButton(becv, null, () -> MainFrame.pageHolder.addPage(new Page(becv.getName(), new BlockEntityColorsPage(becv)))));
                }
                if (value instanceof KeybindValue kbv) {
                    valuesContainer.addChild(new KeyBindComponent(new KeyBindComponent.KeyWrapper() {
                        @Override
                        public InputUtil.Key get() {
                            return kbv.getValue();
                        }

                        @Override
                        public void set(InputUtil.Key key) {
                            kbv.setValue(key);
                        }
                    }));
                }
            }
        });
        this.valueGroup = valueGroup;
    }

    protected boolean implIsVisible() {
        return this.valueGroup.isVisible();
    }

}
