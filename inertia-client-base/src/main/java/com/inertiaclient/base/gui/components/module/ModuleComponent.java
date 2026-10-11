package com.inertiaclient.base.gui.components.module;

import com.inertiaclient.base.InertiaBase;
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
import com.inertiaclient.base.utils.CursorUtils;
import com.inertiaclient.base.utils.UIUtils;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.RRect;

import java.awt.Color;

public class ModuleComponent extends YogaNode {

    private static final boolean MUST_USE_BUTTONS = false;
    private AnimationValue toggleAnimation;
    private AnimationValue descriptionAnimation;

    private boolean oldEnabledState;
    public static final float BACKGROUND_OPACITY = .24f;
    public static final Color BACKGROUND_COLOR = new Color(0, 0, 0, (int) (255 * BACKGROUND_OPACITY));
    public static final Color switchBackgroundColor = new Color(104, 104, 104);
    private static final SvgRenderer starStroke = new SvgRenderer("icb/textures/star-stroke.svg");
    private static final SvgRenderer starFill = new SvgRenderer("icb/textures/star-fill.svg");

    public ModuleComponent(Module module, ModulesPage.FavoritesList favoritesList) {
        this.oldEnabledState = module.isEnabled();
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

        var clickGuiSettings = InertiaBase.instance.getSettings().getClickGuiSettings();
        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            if (module.isEnabled() != oldEnabledState) {
                AnimationValue.tweenEngine.cancelTarget(toggleAnimation);
                toggleAnimation.to(.75f).value(module.isEnabled() ? 1 : 0).ease(Animations.easeOutBack).start();
                oldEnabledState = module.isEnabled();
            }

            Color enabledColor = UIUtils.colorWithAlpha(clickGuiSettings.getPrimaryColor().getValue().getRenderColor(), BACKGROUND_OPACITY);
            canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), 5, UIUtils.transitionColor(BACKGROUND_COLOR, enabledColor, Math.clamp(this.toggleAnimation.getValue(), 0, 1)));

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

        if (!MUST_USE_BUTTONS) {
            this.setHoverCursorToIndicateClick();
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
        }

        YogaNode favoriteComponent = new YogaNode();
        this.addChild(favoriteComponent);
        favoriteComponent.styleSetWidth(12);
        favoriteComponent.styleSetHeight(12);
        favoriteComponent.setHoverCursorToIndicateClick();
        favoriteComponent.setRenderCallback((graphics, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
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
            if (button == ButtonIdentifier.LEFT) {
                module.setFavorite(!module.isFavorite());
                favoritesList.refresh();
            }
            return true;
        });

        SvgComponent settingComponent = new SvgComponent("icb/textures/setting-gear.svg");
        this.addChild(settingComponent);
        settingComponent.styleSetWidth(16);
        settingComponent.styleSetHeight(16);
        settingComponent.setHoverCursorToIndicateClick();
        settingComponent.setBlurRadius(() -> settingComponent.shouldShowHoveredEffects() ? 1f : 0);
        settingComponent.setIsVisible(() -> clickGuiSettings.getModuleSettingsIcon().getValue());
        settingComponent.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                this.openSettings(module);
            }
            return true;
        });

        YogaNode enabledSwitch = new YogaNode();
        enabledSwitch.setHoverCursor(CursorUtils.Cursor.HAND);
        enabledSwitch.styleSetWidth(28);
        enabledSwitch.styleSetHeight(12);
        enabledSwitch.setIsVisible(() -> clickGuiSettings.getModuleEnabledSwitch().getValue());
        enabledSwitch.setRenderCallback((graphics, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            float switchWidth = 28;
            float switchHeight = 12;
            drawSwitchAnimation(canvas, 0, 0, switchWidth, switchHeight, toggleAnimation.getValue());

            if (module.isEnabled() || toggleAnimation.getValue() != 0) {
                // CanvasWrapper.getFreshTextBuilder().setBlurRadius(1.5f).basic("on", 3 + .5f, switchHeight / 2f + .5f).setColor(new Color(0.26f, 0.26f, 0.26f, Math.clamp(toggleAnimation.getValue(), 0, 1))).setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE).draw(canvas);
                CanvasWrapper.getFreshTextBuilder().basic("on", 3, switchHeight / 2f).setColor(UIUtils.colorWithAlpha(Color.white, Math.clamp(toggleAnimation.getValue(), 0, 1))).setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE).draw(canvas);
            }
            if (!module.isEnabled() || toggleAnimation.getValue() != 1) {
                //CanvasWrapper.getFreshTextBuilder().setBlurRadius(1.5f).basic("off", switchWidth - 3 + .5f, switchHeight / 2f + .5f).setColor(new Color(0.26f, 0.26f, 0.26f, 1 - Math.clamp(toggleAnimation.getValue(), 0, 1))).setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.RIGHT).setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE).draw(canvas);
                CanvasWrapper.getFreshTextBuilder().basic("off", switchWidth - 3, switchHeight / 2f).setColor(UIUtils.colorWithAlpha(Color.white, 1 - Math.clamp(toggleAnimation.getValue(), 0, 1))).setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.RIGHT).setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE).draw(canvas);
            }
        });
        enabledSwitch.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                module.toggle();
                //return true;
            }
            return true;
        });
        this.addChild(enabledSwitch);
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
