package com.inertiaclient.base.render.yoga;

import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.gui.components.HorizontalScrollbar;
import com.inertiaclient.base.gui.components.VerticalScrollbar;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.layouts.*;
import com.inertiaclient.base.utils.CursorUtils;
import com.inertiaclient.base.utils.UIUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.util.yoga.Yoga;

import java.awt.Color;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.lwjgl.util.yoga.Yoga.*;


public class YogaNode {

    private boolean isSearching;
    private String searchQuery = "";

    //returns true if a child had a valid search
    public boolean onSearch(String searchFor) {
        this.searchQuery = searchFor;

        if (searchFor.equals("")) {
            isSearching = false;
        } else {
            isSearching = true;
        }


        this.children.forEach(yogaNode -> yogaNode.onSearch(searchFor));


        boolean valid = false;
        for (YogaNode yogaNode : this.getChildren()) {
            if (yogaNode.getSearchContext() == null || (yogaNode.getSearchContext().toLowerCase().contains(searchFor) && yogaNode.implIsVisible())) {
                yogaNode.styleSetDisplay(Display.FLEX);
                this.lastVisible = true;
                valid = true;
            } else {
                yogaNode.styleSetDisplay(Display.NONE);
                this.lastVisible = false;
            }
        }
        return valid;
    }

    public void refreshSearch() {
        this.onSearch(this.searchQuery);
    }

    @Getter
    private long nativeNode;

    @Getter
    private ArrayList<YogaNode> children = new ArrayList<>();
    @Getter
    private ArrayList<YogaNode> absoluteChildren = new ArrayList<>();
    @Getter
    private ArrayList<YogaNode> scrollbars = new ArrayList<>();
    @Getter
    private YogaNode parent;
    @Getter
    private int addedAtIndex;

    @Accessors(chain = true)
    @Setter
    private boolean debug;
    @Setter
    private Color debugColor = new Color(0, 0, 255, 150);

    @Getter
    protected float globalX;
    @Getter
    protected float globalY;
    @Getter
    protected float relativeX;
    @Getter
    protected float relativeY;
    @Getter
    protected float width;
    @Getter
    protected float height;
    @Getter
    private Display display = Display.FLEX;
    @Getter
    private PositionType positionType = PositionType.RELATIVE;

    private boolean hasDoneFirstInit;
    @Accessors(chain = true)
    @Setter
    private RenderCallback firstInitCallback;
    @Accessors(chain = true)
    @Setter
    private RenderCallback preLayoutCalculationsCallback;
    @Accessors(chain = true)
    @Setter
    private RenderCallback renderCallback;
    @Accessors(chain = true)
    @Setter
    @Getter
    private MouseCallback clickCallback;
    @Accessors(chain = true)
    @Setter
    private MouseCallback releaseClickCallback;
    @Accessors(chain = true)
    @Setter
    private MouseCallback globalClickCallback;
    @Accessors(chain = true)
    @Setter
    private ScrollCallback scrollCallback;

    @Accessors(chain = true)
    @Setter
    private RenderCallback beforeRenderCallback;
    @Accessors(chain = true)
    @Setter
    private RenderCallback preRenderCallback;
    @Accessors(chain = true)
    @Setter
    private RenderCallback preChildrenRenderCallback;

    @Accessors(chain = true)
    @Setter
    private KeyPressedCallback keyPressedCallback;
    @Accessors(chain = true)
    @Setter
    private CharTypedCallback charTypedCallback;
    @Setter
    private Runnable afterInitCallback;
    @Setter
    private Runnable beforeSetFirstPositionCallback;
    @Setter
    @Accessors(chain = true)
    private Consumer<Boolean> hoverCallback;//boolean is if start hovering
    @Setter
    @Accessors(chain = true)
    private Consumer<Integer> removedCallback;//integer 0 means this was removed, 1 means parent was removed, 2 means parents' parent was removed, etc

    @Setter
    private Function<Float, Float> widthModifier;
    @Setter
    private Function<Float, Float> heightModifier;

