package com.inertiaclient.base.gui;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.render.animation.AnimationValue;
import com.inertiaclient.base.render.animation.Animations;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.yoga.AbsoulteYogaNode;
import com.inertiaclient.base.render.yoga.layouts.PositionType;
import com.inertiaclient.base.utils.TimerUtil;
import com.inertiaclient.base.utils.UIUtils;
import io.github.humbleui.skija.Paint;
import lombok.Builder;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.ArrayList;

public class Notifcations extends AbsoulteYogaNode {

    private ArrayList<Notification> notifications = new ArrayList<>();

    private Notification currentRenderingNotification;
    private AnimationValue openAnimation = new AnimationValue();
    private AnimationValue closeAnimation = new AnimationValue();
    private boolean isOpening;
    private boolean isClosing;


    public Notifcations() {
        this.styleSetPositionType(PositionType.ABSOLUTE);
        this.setHeight(12);

        this.setBeforeRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {

            if (currentRenderingNotification == null && !this.notifications.isEmpty()) {
                this.currentRenderingNotification = this.notifications.get(0);
                this.currentRenderingNotification.displayTimer.reset();
                AnimationValue.tweenEngine.cancelTarget(openAnimation);
                openAnimation.setValue(0);
                openAnimation.to(.3f).value(1).ease(Animations.easeOutQuad).start();
                isOpening = true;
                isClosing = false;
            }

            if (currentRenderingNotification == null) {
                return;
            }

            if (isOpening) {
                if (!AnimationValue.tweenEngine.containsTarget(openAnimation)) {
                    this.isOpening = false;
                } else {//dont start the time till the animation  is done
                    this.currentRenderingNotification.displayTimer.reset();
                }
            }


            this.currentRenderingNotification.displayTimer.update();


            boolean hasCurrentNotificationExpired = this.currentRenderingNotification.hasExpired();
            if (notifications.size() == 1 && hasCurrentNotificationExpired && !this.isClosing) {
                this.isClosing = true;
                AnimationValue.tweenEngine.cancelTarget(closeAnimation);
                closeAnimation.setValue(0);
                this.closeAnimation.to(.3f).value(1).ease(Animations.easeInQuad).start();
            }

            if (isClosing) {
                if (!AnimationValue.tweenEngine.containsTarget(closeAnimation)) {
                    this.isClosing = false;
                }
            }

            if (hasCurrentNotificationExpired && !this.isOpening && !this.isClosing) {
                this.notifications.remove(0);
                this.currentRenderingNotification = null;
                return;
            }

            this.setWidth(CanvasWrapper.getFreshTextBuilder().setText(this.currentRenderingNotification.text).getTextWidth() + 4);
            this.setX(UIUtils.getCenterOffset(this.getWidth(), this.getParent().getWidth()));
            this.setY(-this.getHeight() + (this.getHeight() * (this.isClosing ? 1 - this.closeAnimation.getValue() : this.openAnimation.getValue())));
        });

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            if (this.currentRenderingNotification == null) {
                return;
            }

            canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), 2.5f, new Color(0, 0, 0, 200));
            try (Paint stoke = SkiaUtils.createStrokePaint(MainFrame.s_lineColor.get(), MainFrame.s_lineWidth.get())) {
                canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), 2.5f, stoke);
            }

            var name = CanvasWrapper.getFreshTextBuilder();
            name.basic(this.currentRenderingNotification.text, this.getWidth() / 2, this.getHeight() / 2);
            name.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE);
            name.setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.CENTER);
            name.draw(canvas);
        });

    }


    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }

    @Builder
    public static class Notification {

        private Component text;
        private final TimerUtil displayTimer = new TimerUtil(true);
        @Builder.Default
        private long displayTime = 1500;

        public boolean hasExpired() {
            return this.displayTimer.hasDelayRun(this.displayTime);
        }
    }

}
