package com.inertiaclient.base.gui.components.friends;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventListener;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.EventTarget;
import com.inertiaclient.base.event.impl.ClientTickEvent;
import com.inertiaclient.base.event.impl.inertia.FriendStateEvent;
import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.gui.Notifcations;
import com.inertiaclient.base.gui.YogaTextField;
import com.inertiaclient.base.gui.components.SelectButton;
import com.inertiaclient.base.gui.components.TextLabel;
import com.inertiaclient.base.gui.components.helpers.VerticalListContainer;
import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.gui.components.tabbedpage.Tab;
import com.inertiaclient.base.gui.components.tabbedpage.TabbedPage;
import com.inertiaclient.base.gui.components.toppanel.SearchBox;
import com.inertiaclient.base.render.yoga.YogaBuilder;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.*;
import com.inertiaclient.base.utils.Friend;
import com.inertiaclient.base.utils.TimerUtil;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class FriendsPage extends TabbedPage {

    private Tab<VerticalListContainer> removeTab;

    private VerticalListContainer onlineList;

    private TimerUtil skinLookupTimer = new TimerUtil();
    private boolean hasLookedupHead;
    private YogaTextField addNameField;
    private CachedHeadComponent addHeadComponent;

    @EventTarget
    private final EventListener<FriendStateEvent> friendStateEventListener = this::onEvent;
    @EventTarget
    private final EventListener<ClientTickEvent> clientTickListener = this::onEvent;

    public FriendsPage() {
        super();

        EventManager.register(this);
        this.setLifeCycleEndCallback(() -> {
            EventManager.unregister(this);
        });
    }

    @Override
    public ArrayList<Tab> createTabs() {
        var tabs = new ArrayList<Tab>();

        Tab<YogaNode> onlinePlayersTab = new Tab(TabbedPage.getTextForPage("friends", "online"), this.createOnlineNode());
        tabs.add(onlinePlayersTab);

        Tab<VerticalListContainer> remove = new Tab(TabbedPage.getTextForPage("friends", "remove"), this.createRemoveNode());
        tabs.add(this.removeTab = remove);

        return tabs;
    }

    private YogaNode createOnlineNode() {
        YogaNode yogaNode = YogaBuilder.getFreshBuilder().setFlexGrow(1).setFlexShrink(1).setFlexDirection(FlexDirection.COLUMN).build();
        yogaNode.styleSetGap(GapGutter.ROW, 2);

        this.onlineList = new VerticalListContainer();
        InertiaBase.mc.player.connection.getListedOnlinePlayers().stream().filter(playerInfo -> !playerInfo.getProfile().getId().equals(InertiaBase.mc.getUser().getProfileId())).forEach(playerInfo -> {
            this.onlineList.addToList(new OnlinePlayerComponent(playerInfo));
        });
        if (onlineList.listSize() == 0) {
            this.onlineList.styleSetAlignItems(AlignItems.CENTER);
            this.onlineList.addToList(new TextLabel(Component.translatable("icb.gui.pages.friends.no_players_online")));
        }

        yogaNode.addChild(this.onlineList);
        yogaNode.addChild(this.createAddNode());

        return yogaNode;
    }

    private YogaNode createRemoveNode() {
        VerticalListContainer yogaNode = new VerticalListContainer();

        InertiaBase.instance.getFriendManager().getFriends().forEach(friend -> {
            yogaNode.addToList(new AddedFriendComponent(friend));
        });

        return yogaNode;
    }

    private YogaNode createAddNode() {
        YogaNode addNode = YogaBuilder.getFreshBuilder().setFlexGrow(0).setFlexShrink(0).setHeight(12).setAlignItems(AlignItems.CENTER).setJustifyContent(JustifyContent.CENTER).setGap(GapGutter.COLUMN, 2).build();

        this.addHeadComponent = new CachedHeadComponent();
        this.addHeadComponent.setHeadVisible(false);

        this.addNameField = new YogaTextField();
        this.addNameField.setPlaceHolderText(Component.translatable("icb.gui.pages.friends.add.text_field.placeholder_text"));
        this.addNameField.styleSetMinWidth(50);
        this.addNameField.styleSetWidth(33, ExactPercentAuto.PERCENTAGE);
        this.addNameField.styleSetHeight(10);
        this.addNameField.setBackgroundColor(() -> SearchBox.BACKGROUND_COLOR);
        this.addNameField.setBorderRadius(() -> SelectButton.BORDER_RADIUS);
        this.addNameField.setFontSize(AbstractGroupContainer.valuesFontSize);
        this.addNameField.setEnterAction(s -> addFriend());
        this.addNameField.setChangedListener(s -> {
            this.skinLookupTimer.reset();
            this.hasLookedupHead = false;
            this.addHeadComponent.setUuidOrName(null);
            this.addHeadComponent.setHeadVisible(s.length() >= 2);
        });

        SelectButton addButton = new SelectButton(Component.translatable("icb.gui.pages.friends.add.button"), this::addFriend);
        addNode.addChild(this.addHeadComponent);
        addNode.addChild(this.addNameField);
        addNode.addChild(addButton);

        return addNode;
    }

    private void addFriend() {
        if (!this.addNameField.getText().isEmpty()) {
            String playerName = this.addNameField.getText();
            if (InertiaBase.instance.getFriendManager().isFriend(playerName)) {
                ModernClickGui.MODERN_CLICK_GUI.getNotifcations().addNotification(Notifcations.Notification.builder().text(Component.translatable("icb.command.friend.already_friend", playerName)).displayTime(1000).build());
                return;
            }
            InertiaBase.instance.getFriendManager().addFriend(new Friend(playerName, null));
            ModernClickGui.MODERN_CLICK_GUI.getNotifcations().addNotification(Notifcations.Notification.builder().text(Component.translatable("icb.command.friend.added_friend", playerName)).displayTime(1000).build());
        }
    }

    private void onEvent(FriendStateEvent event) {
        if (event.isWasAdded()) {
            this.removeTab.getYogaNode().addToList(new AddedFriendComponent(event.getFriend()), event.getListIndex());
        } else {
            this.removeTab.getYogaNode().removeFromList(event.getListIndex());
        }
    }

    private void onEvent(ClientTickEvent event) {
        if (this.addNameField != null) {
            this.skinLookupTimer.update();
            if (this.skinLookupTimer.hasDelayRun(1000 / 3f) && !this.hasLookedupHead && this.addNameField.getText().length() >= 2) {
                addHeadComponent.setUuidOrName(this.addNameField.getText());
                this.hasLookedupHead = true;
            }
        }
    }
}