    @Setter
    @Getter
    private String searchContext;
    @Setter
    @Getter
    private Supplier<Boolean> shouldRenderTooltip;
    @Setter
    @Getter
    @Accessors(chain = true)
    private Supplier<String> tooltip;
    @Setter
    @Getter
    @Accessors(chain = true)
    private Supplier<Long> tooltipDelay;

    @Setter
    @Getter
    private float childrenScrollXOffset;
    @Setter
    @Getter
    private float childrenScrollYOffset;

    @Setter
    private boolean shouldScissorChildren = false;
    @Setter
    @Getter
    private CursorUtils.Cursor hoverCursor;

    private boolean hasVerticalScrollbar;
    private VerticalScrollbar verticalScrollbar;
    private boolean hasHorizontalScrollbar;
    private HorizontalScrollbar horizontalScrollbar;

    //used to make sure the releaseclick callback is fired correctly(user clicked and release on this), not just release on this
    private ButtonIdentifier lastClickedButton;

    private boolean lastVisible = true;
    private boolean hasSetPosition = false;

    public YogaNode() {
        this.nativeNode = Yoga.YGNodeNew();
    }

    public YogaNode(YogaNode parent) {
        this();

        parent.addChild(this);
    }

    public YogaNode styleSetFlexDirection(FlexDirection flexDirection) {
        Yoga.YGNodeStyleSetFlexDirection(nativeNode, flexDirection.ordinal());
        return this;
    }

    public YogaNode styleSetMinWidth(float width) {
        return this.styleSetMinWidth(width, ExactPercentAuto.EXACT);
    }

    public YogaNode styleSetMinWidth(float width, ExactPercentAuto unit) {
        switch (unit) {
            case EXACT -> Yoga.YGNodeStyleSetMinWidth(this.nativeNode, width);
            case PERCENTAGE -> Yoga.YGNodeStyleSetMinWidthPercent(this.nativeNode, width);
        }
        return this;
    }


    public YogaNode styleSetWidth(float width) {
        return this.styleSetWidth(width, ExactPercentAuto.EXACT);
    }

    public YogaNode styleSetWidth(float width, ExactPercentAuto unit) {
        switch (unit) {
            case EXACT -> Yoga.YGNodeStyleSetWidth(this.nativeNode, width);
            case PERCENTAGE -> Yoga.YGNodeStyleSetWidthPercent(this.nativeNode, width);
            case AUTO -> Yoga.YGNodeStyleSetWidthAuto(this.nativeNode);
        }
        return this;
    }

    public YogaNode styleSetMaxWidth(float width) {
        return this.styleSetMaxWidth(width, ExactPercentAuto.EXACT);
    }

    public YogaNode styleSetMaxWidth(float width, ExactPercentAuto unit) {
        switch (unit) {
            case EXACT -> Yoga.YGNodeStyleSetMaxWidth(this.nativeNode, width);
            case PERCENTAGE -> Yoga.YGNodeStyleSetMaxWidthPercent(this.nativeNode, width);
        }
        return this;
    }

    public YogaNode styleSetMinHeight(float height) {
        return this.styleSetMinHeight(height, ExactPercentAuto.EXACT);
    }

    public YogaNode styleSetMinHeight(float height, ExactPercentAuto unit) {
        switch (unit) {
            case EXACT -> Yoga.YGNodeStyleSetMinHeight(this.nativeNode, height);
            case PERCENTAGE -> Yoga.YGNodeStyleSetMinHeightPercent(this.nativeNode, height);
        }
        return this;
    }

    public YogaNode styleSetHeight(float height) {
        return this.styleSetHeight(height, ExactPercentAuto.EXACT);
    }

    public YogaNode styleSetHeight(float height, ExactPercentAuto unit) {
        switch (unit) {
            case EXACT -> Yoga.YGNodeStyleSetHeight(this.nativeNode, height);
            case PERCENTAGE -> Yoga.YGNodeStyleSetHeightPercent(this.nativeNode, height);
            case AUTO -> Yoga.YGNodeStyleSetHeightAuto(this.nativeNode);
        }
        return this;
    }

