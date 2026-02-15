package com.inertiaclient.base.gui;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.render.animation.AnimationValue;
import com.inertiaclient.base.render.yoga.YogaNode;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ModernClickGui extends YogaScreen {

    public static final ModernClickGui MODERN_CLICK_GUI = new ModernClickGui();
    public static ResourceLocation UNKNOWN_TEXTURE = ResourceLocation.fromNamespaceAndPath("icb", "textures/gui-unknown.png");//MissingSprite.getMissingSpriteId()

    @Getter
    private MainFrame mainFrame;
    @Getter
    private Tooltip tooltip;
    @Getter
    private Notifcations notifcations;

    public ModernClickGui() {
        super(Component.literal(""));
    }

    @Override
    protected void initRoot(YogaNode root) {
        root.addChild(this.mainFrame = new MainFrame());
        root.addAbsoluteChild(tooltip = new Tooltip());
        root.addAbsoluteChild(notifcations = new Notifcations());
    }

    @Override
    protected void init() {
        var clickGuiSettings = InertiaBase.instance.getSettings().getClickGuiSettings();
        AnimationValue.tweenEngine.cancelTarget(mainFrame.getAnimationValue());
        mainFrame.getAnimationValue().to(clickGuiSettings.getAnimationDuration().getValue()).value(1).ease(clickGuiSettings.getAnimation().getValue()).start();
    }

    @Override
    public void removed() {
        AnimationValue.tweenEngine.cancelTarget(mainFrame.getAnimationValue());

        var clickGuiSettings = InertiaBase.instance.getSettings().getClickGuiSettings();
        mainFrame.getAnimationValue().to(clickGuiSettings.getAnimationDuration().getValue()).value(0).ease(clickGuiSettings.getAnimation().getValue()).start();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }


}
