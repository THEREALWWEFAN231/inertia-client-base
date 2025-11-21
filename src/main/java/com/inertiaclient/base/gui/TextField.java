package com.inertiaclient.base.gui;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.Fonts;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.utils.TimerUtil;
import com.inertiaclient.base.utils.UIUtils;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Path;
import io.github.humbleui.types.Rect;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SUPER;

//thank you chat gbt, heavily edited
public class TextField {

    private final StringBuilder text = new StringBuilder();
    private int cursorPos = 0;
    private int selectionStart = -1;
    @Getter
    private boolean isFocused = false;

    private float scrollX = 0;
    private boolean dragging = false;
    private float clickX;
    private TimerUtil cursorFlash = new TimerUtil();

    @Setter
    private Font font = Fonts.getDefault();

    @Getter
    @Setter
    private float x;
    @Getter
    @Setter
    private float y;
    @Getter
    private float width;
    @Getter
    @Setter
    private float height;
    @Setter
    private Supplier<Color> backgroundColor = () -> new Color(0, 0, 0, 200);
    @Setter
    @Nullable
    private Supplier<Color> strokeColor;
    @Setter
    private Supplier<Float> strokeWidth = () -> 1f;
    @Setter
    private Supplier<Color> textColor = () -> Color.white;
    @Setter
    private Supplier<Color> selectionColor = () -> new Color(51, 153, 255, 200);
    @Setter
    private Supplier<Float> borderRadius = () -> 0f;
    @Setter
    private Text placeHolderText = Text.literal("");
    @Setter
    private Color placeHolderTextColor = Color.lightGray;
    @Setter
    private Supplier<Float> textPadding = () -> this.height * .3f;
    @Nullable
    @Setter
    private Consumer<String> changedListener;
    @Setter
    private float fontSize = Fonts.DEFAULT_SIZE;
    @Setter
    private int maxTextLength = Integer.MAX_VALUE;

    public void render(CanvasWrapper canvas, float mouseX, float mouseY, float delta) {
        float oldFontSize = this.font.getSize();
        try {
            this.font.setSize(this.fontSize);

            if (dragging && isFocused) {
                cursorPos = getMouseCharIndex(mouseX);
                scrollCursorIntoView();
            }

            float padding = this.textPadding.get();
            {
                canvas.drawRRect(x, y, width, height, this.borderRadius.get(), this.backgroundColor.get());

                if (strokeColor != null) {
                    try (Paint stroke = SkiaUtils.createStrokePaint(this.strokeColor.get(), this.strokeWidth.get())) {
                        canvas.drawRRect(x, y, width, height, this.borderRadius.get(), stroke);
                    }
                }
            }

            Color textColor = this.textColor.get();
            String renderString = text.toString();
            if (text.isEmpty()) {
                renderString = this.placeHolderText.getString();
                textColor = this.placeHolderTextColor;
            }

            float textX = x + padding - scrollX;
            float textY = y + height / 2 + font.getMetrics().getCapHeight() / 2;

            canvas.save();
            canvas.clipRect(Rect.makeXYWH(x + padding, y, width - (padding * 2), height));

            // Draw selection
            if (hasSelection()) {
                int selStart = Math.min(cursorPos, selectionStart);
                int selEnd = Math.max(cursorPos, selectionStart);
                float selStartX = textX + font.measureTextWidth(renderString.substring(0, selStart));
                float selEndX = textX + font.measureTextWidth(renderString.substring(0, selEnd));
                canvas.drawRect(selStartX, y, selEndX - selStartX, height, this.selectionColor.get());
            }

            // Draw text
            try (Paint paint = SkiaUtils.createPaintForColor(textColor)) {
                canvas.getCanvas().drawString(renderString, textX, textY, font, paint);
            }

            // Draw blinking cursor
            cursorFlash.update();
            if (isFocused && cursorFlash.getTime() <= 500) {
                float cursorX = textX + font.measureTextWidth(renderString.substring(0, cursorPos));
                try (Path path = new Path(); Paint stroke = SkiaUtils.createStrokePaint(Color.white, .5f)) {
                    float yStart = font.getMetrics().getDescent();
                    float y = this.y + (this.height / 2);
                    float lineHeight = font.getMetrics().getHeight();
                    path.moveTo(cursorX, y - (lineHeight / 2));
                    path.lineTo(cursorX, y + (lineHeight / 2));
                    canvas.drawPath(path, stroke);
                    //canvas.getCanvas().drawLine(cursorX, y - (lineHeight / 2), cursorX, y + (lineHeight / 2), stroke);
                }
            }
            if (cursorFlash.hasDelayRun(1000)) {
                cursorFlash.reset();
            }

            canvas.restore();
        } finally {
            this.font.setSize(oldFontSize);
        }
    }