    public YogaNode styleSetMaxHeight(float height) {
        return this.styleSetMaxHeight(height, ExactPercentAuto.EXACT);
    }

    public YogaNode styleSetMaxHeight(float height, ExactPercentAuto unit) {
        switch (unit) {
            case EXACT -> Yoga.YGNodeStyleSetMaxHeight(this.nativeNode, height);
            case PERCENTAGE -> Yoga.YGNodeStyleSetMaxHeightPercent(this.nativeNode, height);
        }
        return this;
    }

    public YogaNode styleSetJustifyContent(JustifyContent justifyContent) {
        Yoga.YGNodeStyleSetJustifyContent(nativeNode, justifyContent.ordinal());
        return this;
    }

    public YogaNode styleSetAlignItems(AlignItems alignItems) {
        Yoga.YGNodeStyleSetAlignItems(nativeNode, alignItems.ordinal());
        return this;
    }

    public YogaNode styleSetFlexWrap(FlexWrap flexWrap) {
        Yoga.YGNodeStyleSetFlexWrap(nativeNode, flexWrap.ordinal());
        return this;
    }

    public YogaNode styleSetPosition(YogaEdge positionEdge, float position) {
        return this.styleSetPosition(positionEdge, position, ExactPercentAuto.EXACT);
    }

    public YogaNode styleSetPosition(YogaEdge positionEdge, float position, ExactPercentAuto unit) {
        switch (unit) {
            case EXACT -> Yoga.YGNodeStyleSetPosition(this.nativeNode, positionEdge.ordinal(), position);
            case PERCENTAGE -> Yoga.YGNodeStyleSetPositionPercent(this.nativeNode, positionEdge.ordinal(), position);
        }
        return this;
    }

    public YogaNode styleSetPositionType(PositionType type) {
        if (this.positionType != type) {
            Yoga.YGNodeStyleSetPositionType(nativeNode, type.ordinal());
        }
        this.positionType = type;
        return this;
    }

    public YogaNode styleSetDisplay(Display display) {
        if (this.display != display) {
            Yoga.YGNodeStyleSetDisplay(nativeNode, display.ordinal());
        }
        this.display = display;
        return this;
    }

    public YogaNode styleSetAlignSelf(AlignItems alignSelf) {
        Yoga.YGNodeStyleSetAlignSelf(nativeNode, alignSelf.ordinal());
        return this;
    }

    public YogaNode styleSetFlexGrow(float flexGrow) {
        Yoga.YGNodeStyleSetFlexGrow(nativeNode, flexGrow);
        return this;
    }

    public YogaNode styleSetFlexShrink(float shrink) {
        Yoga.YGNodeStyleSetFlexShrink(nativeNode, shrink);
        return this;
    }

    public YogaNode styleSetPadding(YogaEdge edge, float padding, ExactPercentAuto unit) {
        switch (unit) {
            case EXACT -> Yoga.YGNodeStyleSetPadding(this.nativeNode, edge.ordinal(), padding);
            case PERCENTAGE -> Yoga.YGNodeStyleSetPaddingPercent(this.nativeNode, edge.ordinal(), padding);
        }
        return this;
    }

    public YogaNode styleSetPadding(YogaEdge edge, float padding) {
        return this.styleSetPadding(edge, padding, ExactPercentAuto.EXACT);
    }

    public YogaNode styleSetMargin(YogaEdge edge, float margin, ExactPercentAuto unit) {
        switch (unit) {
            case EXACT -> Yoga.YGNodeStyleSetMargin(this.nativeNode, edge.ordinal(), margin);
            case PERCENTAGE -> Yoga.YGNodeStyleSetMarginPercent(this.nativeNode, edge.ordinal(), margin);
        }
        return this;
    }

