package com.inertiaclient.base.gui.components.friends;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventListener;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.EventTarget;
import com.inertiaclient.base.event.impl.inertia.FriendStateEvent;
import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.gui.Notifcations;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.render.animation.AnimationValue;
import com.inertiaclient.base.render.animation.Animations;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.Friend;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Path;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public class FriendAddRemoveButton extends YogaNode {

    private String playername;
    private boolean wasRemoved;
    private AnimationValue friendStateAnimation;

    @EventTarget
    private final EventListener<FriendStateEvent> event = this::onEvent;

    public FriendAddRemoveButton(String playername, UUID uuid) {
        this.playername = playername;
        this.friendStateAnimation = new AnimationValue();
        this.friendStateAnimation.setValue(InertiaBase.instance.getFriendManager().isFriend(this.playername) ? 0 : 1);

        this.styleSetMinWidth(8);
        this.styleSetWidth(8);
        this.styleSetHeight(8);
        this.setHoverCursorToIndicateClick();

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            try (Path path = new Path(); Paint stroke = SkiaUtils.createStrokePaint(MainFrame.s_unselectedTextColor.get(), MainFrame.s_lineWidth.get())) {
                if (friendStateAnimation.getValue() != 0) {
                    //up -> down
                    //path.moveTo(this.getWidth() / 2, 0);
                    //path.lineTo(this.getWidth() / 2, this.getHeight() * friendStateAnimation.getValue());
                    float halfLineHeight = (this.getHeight() / 2) * friendStateAnimation.getValue();
                    path.moveTo(this.getWidth() / 2, this.getHeight() / 2);
                    path.lineTo(this.getWidth() / 2, (this.getHeight() / 2) - halfLineHeight);
                    path.moveTo(this.getWidth() / 2, this.getHeight() / 2);
                    path.lineTo(this.getWidth() / 2, (this.getHeight() / 2) + halfLineHeight);
                }
                //left -> right
                path.moveTo(0, this.getHeight() / 2);
                path.lineTo(this.getWidth(), this.getHeight() / 2);

                canvas.drawPath(path, stroke);

                if (this.shouldShowHoveredEffects()) {
                    try (Paint blur = SkiaUtils.createStrokePaint(MainFrame.s_unselectedTextColor.get(), MainFrame.s_lineWidth.get())) {
                        SkiaUtils.setPaintBlur(blur, 1);
                        canvas.drawPath(path, blur);
                    }
                }
            }
        });

        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (InertiaBase.instance.getFriendManager().isFriend(this.playername)) {
                InertiaBase.instance.getFriendManager().removeFriend(this.playername);
                this.wasRemoved = true;
                ModernClickGui.MODERN_CLICK_GUI.getNotifcations().addNotification(Notifcations.Notification.builder().text(Component.translatable("icb.command.friend.removed_friend", this.playername)).displayTime(1000).build());
            } else {
                ModernClickGui.MODERN_CLICK_GUI.getNotifcations().addNotification(Notifcations.Notification.builder().text(Component.translatable(this.wasRemoved ? "icb.gui.pages.friends.readded" : "icb.command.friend.added_friend", this.playername)).displayTime(1000).build());
                InertiaBase.instance.getFriendManager().addFriend(new Friend(this.playername, uuid));
            }

            return true;
        });

        EventManager.register(this);
        this.setLifeCycleEndCallback(() -> {
            EventManager.unregister(this);
        });
    }

    private void onEvent(FriendStateEvent event) {
        if (!event.getFriend().getUsername().equals(this.playername)) {
            return;
        }
        AnimationValue.tweenEngine.cancelTarget(friendStateAnimation);
        friendStateAnimation.to(.3f).value(event.isWasAdded() ? 0 : 1).ease(Animations.linear).start();
    }

}
