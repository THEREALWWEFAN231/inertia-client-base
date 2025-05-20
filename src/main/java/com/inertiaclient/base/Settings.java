package com.inertiaclient.base;

import com.inertiaclient.base.render.animation.Animations;
import com.inertiaclient.base.utils.CursorUtils;
import com.inertiaclient.base.utils.InputUtils;
import com.inertiaclient.base.value.group.ButtonValueGroup;
import com.inertiaclient.base.value.group.ValueGroup;
import com.inertiaclient.base.value.impl.*;
import lombok.Getter;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.ArrayList;

public class Settings {

    @Getter
    private ArrayList<ValueGroup> allGroups = new ArrayList<>();

    private ValueGroup main;
    @Getter
    private ModeValue clientRotationDisplay;

    private ButtonValueGroup clickGuiButtonGroup;
    @Getter
    private ClickGuiSettings clickGuiSettings;

    private ButtonValueGroup tpsButtonGroup;
    @Getter
    private TPSSettings tpsSettings;


    public Settings() {
        this.addGroup(main = new ValueGroup("main", ""));

        this.clientRotationDisplay = new ModeValue("client_rotation_display", this.main, ClientRotationDisplay.WHOLE_BODY, ClientRotationDisplay.class);

        this.addGroup(this.clickGuiButtonGroup = new ButtonValueGroup("click_gui", ""));
        this.clickGuiSettings = new ClickGuiSettings(this.clickGuiButtonGroup);

        this.addGroup(this.tpsButtonGroup = new ButtonValueGroup("tps", ""));
        this.tpsSettings = new TPSSettings(this.tpsButtonGroup);
    }

    private void addGroup(ValueGroup valueGroup) {
        this.allGroups.add(valueGroup);
    }

    public static class ClientRotationDisplay {

        public static ModeValue.Mode NONE = new ModeValue.Mode("none");
        public static ModeValue.Mode HEAD = new ModeValue.Mode("head");
        public static ModeValue.Mode WHOLE_BODY = new ModeValue.Mode("whole_body");
    }

    @Getter
    public class ClickGuiSettings {

        private BooleanValue customCursors;
        private BooleanValue scaleAnimation;
        private BooleanValue opacityAnimation;
        private BooleanValue translateAnimation;
        private AnimationValue animation;
        private FloatValue animationDuration;
        private KeybindValue keybind;

        private ColorValue backgroundColor;
        private ColorValue lineColor;
        private ColorValue unselectedTextColor;
        private ColorValue selectedTextColor;
        private ColorValue primaryColor;
        private ColorValue selectorButtonSelectedColor;
        private ColorValue selectorButtonColor;
        private ColorValue selectorButtonoutlineColor;
        private ColorValue stringValuesBackgroundColor;


        private FloatValue borderRadius;
        private FloatValue lineWidth;

        public ClickGuiSettings(ValueGroup valueGroup) {
            this.customCursors = new BooleanValue("custon_cursors", valueGroup, true);
            this.scaleAnimation = new BooleanValue("scale", valueGroup, true);
            this.opacityAnimation = new BooleanValue("opacity", valueGroup, false);
            this.translateAnimation = new BooleanValue("translate", valueGroup, false);
            this.animation = new AnimationValue("animation", valueGroup, Animations.easeOutQuart);
            this.animationDuration = new FloatValue("duration", valueGroup, .75f, 0.05f, 3);
            this.keybind = new KeybindValue("keybind", valueGroup, InputUtils.fromKeyCode(GLFW.GLFW_KEY_RIGHT_SHIFT));

            this.backgroundColor = new ColorValue("background_color", valueGroup, new Color(20, 20, 20, (int) (255 * .98f)));
            this.lineColor = new ColorValue("line_color", valueGroup, new Color(81, 81, 81));
            this.unselectedTextColor = new ColorValue("unselected_text_color", valueGroup, new Color(212, 212, 212));
            this.selectedTextColor = new ColorValue("selected_text_color", valueGroup, Color.white);
            this.primaryColor = new ColorValue("primary_color", valueGroup, new Color(100, 20, 20));
            this.selectorButtonSelectedColor = new ColorValue("selector_button_selected_color", valueGroup, new Color(27, 27, 27));
            this.selectorButtonColor = new ColorValue("selector_button_color", valueGroup, new Color(38, 38, 38, (int) (255 * .98f)));
            this.selectorButtonoutlineColor = new ColorValue("selector_button_outline", valueGroup, Color.black);
            this.stringValuesBackgroundColor = new ColorValue("string_values_background_Color", valueGroup, new Color(59, 59, 59, (int) (255 * .52f)));

            this.borderRadius = new FloatValue("border_radius", valueGroup, 5, 0, 20);
            this.lineWidth = new FloatValue("line_width", valueGroup, .5f, 0, 3);

            this.customCursors.setChangeListener((oldValue, newValue) -> {
                if (!newValue) {
                    CursorUtils.setCursor(CursorUtils.Cursor.ARROW);
                }
            });
        }
    }

    public class TPSSettings {

        @Getter
        private BooleanValue active;
        private IntegerValue samples;

        public TPSSettings(ValueGroup valueGroup) {
            this.active = new BooleanValue("active", valueGroup, false);
            this.samples = new IntegerValue("samples", valueGroup, 5, 1, 20);

            this.active.setChangeListener((oldValue, newValue) -> {
                InertiaBase.instance.getTickRateCalculator().setTickRateActive(newValue);
            });
            this.samples.setChangeListener((oldValue, newValue) -> {
                InertiaBase.instance.getTickRateCalculator().changeNumberOfSamples(newValue);
            });

            this.samples.setVisibleWhen(() -> !this.active.getValue());
        }

    }

}