    public YogaNode styleSetMargin(YogaEdge edge, float margin) {
        return this.styleSetMargin(edge, margin, ExactPercentAuto.EXACT);
    }

    public YogaNode styleSetBorder(YogaEdge edge, float border) {
        Yoga.YGNodeStyleSetBorder(nativeNode, edge.ordinal(), border);
        return this;
    }

    public YogaNode styleSetGap(GapGutter gutter, float gap, ExactPercentAuto unit) {
        switch (unit) {
            case EXACT -> Yoga.YGNodeStyleSetGap(this.nativeNode, gutter.ordinal(), gap);
            case PERCENTAGE -> Yoga.YGNodeStyleSetGapPercent(this.nativeNode, gutter.ordinal(), gap);
        }
        return this;
    }

    public YogaNode styleSetGap(GapGutter gutter, float gap) {
        return this.styleSetGap(gutter, gap, ExactPercentAuto.EXACT);
    }

    public void insertChild(YogaNode child, int atIndex) {
        YGNodeInsertChild(this.getNativeNode(), child.getNativeNode(), atIndex);
        children.add(atIndex, child);
        child.parent = this;
        child.addedAtIndex = atIndex;

        if (child.afterInitCallback != null) {
            child.afterInitCallback.run();
        }
    }

    public void removeChild(YogaNode child) {
        child.doRemoveCallback(0);

        YGNodeRemoveChild(this.getNativeNode(), child.getNativeNode());
        children.remove(child);
        child.parent = null;
    }

    public void removeChildAtIndex(int index) {
        YogaNode child = this.children.get(index);
        child.doRemoveCallback(0);

        YGNodeRemoveChild(this.getNativeNode(), child.getNativeNode());
        children.remove(index);
        child.parent = null;
    }

    public void removeAllChildren() {
        for (int i = 0; i < this.getChildren().size(); i++) {
            this.removeChildAtIndex(i);
            i--;
        }
    }

    private void doRemoveCallback(int ancestor) {
        if (this.removedCallback != null) {
            this.removedCallback.accept(ancestor);
        }
        for (YogaNode yogaNode : this.children) {
            yogaNode.doRemoveCallback(ancestor + 1);
        }
    }

    public void addChild(YogaNode child) {
        this.insertChild(child, children.size());
    }

    public YogaNode getChildAtIndex(int index) {
        return this.children.get(index);
    }

    public <T extends YogaNode> T getChildOfType(Class<T> type) {
        return (T) this.children.stream().filter(yogaNode -> yogaNode.getClass() == type).findFirst().orElse(null);//get or null
    }

    public void insertAbsoluteChild(YogaNode child, int atIndex) {
        YGNodeInsertChild(this.getNativeNode(), child.getNativeNode(), atIndex);
        absoluteChildren.add(atIndex, child);
        child.parent = this;
    }

    public void removeAbsoluteChild(YogaNode child) {
        YGNodeRemoveChild(this.getNativeNode(), child.getNativeNode());
        absoluteChildren.remove(child);
        child.parent = null;
    }

    public void addAbsoluteChild(YogaNode child) {
        this.insertAbsoluteChild(child, absoluteChildren.size());
    }

    public void enableVerticalScrollbar() {
        this.hasVerticalScrollbar = true;
        this.scrollbars.add(this.verticalScrollbar = new VerticalScrollbar());
        ((YogaNode) this.verticalScrollbar).parent = this;
    }

    public void enableHorizontalScrollbar() {
        this.hasHorizontalScrollbar = true;
        this.scrollbars.add(this.horizontalScrollbar = new HorizontalScrollbar());
        ((YogaNode) this.horizontalScrollbar).parent = this;
    }

    public float _relativeX() {
        return YGNodeLayoutGetLeft(nativeNode);
    }

    public float _relativeY() {
        return YGNodeLayoutGetTop(nativeNode);
    }


    protected float width() {
        return YGNodeLayoutGetWidth(nativeNode);
    }

