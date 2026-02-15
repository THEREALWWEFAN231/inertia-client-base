package com.inertiaclient.base.gui.components;

import com.inertiaclient.base.render.animation.AnimationValue;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.ExactPercentAuto;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.YogaEdge;
import lombok.Getter;

import java.util.ArrayDeque;

public class PageHolder extends YogaNode {

    @Getter
    private ArrayDeque<Page> pages = new ArrayDeque<>();
    private YogaNode emptySpace;

    private AnimationValue backpageAnimation = new AnimationValue();
    private YogaNode animatedBackPage;

    public PageHolder() {
        this.styleSetWidth(100, ExactPercentAuto.PERCENTAGE);
        this.styleSetFlexGrow(1);
        float padding = 3f;
        this.styleSetPadding(YogaEdge.LEFT, padding);
        this.styleSetPadding(YogaEdge.TOP, padding);
        this.styleSetPadding(YogaEdge.RIGHT, padding);
        this.styleSetPadding(YogaEdge.BOTTOM, padding);

        this.emptySpace = new YogaNode();
        this.addChild(emptySpace);
        emptySpace.styleSetFlexGrow(1);
        emptySpace.styleSetFlexShrink(1);
        emptySpace.styleSetFlexDirection(FlexDirection.COLUMN);
        /*emptySpace.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {

            if (animatedBackPage != null) {
                canvas.save();
                canvas.clipRect(0, 0, emptySpace.getWidth(), emptySpace.getHeight());
                float xTranslate = 0;
                float yTranslate = this.height / 2;
                float scale = 1 - backpageAnimation.getValue();
                //canvas.translate(xTranslate, yTranslate);
                //canvas.scale(scale, scale);
                //canvas.translate(xTranslate * -1, yTranslate * -1);
                canvas.translate(-(emptySpace.getWidth() * backpageAnimation.getValue()), -(emptySpace.getHeight() * backpageAnimation.getValue()));
                animatedBackPage.draw(context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas);
                canvas.restore();
            }
        });*/
    }

    public Page getCurrentPage() {
        return this.pages.peek();
    }

    public void addPage(Page page) {
        if (emptySpace.getChildren().size() > 0) {
            emptySpace.removeChild(emptySpace.getChildAtIndex(0));
        }
        emptySpace.addChild(page.getNode());
        pages.push(page);

        MainFrame.topPanel.clearSearch();
    }

    public boolean canGoBack() {
        return this.pages.size() > 1;
    }

    public boolean goBack() {
        if (!canGoBack()) {
            return false;
        }
        Page removing = this.pages.pop();
        emptySpace.removeChild(removing.getNode());
        removing.getNode().revokeLifeCycle();
        this.animatedBackPage = removing.getNode();

        Page restoredPage = this.pages.peek();
        emptySpace.addChild(restoredPage.getNode());

        MainFrame.topPanel.clearSearch();
        /*this.backpageAnimation.setValue(0);
        this.backpageAnimation.to(1).ease(Animations.easeOutQuad).value(1).addCallback(TweenEvents.COMPLETE, animationValueTween -> {
            animatedBackPage = null;
            return Unit.INSTANCE;
        }).start();*/
        return true;
    }

    public void removeHistory() {
        Page p = pages.peek();
        pages.stream().skip(1).forEach(page -> {
            page.getNode().revokeLifeCycle();
        });
        pages.clear();
        pages.add(p);
    }
}
