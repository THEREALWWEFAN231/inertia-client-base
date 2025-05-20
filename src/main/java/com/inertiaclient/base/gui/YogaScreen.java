package com.inertiaclient.base.gui;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.render.skia.SkiaOpenGLInstance;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.CursorUtils;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.util.yoga.Yoga;

import static org.lwjgl.util.yoga.Yoga.YGDirectionLTR;
import static org.lwjgl.util.yoga.Yoga.YGNodeCalculateLayout;

public abstract class YogaScreen extends BetterScreen {

    @Getter
    private YogaNode root;

    private SkiaOpenGLInstance skiaInstance;


    public YogaScreen(Text title) {
        super(title);

        if (skiaInstance == null) {
            skiaInstance = new SkiaOpenGLInstance();
            //skiaInstance.setFps(() -> 120f);
        }

        Yoga.YGConfigSetUseWebDefaults(Yoga.YGConfigGetDefault(), true);

        this.root = new YogaNode();
        this.initRoot(this.root);
    }

    public void debugRefreshRoot() {
        this.root = new YogaNode();
        this.initRoot(this.root);
    }

    protected abstract void initRoot(YogaNode root);

    @Override
    public void betterRender(DrawContext context, float mouseX, float mouseY, float delta) {
        skiaInstance.setup(() -> {
            //calculate "every" components width, then set their positions
            root.beforeLayoutCalculations(context, mouseX, mouseY, delta, skiaInstance.getCanvasWrapper());
            //TODO: only on window size change
            YGNodeCalculateLayout(root.getNativeNode(), width, height, YGDirectionLTR);
            root.setWidths(0, 0, context, mouseX, mouseY, delta);
            root.setGlobalPositions(0, 0, context, mouseX, mouseY, delta);
            CursorUtils.Cursor cursor = null;
            YogaNode hoveredComponent = root.getHov(mouseX, mouseY);
            YogaNode iteratingParent = hoveredComponent;
            while (iteratingParent != null) {

                if (cursor == null && iteratingParent.getHoverCursor() != null) {
                    cursor = iteratingParent.getHoverCursor();
                }

                iteratingParent.setShowHoveredEffects(true);
                iteratingParent = iteratingParent.getParent();
            }

            if (InertiaBase.instance.getSettings().getClickGuiSettings().getCustomCursors().getValue()) {
                if (cursor != null) {
                    CursorUtils.setCursor(cursor);
                } else {
                    CursorUtils.setCursor(CursorUtils.Cursor.ARROW);
                }
            }

            root.beforeDraw(context, mouseX, mouseY, 0, 0, delta, skiaInstance.getCanvasWrapper());
            root.draw(context, mouseX, mouseY, 0, 0, delta, skiaInstance.getCanvasWrapper());

            root.reset(mouseX, mouseY);
        });
    }

    @Override
    public boolean mouseClicked(double mouseXD, double mouseYD, int button) {
        float mouseX = (float) mouseXD;
        float mouseY = (float) mouseYD;

        this.root.globalMouseClicked(mouseX, mouseY, ButtonIdentifier.fromGLFW(button));
        if (this.root.mouseClicked(mouseX, mouseY, ButtonIdentifier.fromGLFW(button))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseXD, double mouseYD, int button) {
        float mouseX = (float) mouseXD;
        float mouseY = (float) mouseYD;

        this.root.globalMouseReleased(mouseX, mouseY, ButtonIdentifier.fromGLFW(button));
        this.root.mouseReleased(mouseX, mouseY, ButtonIdentifier.fromGLFW(button));
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseXD, double mouseYD, double horizontalAmount, double verticalAmount) {
        float mouseX = (float) mouseXD;
        float mouseY = (float) mouseYD;

        if (this.root.mouseScrolled(mouseX, mouseY, (float) verticalAmount)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean result = this.root.keyPressed(keyCode, scanCode, modifiers);
        if (result) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        this.root.charTyped(chr, modifiers);
        return false;
    }

}