    protected float height() {
        return YGNodeLayoutGetHeight(nativeNode);
    }

    public void setWidths(GuiGraphics context, float globalMouseX, float globalMouseY, float delta) {
        if (this.display == Display.NONE) {
            return;
        }

        width = width();
        height = height();
        if (widthModifier != null) {
            width = widthModifier.apply(width);
        }
        if (heightModifier != null) {
            height = heightModifier.apply(height);
        }

        for (YogaNode child : this.absoluteChildren) {
            child.setWidths(context, globalMouseX, globalMouseY, delta);
        }
        for (YogaNode child : this.scrollbars) {
            child.setWidths(context, globalMouseX, globalMouseY, delta);
        }
        for (YogaNode child : this.children) {
            child.setWidths(context, globalMouseX, globalMouseY, delta);
        }
    }

    public void setWidths(float currentX, float currentY, GuiGraphics context, float globalMouseX, float globalMouseY, float delta) {
        width = width();
        height = height();
        if (widthModifier != null) {
            width = widthModifier.apply(width);
        }
        if (heightModifier != null) {
            height = heightModifier.apply(height);
        }

        for (YogaNode child : this.absoluteChildren) {
            child.setWidths(currentX, currentY, context, globalMouseX, globalMouseY, delta);
        }
        for (YogaNode child : this.scrollbars) {
            child.setWidths(currentX, currentY, context, globalMouseX, globalMouseY, delta);
        }
        for (YogaNode child : this.children) {
            child.setWidths(currentX, currentY, context, globalMouseX, globalMouseY, delta);
        }
    }

    public void setGlobalPositions(float currentX, float currentY, GuiGraphics context, float globalMouseX, float globalMouseY, float delta) {
        if (this.display == Display.NONE) {
            return;
        }
        if (!this.hasSetPosition && this.beforeSetFirstPositionCallback != null) {
            this.beforeSetFirstPositionCallback.run();
        }
        relativeX = _relativeX();
        relativeY = _relativeY();
        hasSetPosition = true;

        currentX += relativeX;
        currentY += relativeY;
        globalX = currentX;
        globalY = currentY;

        for (YogaNode child : this.absoluteChildren) {
            child.setGlobalPositions(currentX, currentY, context, globalMouseX, globalMouseY, delta);
        }
        for (YogaNode child : this.scrollbars) {
            child.setGlobalPositions(currentX, currentY, context, globalMouseX, globalMouseY, delta);
        }

        currentX += this.childrenScrollXOffset;
        currentY += this.childrenScrollYOffset;

        for (YogaNode child : this.children) {
            child.setGlobalPositions(currentX, currentY, context, globalMouseX, globalMouseY, delta);
        }
    }

    public void beforeLayoutCalculations(GuiGraphics context, float mouseX, float mouseY, float delta, CanvasWrapper canvas) {

        if (!this.hasDoneFirstInit) {
            this.doRenderCallback(this.firstInitCallback, context, mouseX, mouseY, mouseX - this.getGlobalX(), mouseY - this.getGlobalY(), delta, canvas);
            this.hasDoneFirstInit = true;
        }
        this.doRenderCallback(this.preLayoutCalculationsCallback, context, mouseX, mouseY, mouseX - this.getGlobalX(), mouseY - this.getGlobalY(), delta, canvas);
        if (!this.isSearching) {
            boolean isVisible = this.implIsVisible();
            if (this.lastVisible != isVisible) {
                if (isVisible) {
                    this.styleSetDisplay(Display.FLEX);
                } else {
                    this.styleSetDisplay(Display.NONE);
                }
            }
            this.lastVisible = isVisible;
        }

        //if this isn't working move children to the bottom, 9/30/2023, remove this if it's working
        for (YogaNode child : this.children) {
            child.beforeLayoutCalculations(context, mouseX, mouseY, delta, canvas);
        }
        for (YogaNode child : this.absoluteChildren) {
            child.beforeLayoutCalculations(context, mouseX, mouseY, delta, canvas);
        }
        for (YogaNode child : this.scrollbars) {
            child.beforeLayoutCalculations(context, mouseX, mouseY, delta, canvas);
        }

    }

