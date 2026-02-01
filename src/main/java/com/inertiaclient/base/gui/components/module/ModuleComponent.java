package com.inertiaclient.base.gui.components.module;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.Page;
import com.inertiaclient.base.gui.components.SvgComponent;
import com.inertiaclient.base.gui.components.helpers.ValuesPage;
import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.module.Module;
import com.inertiaclient.base.render.animation.AnimationValue;
import com.inertiaclient.base.render.animation.Animations;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.skia.SvgRenderer;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.*;
import com.inertiaclient.base.utils.UIUtils;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.RRect;

import java.awt.Color;

public class ModuleComponent extends YogaNode {

    private AnimationValue toggleAnimation;
    private AnimationValue descriptionAnimation;

    private boolean oldModuleState;
    public static final Color BACKGROUND_COLOR = new Color(0, 0, 0, (int) (255 * .24f));
    public static Color switchBackgroundColor = new Color(104, 104, 104);
    private static final SvgRenderer starStroke = new SvgRenderer("icb/textures/star-stroke.svg");
    private static final SvgRenderer starFill = new SvgRenderer("icb/textures/star-fill.svg");

    public ModuleComponent(Module module, ModulesPage.FavoritesList favoritesList) {
        this.oldModuleState = module.isEnabled();
        this.toggleAnimation = new AnimationValue();
        this.descriptionAnimation = new AnimationValue();
        this.toggleAnimation.setValue(module.isEnabled() ? 1 : 0);

        this.setSearchContext(module.getNameString());
        this.styleSetMinHeight(30);

        this.setHoverCallback(hovered -> {
            AnimationValue.tweenEngine.cancelTarget(descriptionAnimation);
            descriptionAnimation.to(.15f).value(hovered ? 1 : 0).start();
        });


        this.styleSetAlignItems(AlignItems.CENTER);
        this.styleSetJustifyContent(JustifyContent.FLEX_END);
        this.styleSetGap(GapGutter.COLUMN, 5);
        this.styleSetBorder(YogaEdge.ALL, 7);

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), 5, BACKGROUND_COLOR);

            boolean showDescription = descriptionAnimation.getValue() != 0;

            var nameTextBuilder = CanvasWrapper.getFreshTextBuilder();
            nameTextBuilder.basic(module.getNameString(), 7, this.getHeight() / 2 - (showDescription ? 2 * descriptionAnimation.getValue() : 0));
            nameTextBuilder.setFontSize(14);
            nameTextBuilder.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE);
            nameTextBuilder.draw(canvas);

            if (showDescription) {
                var descriptionTextBuilder = CanvasWrapper.getFreshTextBuilder();
                descriptionTextBuilder.basic(module.getDescriptionString(), 7, 20, UIUtils.colorWithAlpha(Color.white, descriptionAnimation.getValue()));
                descriptionTextBuilder.setFontSize(AbstractGroupContainer.valuesFontSize);
                descriptionTextBuilder.draw(canvas);
            }
        });

        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                module.toggle();
                return true;
            }
            if (button == ButtonIdentifier.RIGHT) {
                this.openSettings(module);
                return true;
            }

            return false;
        });

        YogaNode favoriteComponent = new YogaNode();
        this.addChild(favoriteComponent);
        favoriteComponent.styleSetWidth(12);
        favoriteComponent.styleSetHeight(12);
        favoriteComponent.setHoverCursorToIndicateClick();
        favoriteComponent.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            try (var paint = SkiaUtils.createPaintForColor(module.isFavorite() ? Color.orange : Color.white); var blurPaint = SkiaUtils.createPaintForColor(module.isFavorite() ? Color.orange : Color.white)) {
                this.renderStar(canvas, module, favoriteComponent, paint);

                if (favoriteComponent.shouldShowHoveredEffects()) {
                    float blurRadius = module.isFavorite() ? 2 : 1;
                    SkiaUtils.setPaintBlur(blurPaint, blurRadius);
                    this.renderStar(canvas, module, favoriteComponent, blurPaint);
                }
            }
        });
        favoriteComponent.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            module.setFavorite(!module.isFavorite());
            favoritesList.refresh();
            return true;
        });

        SvgComponent settingComponent = new SvgComponent("icb/textures/setting-gear.svg");
        this.addChild(settingComponent);
        settingComponent.styleSetWidth(16);
        settingComponent.styleSetHeight(16);
        settingComponent.setHoverCursorToIndicateClick();
        settingComponent.setBlurRadius(() -> settingComponent.shouldShowHoveredEffects() ? 1f : 0);
        settingComponent.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            this.openSettings(module);
            return true;
        });

        YogaNode enabledSwitch = new YogaNode();
        this.addChild(enabledSwitch);
        enabledSwitch.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            if (module.isEnabled() != oldModuleState) {
                AnimationValue.tweenEngine.cancelTarget(toggleAnimation);
                toggleAnimation.to(.75f).value(module.isEnabled() ? 1 : 0).ease(Animations.easeOutBounce).start();
                oldModuleState = module.isEnabled();
            }

            float switchHeight = 12;
            drawSwitchAnimation(canvas, 0, 0, 28, switchHeight, toggleAnimation.getValue());
        });
        enabledSwitch.styleSetWidth(28);
        enabledSwitch.styleSetHeight(12);
    }

    public void openSettings(Module module) {
        ValuesPage modulesValuePage = new ValuesPage(module.getValueGroups());
        modulesValuePage.getAllGroups().addChild(new ModuleInfoGroupContainer(module));
        YogaNode moduleExtraStuff = module.getGuiNode();
        if (moduleExtraStuff != null) {
            modulesValuePage.getAllGroups().addChild(moduleExtraStuff);
        }

        MainFrame.pageHolder.addPage(new Page(module.getName(), modulesValuePage));
    }

    public static void drawSwitchAnimation(CanvasWrapper canvas, float x, float y, float width, float height, float animation) {
        float smallerSize = 1;
        float circleRadius = (height / 2) - smallerSize;

        try (Paint paint = SkiaUtils.createPaintForColor(UIUtils.transitionColor(ModuleComponent.switchBackgroundColor, MainFrame.s_funColor.get(), animation))) {
            canvas.drawRRect(RRect.makeXYWH(x, y, width, height, height / 2), paint);
        }

        try (Paint paint = SkiaUtils.createPaintForColor(Color.white)) {
            canvas.drawCircle(x + circleRadius + smallerSize + (width - smallerSize - (circleRadius * 2) - smallerSize) * animation, y + circleRadius + smallerSize, circleRadius, paint);
        }
    }

    private void renderStar(CanvasWrapper canvas, Module module, YogaNode favoriteComponent, Paint paint) {
        starStroke.render(canvas, 0, 0, favoriteComponent.getWidth(), favoriteComponent.getHeight(), paint);
        if (module.isFavorite()) {
            starFill.render(canvas, 0, 0, favoriteComponent.getWidth(), favoriteComponent.getHeight(), paint);
        }
    }

}