    public boolean charTyped(char chr, int modifiers) {
        if (this.isFocused) {
            String toInsert = Character.toString(chr);
            insertText(toInsert);
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isFocused) {
            int SYSTEM_COMMAND_MOD = MinecraftClient.IS_SYSTEM_MAC ? GLFW_MOD_SUPER : GLFW_MOD_CONTROL;
            boolean isControlDown = (modifiers & SYSTEM_COMMAND_MOD) != 0;
            switch (keyCode) {
                case GLFW.GLFW_KEY_LEFT -> {
                    if (isControlDown) {
                        this.moveCursorWordLeft((modifiers & GLFW.GLFW_MOD_SHIFT) != 0);
                    } else {
                        moveCursor(-1, modifiers);
                    }
                }
                case GLFW.GLFW_KEY_RIGHT -> {
                    if (isControlDown) {
                        this.moveCursorWordRight((modifiers & GLFW.GLFW_MOD_SHIFT) != 0);
                    } else {
                        moveCursor(1, modifiers);
                    }
                }
                case GLFW.GLFW_KEY_HOME -> {
                    moveCursorToStart(modifiers);
                }
                case GLFW.GLFW_KEY_END -> {
                    moveCursorToEnd(modifiers);
                }
                case GLFW.GLFW_KEY_BACKSPACE -> {
                    if (isControlDown) {
                        deleteWordLeft();
                    } else {
                        backspace();
                    }
                }
                case GLFW.GLFW_KEY_DELETE -> {
                    if (isControlDown) {
                        deleteWordRight();
                    } else {
                        delete();
                    }
                }
                case GLFW.GLFW_KEY_A -> {
                    if (isControlDown) {
                        selectAll();
                    }
                }
                case GLFW.GLFW_KEY_X -> {
                    if (isControlDown) {
                        cut();
                    }
                }
                case GLFW.GLFW_KEY_C -> {
                    if (isControlDown) {
                        copy();
                    }
                }
                case GLFW.GLFW_KEY_V -> {
                    if (isControlDown) {
                        paste();
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void mouseClicked(float mouseX, float mouseY, ButtonIdentifier button) {
        isFocused = UIUtils.isHovered(mouseX, mouseY, this.x, this.y, this.width, this.height);
        if (button == ButtonIdentifier.LEFT) {
            clickX = mouseX;
        }
        if (isFocused) {
            dragging = true;
            cursorPos = getMouseCharIndex(mouseX);
            selectionStart = cursorPos;
            scrollCursorIntoView();
            cursorFlash.reset();
        } else {
            dragging = false;
        }
    }

    public void mouseReleased(float mouseX, float mouseY, ButtonIdentifier button) {
        if (dragging) {
            if (Math.abs(mouseX - clickX) <= 1) {
                selectionStart = -1;
            }
        }
        dragging = false;
    }


    public void setFocused(boolean isFocused) {
        this.isFocused = isFocused;
        if (isFocused) {
            selectionStart = -1;
            cursorPos = text.length(); // or wherever you want it
            scrollCursorIntoView();
            cursorFlash.reset();
        }
    }

    private int getMouseCharIndex(float mouseX) {
        float oldFontSize = this.font.getSize();
        try {
            this.font.setSize(this.fontSize);
            float relX = mouseX - (x + this.textPadding.get()) + scrollX;
            String str = text.toString();
            for (int i = 1; i <= str.length(); i++) {
                float w = font.measureTextWidth(str.substring(0, i));
                if (w > relX) {
                    return i - 1;
                }
            }
            return str.length();

        } finally {
            this.font.setSize(oldFontSize);
        }
    }

    private boolean hasSelection() {
        return selectionStart != -1 && selectionStart != cursorPos;
    }

    private void moveCursor(int xDirection, int mods) {
        if ((mods & GLFW.GLFW_MOD_SHIFT) != 0) {
            if (!hasSelection()) {
                selectionStart = cursorPos;
            }
        } else {
            selectionStart = -1;
        }

        cursorPos = Math.max(0, Math.min(text.length(), cursorPos + xDirection));
        scrollCursorIntoView();
    }

    private void moveCursorWordLeft(boolean select) {
        if (!select) {
            selectionStart = -1;
        }
        if (cursorPos == 0) {
            return;
        }
        int pos = cursorPos - 1;

        // Skip spaces first
        while (pos > 0 && Character.isWhitespace(text.charAt(pos))) {
            pos--;
        }
        // Skip over word
        while (pos > 0 && !Character.isWhitespace(text.charAt(pos - 1))) {
            pos--;
        }

        updateCursorAndSelection(pos, select);
    }

    private void moveCursorWordRight(boolean select) {
        if (!select) {
            selectionStart = -1;
        }
        int len = text.length();
        if (cursorPos >= len) {
            return;
        }
        int pos = cursorPos;

        // Skip current word
        while (pos < len && !Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        // Skip trailing whitespace
        while (pos < len && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }

        updateCursorAndSelection(pos, select);
    }


    private void updateCursorAndSelection(int newPos, boolean select) {
        if (select) {
            if (selectionStart == -1)
                selectionStart = cursorPos;
        } else {
            selectionStart = -1;
        }
        cursorPos = newPos;
        scrollCursorIntoView();
    }

    private void backspace() {
        if (hasSelection()) {
            deleteSelection();
        } else if (cursorPos > 0) {
            text.deleteCharAt(cursorPos - 1);
            cursorPos--;
            this.onChanged();
        }
        scrollCursorIntoView();
    }

    private void delete() {
        if (hasSelection()) {
            deleteSelection();
        } else if (cursorPos < text.length()) {
            text.deleteCharAt(cursorPos);
            this.onChanged();
        }
        scrollCursorIntoView();
    }

    private void deleteSelection() {
        if (!hasSelection()) {
            return;
        }
        int selStart = Math.min(cursorPos, selectionStart);
        int selEnd = Math.max(cursorPos, selectionStart);
        text.delete(selStart, selEnd);
        cursorPos = selStart;
        selectionStart = -1;
        this.onChanged();
    }

    private void moveCursorToStart(int mods) {
        if ((mods & GLFW.GLFW_MOD_SHIFT) != 0) {
            if (!hasSelection())
                selectionStart = cursorPos;
        } else {
            selectionStart = -1;
        }
        cursorPos = 0;
        scrollCursorIntoView();
    }

    private void moveCursorToEnd(int mods) {
        if ((mods & GLFW.GLFW_MOD_SHIFT) != 0) {
            if (!hasSelection())
                selectionStart = cursorPos;
        } else {
            selectionStart = -1;
        }
        cursorPos = text.length();
        scrollCursorIntoView();
    }


    private void selectAll() {
        selectionStart = 0;
        cursorPos = text.length();
    }

    private void cut() {
        if (!hasSelection()) {
            return;
        }
        copy(); // reuse existing copy logic
        delete(); // delete selected text
    }

    private void copy() {
        if (!hasSelection()) {
            return;
        }
        int selStart = Math.min(cursorPos, selectionStart);
        int selEnd = Math.max(cursorPos, selectionStart);
        String selected = text.substring(selStart, selEnd);
        InertiaBase.mc.keyboard.setClipboard(selected);
    }

    private void paste() {
        try {
            String data = InertiaBase.mc.keyboard.getClipboard();
            insertText(data);
        } catch (Exception ignored) {
        }
    }

    private void deleteWordLeft() {
        if (hasSelection()) {
            delete();
            return;
        }

        if (cursorPos == 0) {
            return;
        }

        int pos = cursorPos - 1;
        while (pos > 0 && Character.isWhitespace(text.charAt(pos))) {
            pos--;
        }
        while (pos > 0 && !Character.isWhitespace(text.charAt(pos - 1))) {
            pos--;
        }

        text.delete(pos, cursorPos);
        cursorPos = pos;
        scrollCursorIntoView();
        this.onChanged();
    }

    private void deleteWordRight() {
        if (hasSelection()) {
            delete();
            return;
        }

        if (cursorPos >= text.length())
            return;

        int end = cursorPos;
        while (end < text.length() && Character.isWhitespace(text.charAt(end))) {
            end++;
        }
        while (end < text.length() && !Character.isWhitespace(text.charAt(end))) {
            end++;
        }

        text.delete(cursorPos, end);
        scrollCursorIntoView();
        this.onChanged();
    }

    private void insertText(String str) {
        deleteSelection();

        int allowed = Math.min(str.length(), this.maxTextLength - text.length());
        if (allowed <= 0) {
            return;
        }
        str = str.substring(0, allowed);

        text.insert(cursorPos, str);
        cursorPos += str.length();
        scrollCursorIntoView();
        this.onChanged();
    }

    public void setText(String text) {
        if (text.length() > this.maxTextLength) {
            text = text.substring(0, this.maxTextLength);
        }

        this.text.setLength(0);
        this.text.append(text);
        cursorPos = text.length();
        selectionStart = -1;
        scrollCursorIntoView();
        this.onChanged();
    }

    public String getText() {
        return text.toString();
    }

    private void scrollCursorIntoView() {
        float oldFontSize = this.font.getSize();
        try {
            this.font.setSize(this.fontSize);

            float padding = this.textPadding.get();
            float cursorX = font.measureTextWidth(text.substring(0, cursorPos));
            float visibleStart = scrollX;
            float visibleEnd = scrollX + (width - 2 * padding);

            if (cursorX < visibleStart) {
                scrollX = cursorX;
            } else if (cursorX > visibleEnd) {
                scrollX = cursorX - (width - 2 * padding);
            }

            float maxScroll = Math.max(0, font.measureTextWidth(text.toString()) - (width - 2 * padding));
            scrollX = Math.max(0, Math.min(scrollX, maxScroll));
        } finally {
            this.font.setSize(oldFontSize);
        }
    }

    private void onChanged() {
        if (this.changedListener != null) {
            this.changedListener.accept(this.text.toString());
        }
    }

    public void setWidth(float width) {
        if (this.width != width) {
            this.width = width;
            this.scrollCursorIntoView();
        }
    }
}