    public void beforeDraw(GuiGraphics context, float globalMouseX, float globalMouseY, float relativeMouseX, float relativeMouseY, float delta, CanvasWrapper canvas) {
        if (this.display == Display.NONE) {
            return;
        }

        if (this.hoverCallback != null) {
            if (showHoveredEffects && !wasHovered) {
                hoverCallback.accept(true);
            } else if (!showHoveredEffects && wasHovered) {
                hoverCallback.accept(false);
            }
        }
        wasHovered = showHoveredEffects;

        if (this.showHoveredEffects && this.tooltip != null) {
            if (this.shouldRenderTooltip == null ? true : this.shouldRenderTooltip.get()) {
                if (this.tooltipDelay != null) {
                    ModernClickGui.MODERN_CLICK_GUI.getTooltip().setTooltip(this, this.tooltip.get(), this.tooltipDelay.get());
                } else {
                    ModernClickGui.MODERN_CLICK_GUI.getTooltip().setTooltip(this, this.tooltip.get());
                }
            }
        }

        this.doRenderCallback(this.beforeRenderCallback, context, globalMouseX, globalMouseY, globalMouseX - this.getGlobalX(), globalMouseY - this.getGlobalY(), delta, canvas);


        for (YogaNode child : this.children) {
            child.beforeDraw(context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas);
        }
        for (YogaNode child : this.absoluteChildren) {
            child.beforeDraw(context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas);
        }
        for (YogaNode child : this.scrollbars) {
            child.beforeDraw(context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas);
        }

    }

    public YogaNode getHov(float globalMouseX, float globalMouseY) {

        boolean isHovered = this.isHovered(globalMouseX, globalMouseY);
        for (int i = this.children.size() - 1; i >= 0; i--) {
            YogaNode child = this.children.get(i);

            if (isHovered) {
                YogaNode hoveredChild = child.getHov(globalMouseX, globalMouseY);
                if (hoveredChild != null) {
                    return hoveredChild;
                }
            }
        }

        //TODO: does this mess stuff up? we previous weren't checking absolute children, I added this when I added hud button to pages
        for (int i = this.absoluteChildren.size() - 1; i >= 0; i--) {
            YogaNode child = this.absoluteChildren.get(i);

            if (isHovered) {
                YogaNode hoveredChild = child.getHov(globalMouseX, globalMouseY);
                if (hoveredChild != null) {
                    return hoveredChild;
                }
            }
        }

        return isHovered ? this : null;
    }

    public void reset(float globalMouseX, float globalMouseY) {
        this.showHoveredEffects = false;
        for (YogaNode child : this.children) {
            child.reset(globalMouseX, globalMouseY);
        }
        for (YogaNode child : this.absoluteChildren) {
            child.reset(globalMouseX, globalMouseY);
        }
    }


