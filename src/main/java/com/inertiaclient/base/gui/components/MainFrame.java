package com.inertiaclient.base.gui.components;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.components.leftpanel.LeftPanel;
import com.inertiaclient.base.gui.components.toppanel.TopPanel;
import com.inertiaclient.base.render.animation.AnimationValue;
import com.inertiaclient.base.render.yoga.GenericStyle;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.*;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.function.Supplier;

//https://yogalayout.com/playground?eyJ3aWR0aCI6NTAwLCJoZWlnaHQiOjUwMCwibWluV2lkdGgiOm51bGwsIm1pbkhlaWdodCI6bnVsbCwibWF4V2lkdGgiOm51bGwsIm1heEhlaWdodCI6bnVsbCwicG9zaXRpb24iOnsidG9wIjpudWxsLCJyaWdodCI6bnVsbCwiYm90dG9tIjpudWxsLCJsZWZ0IjpudWxsfSwiY2hpbGRyZW4iOlt7IndpZHRoIjoiNDAwIiwiaGVpZ2h0IjoiNDAwIiwibWluV2lkdGgiOm51bGwsIm1pbkhlaWdodCI6bnVsbCwibWF4V2lkdGgiOm51bGwsIm1heEhlaWdodCI6bnVsbCwicG9zaXRpb24iOnsidG9wIjpudWxsLCJyaWdodCI6bnVsbCwiYm90dG9tIjpudWxsLCJsZWZ0IjpudWxsfSwiY2hpbGRyZW4iOlt7IndpZHRoIjoiMzMlIiwiaGVpZ2h0IjoiMTAwJSIsIm1pbldpZHRoIjpudWxsLCJtaW5IZWlnaHQiOm51bGwsIm1heFdpZHRoIjpudWxsLCJtYXhIZWlnaHQiOm51bGwsInBvc2l0aW9uIjp7InRvcCI6bnVsbCwicmlnaHQiOm51bGwsImJvdHRvbSI6bnVsbCwibGVmdCI6bnVsbH0sImZsZXhHcm93IjoiMCJ9LHsid2lkdGgiOiIiLCJoZWlnaHQiOiIxMDAlIiwibWluV2lkdGgiOm51bGwsIm1pbkhlaWdodCI6bnVsbCwibWF4V2lkdGgiOm51bGwsIm1heEhlaWdodCI6bnVsbCwiZmxleERpcmVjdGlvbiI6MCwicG9zaXRpb24iOnsidG9wIjpudWxsLCJyaWdodCI6bnVsbCwiYm90dG9tIjpudWxsLCJsZWZ0IjpudWxsfSwiZmxleEdyb3ciOiIxIiwiY2hpbGRyZW4iOlt7IndpZHRoIjoiMTAwJSIsImhlaWdodCI6IiIsIm1pbldpZHRoIjpudWxsLCJtaW5IZWlnaHQiOiIzMHB4IiwibWF4V2lkdGgiOm51bGwsIm1heEhlaWdodCI6bnVsbCwicG9zaXRpb24iOnsidG9wIjpudWxsLCJyaWdodCI6bnVsbCwiYm90dG9tIjpudWxsLCJsZWZ0IjpudWxsfX0seyJ3aWR0aCI6IjEwMCUiLCJoZWlnaHQiOiIiLCJtaW5XaWR0aCI6bnVsbCwibWluSGVpZ2h0IjpudWxsLCJtYXhXaWR0aCI6bnVsbCwibWF4SGVpZ2h0IjpudWxsLCJwb3NpdGlvbiI6eyJ0b3AiOm51bGwsInJpZ2h0IjpudWxsLCJib3R0b20iOm51bGwsImxlZnQiOm51bGx9LCJmbGV4R3JvdyI6IjEifV19XX1dfQ==
public class MainFrame extends YogaNode {

    private static Color frameBackgroundColor = new Color(20, 20, 20, (int) (255 * .98f));
    private static Color lineColor = new Color(81, 81, 81);
    private static float lineWidth = .5f;
    private static Color unselectedTextColor = new Color(212, 212, 212);
    private static Color selectedTextColor = Color.white;
    private static Color funColor = new Color(100, 20, 20);//blue 140
    private static float frameBorderRadius = 5;
    private static Color selectorButtonSelectedColor = new Color(27, 27, 27);
    private static Color selectorButtonColor = new Color(38, 38, 38, (int) (255 * .98f));
    private static Color stringValuesBackgroundColor = new Color(59, 59, 59, (int) (255 * .52f));

