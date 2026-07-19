package com.inertiaclient.base.gui;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.render.skia.SkiaVulkanInstance;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.CursorUtils;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.sdl.SDLKeyboard;
import org.lwjgl.util.yoga.Yoga;

import static org.lwjgl.util.yoga.Yoga.YGDirectionLTR;
import static org.lwjgl.util.yoga.Yoga.YGNodeCalculateLayout;

public abstract class YogaScreen extends BetterScreen {

    @Getter
    private YogaNode root;

    private SkiaVulkanInstance skiaInstance;


    public YogaScreen(Component title) {
        super(title);

        if (skiaInstance == null) {
            skiaInstance = new SkiaVulkanInstance();
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
    public void betterRender(GuiGraphicsExtractor graphics, float mouseX, float mouseY, float delta) {
        //graphics.fill((int) mouseX, (int) mouseY, (int) mouseX + 5, (int) mouseY + 5, 0xffff0000);

        skiaInstance.setup(graphics, () -> {
            //calculate "every" components width, then set their positions
            root.beforeLayoutCalculations(graphics, mouseX, mouseY, delta, skiaInstance.getCanvasWrapper());
            //TODO: only on window size change
            YGNodeCalculateLayout(root.getNativeNode(), width, height, YGDirectionLTR);
            root.setWidths(0, 0, graphics, mouseX, mouseY, delta);
            root.setGlobalPositions(0, 0, graphics, mouseX, mouseY, delta);
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

            root.beforeDraw(graphics, mouseX, mouseY, 0, 0, delta, skiaInstance.getCanvasWrapper());
            root.draw(graphics, mouseX, mouseY, 0, 0, delta, skiaInstance.getCanvasWrapper());

            root.reset(mouseX, mouseY);
        });
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        float mouseX = (float) mouseButtonEvent.x();
        float mouseY = (float) mouseButtonEvent.y();

        this.root.globalMouseClicked(mouseX, mouseY, ButtonIdentifier.fromGLFW(mouseButtonEvent.button()));
        if (this.root.mouseClicked(mouseX, mouseY, ButtonIdentifier.fromGLFW(mouseButtonEvent.button()))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        float mouseX = (float) mouseButtonEvent.x();
        float mouseY = (float) mouseButtonEvent.y();

        this.root.globalMouseReleased(mouseX, mouseY, ButtonIdentifier.fromGLFW(mouseButtonEvent.button()));
        this.root.mouseReleased(mouseX, mouseY, ButtonIdentifier.fromGLFW(mouseButtonEvent.button()));
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
    public boolean keyPressed(KeyEvent event) {
        boolean result = this.root.keyPressed(event.keycode(), event.key(), event.modifiers(), event);
        if (result) {
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        this.root.charTyped((char) event.codepoint(), -1);
        return false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        //dont render background
    }


    public void added() {
        super.added();
        SDLKeyboard.SDL_StartTextInput(InertiaBase.mc.getWindow().handle());
    }

    public void removed() {
        super.removed();
        SDLKeyboard.SDL_StopTextInput(InertiaBase.mc.getWindow().handle());
    }

}