    public void draw(GuiGraphics context, float globalMouseX, float globalMouseY, float relativeMouseX, float relativeMouseY, float delta, CanvasWrapper canvas) {
        if (this.display == Display.NONE) {
            return;
        }

        canvas.save();

        canvas.translate(_relativeX(), _relativeY());
        relativeMouseX = globalMouseX - globalX;
        relativeMouseY = globalMouseY - globalY;

        if (debug) {
            canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), debugColor);
        }
        this.doRenderCallback(this.preRenderCallback, context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas);
        this.doRenderCallback(this.renderCallback, context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas);

        canvas.save();
        canvas.translate(this.childrenScrollXOffset, this.childrenScrollYOffset);
        if (shouldScissorChildren) {
            canvas.clipRect(childrenScrollXOffset * -1, childrenScrollYOffset * -1, this.getWidth(), this.getHeight());
        }
        this.doRenderCallback(this.preChildrenRenderCallback, context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas);
        for (YogaNode child : this.children) {
            if (isChildInRenderBounds(child) || debug) {
                child.draw(context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas);
            }
        }

        canvas.restore();
        for (YogaNode child : this.scrollbars) {
            child.draw(context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas);
        }
        for (YogaNode child : this.absoluteChildren) {
            child.draw(context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas);
        }

        canvas.restore();
    }

    public void globalMouseClicked(float mouseX, float mouseY, ButtonIdentifier button) {
        if (globalClickCallback != null) {
            globalClickCallback.handle(mouseX, mouseY, button, ClickType.CLICKED);
        }
        if (lastClickedButton == button && !this.isHoveredAndInsideParent(mouseX, mouseY)) {
            this.lastClickedButton = null;
        }

        for (int i = this.children.size() - 1; i >= 0; i--) {
            YogaNode child = this.children.get(i);
            child.globalMouseClicked(mouseX, mouseY, button);
        }
    }

    public void globalMouseReleased(float mouseX, float mouseY, ButtonIdentifier button) {
        if (globalClickCallback != null) {
            globalClickCallback.handle(mouseX, mouseY, button, ClickType.RELEASED);
        }

        for (YogaNode child : this.scrollbars) {
            child.globalMouseReleased(mouseX, mouseY, button);
        }

        for (int i = this.children.size() - 1; i >= 0; i--) {
            YogaNode child = this.children.get(i);
            child.globalMouseReleased(mouseX, mouseY, button);
        }
    }

    public boolean mouseClicked(float mouseX, float mouseY, ButtonIdentifier button) {
        if (this.display == Display.NONE) {
            return false;
        }
        float relativeMouseX = mouseX - this.getGlobalX();
        float relativeMouseY = mouseY - this.getGlobalY();
        boolean isHovered = this.isHoveredAndInsideParent(mouseX, mouseY) || (this.positionType == PositionType.ABSOLUTE && this.isHovered(mouseX, mouseY));
        if (isHovered) {
            this.lastClickedButton = button;
        }

        for (YogaNode child : this.absoluteChildren) {
            if (child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        for (YogaNode child : this.scrollbars) {
            if (child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        for (int i = this.children.size() - 1; i >= 0; i--) {
            YogaNode child = this.children.get(i);
            if (child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        if (isHovered && clickCallback != null) {
            if (clickCallback.handle(relativeMouseX, relativeMouseY, button, ClickType.CLICKED)) {
                return true;
            }
        }

        return false;
    }


    public boolean mouseReleased(float mouseX, float mouseY, ButtonIdentifier button) {
        if (this.display == Display.NONE) {
            return false;
        }

        float relativeMouseX = mouseX - this.getGlobalX();
        float relativeMouseY = mouseY - this.getGlobalY();
        boolean isHovered = this.isHoveredAndInsideParent(mouseX, mouseY) || (this.positionType == PositionType.ABSOLUTE && this.isHovered(mouseX, mouseY));

        for (YogaNode child : this.absoluteChildren) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }

        for (int i = this.children.size() - 1; i >= 0; i--) {
            YogaNode child = this.children.get(i);
            if (child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }

        if (isHovered && this.lastClickedButton == button && this.releaseClickCallback != null) {
            this.lastClickedButton = null;
            if (this.releaseClickCallback.handle(relativeMouseX, relativeMouseY, button, ClickType.CLICKED)) {
                return true;
            }
        }

        if (isHovered && clickCallback != null) {
            if (clickCallback.handle(relativeMouseX, relativeMouseY, button, ClickType.RELEASED)) {
                return true;
            }
        }

        return false;
    }

    public boolean mouseScrolled(float mouseX, float mouseY, float amount) {
        float relativeMouseX = mouseX - this.getGlobalX();
        float relativeMouseY = mouseY - this.getGlobalY();
        boolean isHovered = this.isHoveredAndInsideParent(mouseX, mouseY);

        if (this.hasVerticalScrollbar && verticalScrollbar != null && isHovered) {
            if (verticalScrollbar.scroll(amount)) {
                return true;
            }
        }

        if (this.hasHorizontalScrollbar && horizontalScrollbar != null && isHovered) {
            if (horizontalScrollbar.scroll(amount)) {
                return true;
            }
        }

        if (isHovered && scrollCallback != null) {
            if (scrollCallback.handle(relativeMouseX, relativeMouseY, amount)) {
                return true;
            }
        }

        for (int i = this.children.size() - 1; i >= 0; i--) {
            YogaNode child = this.children.get(i);
            if (child.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }
        }

        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (YogaNode child : this.absoluteChildren) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        for (YogaNode child : this.children) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        if (keyPressedCallback != null) {
            if (keyPressedCallback.handle(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        if (charTypedCallback != null) {
            if (charTypedCallback.handle(chr, modifiers)) {
                return true;
            }
        }

        for (YogaNode child : this.absoluteChildren) {
            if (child.charTyped(chr, modifiers)) {
                return true;
            }
        }

        for (YogaNode child : this.children) {
            if (child.charTyped(chr, modifiers)) {
                return true;
            }
        }
        return false;
    }

    protected boolean implIsVisible() {
        return true;
    }

    protected void doRenderCallback(RenderCallback renderCallback, GuiGraphics context, float globalMouseX, float globalMouseY, float relativeMouseX, float relativeMouseY, float delta, CanvasWrapper canvas) {
        if (renderCallback != null) {
            renderCallback.render(context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas);
        }
    }

    public boolean isChildInRenderBounds(YogaNode child) {
        float thisTop = this.getGlobalY();
        float thisBottom = thisTop + this.getHeight();

        float childTop = child.getGlobalY();
        float childBottom = childTop + child.getHeight();


        if (childBottom <= thisTop) {
            return false;
        } else if (childTop >= thisBottom) {
            return false;
        }


        if (this.parent != null) {
            return this.parent.isChildInRenderBounds(child);
        }

        return true;
    }

    public boolean isMouseInParentRenderBounds(float mouseX, float mouseY) {
        YogaNode parent = this.getParent();
        if (parent == null) {
            return true;
        }

        if (UIUtils.isHovered(mouseX, mouseY, parent.getGlobalX(), parent.getGlobalY(), parent.getWidth(), parent.getHeight())) {
            return parent.isMouseInParentRenderBounds(mouseX, mouseY);
        }
        return false;
    }

    public boolean isHovered(float globalMouseX, float globalMouseY) {
        return UIUtils.isHovered(globalMouseX, globalMouseY, this.getGlobalX(), this.getGlobalY(), this.getWidth(), this.getHeight());
    }

    public boolean isHoveredAndInsideParent(float globalMouseX, float globalMouseY) {
        return this.isHovered(globalMouseX, globalMouseY) && this.isMouseInParentRenderBounds(globalMouseX, globalMouseY);
    }

    public void setHoverCursorToIndicateClick() {
        this.setHoverCursor(CursorUtils.Cursor.HAND);
    }

    @Setter
    private boolean showHoveredEffects;
    private boolean wasHovered;

    public boolean shouldShowHoveredEffects() {
        return showHoveredEffects;
    }

    public void applyGenericStyle(GenericStyle genericStyle) {
        genericStyle.applyTo(this);
    }


    public interface RenderCallback {

        void render(GuiGraphics context, float globalMouseX, float globalMouseY, float relativeMouseX, float relativeMouseY, float delta, CanvasWrapper canvas);
    }

    public interface MouseCallback {

        boolean handle(float relativeMouseX, float relativeMouseY, ButtonIdentifier button, ClickType clickType);
    }

    public interface ScrollCallback {

        boolean handle(float relativeMouseX, float relativeMouseY, float amount);
    }

    public interface KeyPressedCallback {

        boolean handle(int keyCode, int scanCode, int modifiers);
    }

    public interface CharTypedCallback {

        boolean handle(char chr, int modifiers);
    }

}