    public static Supplier<Float> s_frameBorderRadius = () -> InertiaBase.instance.getSettings().getClickGuiSettings().getBorderRadius().getValue();
    public static Supplier<Color> s_frameBackgroundColor = () -> InertiaBase.instance.getSettings().getClickGuiSettings().getBackgroundColor().getValue().getRenderColor();
    public static Supplier<Color> s_unselectedTextColor = () -> InertiaBase.instance.getSettings().getClickGuiSettings().getUnselectedTextColor().getValue().getRenderColor();
    public static Supplier<Color> s_selectedTextColor = () -> InertiaBase.instance.getSettings().getClickGuiSettings().getSelectedTextColor().getValue().getRenderColor();
    public static Supplier<Color> s_funColor = () -> InertiaBase.instance.getSettings().getClickGuiSettings().getPrimaryColor().getValue().getRenderColor();
    public static Supplier<Color> s_selectorButtonSelectedColor = () -> InertiaBase.instance.getSettings().getClickGuiSettings().getSelectorButtonSelectedColor().getValue().getRenderColor();
    public static Supplier<Color> s_selectorButtonColor = () -> InertiaBase.instance.getSettings().getClickGuiSettings().getSelectorButtonColor().getValue().getRenderColor();
    public static Supplier<Color> s_selectorButtonOutlineColor = () -> InertiaBase.instance.getSettings().getClickGuiSettings().getSelectorButtonoutlineColor().getValue().getRenderColor();
    public static Supplier<Color> s_stringValuesBackgroundColor = () -> InertiaBase.instance.getSettings().getClickGuiSettings().getStringValuesBackgroundColor().getValue().getRenderColor();
    public static Supplier<Color> s_lineColor = () -> InertiaBase.instance.getSettings().getClickGuiSettings().getLineColor().getValue().getRenderColor();
    public static Supplier<Float> s_lineWidth = () -> InertiaBase.instance.getSettings().getClickGuiSettings().getLineWidth().getValue();

    public static TopPanel topPanel;
    public static LeftPanel leftPanel;
    public static PageHolder pageHolder;

    @Setter
    private boolean isBeingDragged;
    private final float[] dragStart = new float[2];
    private float frameX = 100;
    private float frameY = 70;

    @Getter
    private AnimationValue animationValue = new AnimationValue();

    public MainFrame() {
        this.styleSetPositionType(PositionType.ABSOLUTE);
        this.styleSetPosition(YogaEdge.LEFT, frameX);
        this.styleSetPosition(YogaEdge.TOP, frameY);
        this.styleSetWidth(40, ExactPercentAuto.PERCENTAGE);
        this.styleSetHeight(65, ExactPercentAuto.PERCENTAGE);
        //easeOutBounce, easeOutQuad
        var clickGuiSettings = InertiaBase.instance.getSettings().getClickGuiSettings();

        this.setPreLayoutCalculationsCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            if (this.isBeingDragged) {
                //only allow in positions, it looks bad otherwise
                frameX = (int) (globalMouseX - this.dragStart[0]);
                frameY = (int) (globalMouseY - this.dragStart[1]);

                if (this.frameX < 0) {
                    this.frameX = 0;
                }
                if (this.frameX + this.getWidth() > this.getParent().getWidth()) {
                    this.frameX = this.getParent().getWidth() - this.getWidth();
                }

                if (this.frameY < 0) {
                    this.frameY = 0;
                }
                if (this.frameY + this.getHeight() > this.getParent().getHeight()) {
                    this.frameY = this.getParent().getHeight() - this.getHeight();
                }

                this.styleSetPosition(YogaEdge.LEFT, frameX);
                this.styleSetPosition(YogaEdge.TOP, frameY);
            }
        }).setPreRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            float animationProgress = animationValue.getValue();
            if (clickGuiSettings.getScaleAnimation().getValue()) {
                canvas.translate(getWidth() / 2, getHeight() / 2);
                canvas.scale(animationProgress, animationProgress);
                canvas.translate(getWidth() / 2 * -1, getHeight() / 2 * -1);
            }

            if (clickGuiSettings.getOpacityAnimation().getValue()) {
                canvas.saveLayerAlpha(0, 0, this.getWidth(), this.getHeight(), MathHelper.clamp((int) (animationProgress * 255), 0, 255));
            }

            if (clickGuiSettings.getTranslateAnimation().getValue()) {
                canvas.translate(0, getHeight() - (getHeight() * animationProgress));
            }

        }).setKeyPressedCallback((keyCode, scanCode, modifiers) -> {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                InertiaBase.mc.setScreen(null);
                return true;
            }
            return false;
        }).applyGenericStyle(new GenericStyle().setBorderRadius(MainFrame.s_frameBorderRadius).setBackgroundColor(MainFrame.s_frameBackgroundColor).setStroke(s_lineColor, s_lineWidth));

        this.addChild(leftPanel = new LeftPanel());

        YogaNode restOfContainer = new YogaNode();
        this.addChild(restOfContainer);


        restOfContainer.styleSetFlexDirection(FlexDirection.COLUMN);
        restOfContainer.styleSetFlexGrow(1);
        restOfContainer.styleSetWidth(100, ExactPercentAuto.PERCENTAGE);

        restOfContainer.addChild(topPanel = new TopPanel(this));
        restOfContainer.addChild(pageHolder = new PageHolder());

        //default select 0, modules
        leftPanel.pages.selectIndex(0);
    }

    public void setDragPoints(float x, float y) {
        this.isBeingDragged = true;
        this.dragStart[0] = x;
        this.dragStart[1] = y;
    }

}
