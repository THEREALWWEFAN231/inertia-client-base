package com.inertiaclient.base.hud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.utils.JsonState;
import com.inertiaclient.base.utils.LanguageBaseKey;
import com.inertiaclient.base.utils.UIUtils;
import com.inertiaclient.base.value.Value;
import com.inertiaclient.base.value.group.HudValueGroup;
import com.inertiaclient.base.value.impl.FloatValue;
import com.inertiaclient.base.value.impl.ModeValue;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;

public class HudGroup implements LanguageBaseKey, JsonState {

    @Getter
    @Setter
    private float leftAlignmentPercentage;
    @Getter
    @Setter
    private float middleXAlignmentPercentage;
    @Getter
    @Setter
    private float rightAlignmentPercentage;
    @Getter
    @Setter
    private float topAlignmentPercentage;
    @Getter
    @Setter
    private float middleYAlignmentPercentage;
    @Getter
    @Setter
    private float bottomAlignmentPercentage;
    @Getter
    @Setter
    private float x;
    @Getter
    @Setter
    private float y;
    @Getter
    @Setter
    private float width;
    @Getter
    @Setter
    private float height;
    @Getter
    @Setter
    private float scaledWidth;
    @Getter
    @Setter
    private float scaledHeight;


    @Getter
    private ArrayList<HudComponent> components = new ArrayList<HudComponent>();
    @Getter
    private ArrayList<HudComponent> enabledComponents = new ArrayList<HudComponent>();

    @Getter
    private HudValueGroup valuesGroup = new HudValueGroup("values_group");
    @Getter
    private FloatValue componentSpacing;

    @Getter
    private ModeValue componentXAlignment;
    @Getter
    private ModeValue componentYAlignment;
    @Getter
    @Setter
    private ModeValue.Mode componentsXAlignmentState = XAlignment.LEFT;
    @Getter
    @Setter
    private ModeValue.Mode componentsYAlignmentState = YAlignment.TOP;

    public HudGroup() {
        this.componentSpacing = new FloatValue("component_spacing", this.valuesGroup, 1, 0, 5);

        this.componentXAlignment = new ModeValue("component_x_alignment", this.valuesGroup, XAlignment.AUTOMATIC, XAlignment.class);
        this.componentYAlignment = new ModeValue("component_y_alignment", this.valuesGroup, YAlignment.AUTOMATIC, YAlignment.class);

        this.componentXAlignment.setChangeListener((oldValue, newValue) -> {
            if (newValue != XAlignment.AUTOMATIC) {
                componentsXAlignmentState = newValue;
            }
        });
        this.componentYAlignment.setChangeListener((oldValue, newValue) -> {
            if (newValue != YAlignment.AUTOMATIC) {
                componentsYAlignmentState = newValue;
            }
        });
    }

    public void setXPercentage(float x, float screenWidth) {
        this.leftAlignmentPercentage = x / screenWidth;
        this.middleXAlignmentPercentage = (x + (this.scaledWidth / 2)) / screenWidth;
        this.rightAlignmentPercentage = (x + this.scaledWidth) / screenWidth;
    }

    public void setYPercentage(float y, float screenHeight) {
        this.topAlignmentPercentage = y / screenHeight;
        this.middleYAlignmentPercentage = (y + (this.scaledHeight / 2)) / screenHeight;
        this.bottomAlignmentPercentage = (y + this.scaledHeight) / screenHeight;
    }

    public void setAutomaticAlignmentStates(float mouseX, float mouseY, float screenWidth, float screenHeight) {
        if (this.getComponentXAlignment().getValue() == HudGroup.XAlignment.AUTOMATIC) {
            if (mouseX < screenWidth * (1 / 3f)) {
                this.setComponentsXAlignmentState(HudGroup.XAlignment.LEFT);
            } else if (mouseX < screenWidth * (2 / 3f)) {
                this.setComponentsXAlignmentState(HudGroup.XAlignment.MIDDLE);
            } else {
                this.setComponentsXAlignmentState(HudGroup.XAlignment.RIGHT);
            }
        }

        if (this.getComponentYAlignment().getValue() == HudGroup.YAlignment.AUTOMATIC) {
            if (mouseY < screenHeight * (1 / 3f)) {
                this.setComponentsYAlignmentState(HudGroup.YAlignment.TOP);
            } else if (mouseY < screenHeight * (2 / 3f)) {
                this.setComponentsYAlignmentState(HudGroup.YAlignment.MIDDLE);
            } else {
                this.setComponentsYAlignmentState(HudGroup.YAlignment.BOTTOM);
            }
        }
    }

    public void doComponentsBeforeRender(float mouseX, float mouseY, float tickDelta, boolean editor, CanvasWrapper canvas) {
        this.enabledComponents.clear();
        for (int i = 0; i < this.components.size(); i++) {
            HudComponent component = this.components.get(i);
            if (!component.isEnabled()) {
                continue;
            }

            this.enabledComponents.add(component);
        }

        for (HudComponent component : this.enabledComponents) {
            component.beforeRender(editor, canvas);
        }
    }

    public void doStuff(float mouseX, float mouseY, float tickDelta) {
        this.width = 0;
        this.height = 0;
        this.scaledWidth = 0;
        this.scaledHeight = 0;

        for (int i = 0; i < this.enabledComponents.size(); i++) {
            HudComponent component = this.enabledComponents.get(i);

            float halfComponentSpacing = this.componentSpacing.getValue() / 2;
            boolean isFirstComponent = i == 0;
            boolean isLastComponent = i == this.components.size() - 1;

            float componentScale = component.getScale().getValue();
            float componentWidth = component.getWidth().get();
            float componentHeight = component.getHeight().get();
            float componentScaledWidth = componentWidth * componentScale;
            float componentScaledHeight = componentHeight * componentScale;
            component.setCachedWidth(componentWidth);
            component.setCachedHeight(componentHeight);
            component.setScaledWidth(componentScaledWidth);
            component.setScaledHeight(componentScaledHeight);

            this.width = Math.max(this.width, componentWidth);
            this.height += componentHeight;
            this.scaledWidth = Math.max(this.scaledWidth, componentScaledWidth);
            this.scaledHeight += componentScaledHeight + component.getTopPadding().getValue() + component.getBottomPadding().getValue();

            if (!isFirstComponent && !isLastComponent) {
                //this.scaledHeight += componentSpacing.getValue();
            }
        }
        scaledHeight += (enabledComponents.size() - 1) * componentSpacing.getValue();
    }

    public void renderGroup(DrawContext drawContext, float mouseX, float mouseY, float tickDelta, boolean editor, CanvasWrapper canvas) {
        canvas.save();

        float iteratingYOffset = 0;
        for (int i = 0; i < this.enabledComponents.size(); i++) {
            HudComponent component = this.enabledComponents.get(i);

            float halfComponentSpacing = this.componentSpacing.getValue() / 2;
            boolean isFirstComponent = i == 0;
            boolean isLastComponent = i == this.components.size() - 1;

            iteratingYOffset += component.getTopPadding().getValue();
            if (!isFirstComponent) {
                iteratingYOffset += halfComponentSpacing;
            }

            canvas.save();
            float scale = component.getScale().getValue();

            float xOffset = 0;
            if (this.getComponentsXAlignmentState() == XAlignment.MIDDLE) {
                xOffset = (this.getScaledWidth() - component.getScaledWidth()) / 2;
            } else if (this.getComponentsXAlignmentState() == HudGroup.XAlignment.RIGHT) {
                xOffset = this.getScaledWidth() - component.getScaledWidth();
            }

            float xTranslate = this.x + xOffset;
            float yTranslate = this.y + iteratingYOffset;
            if (component.shouldDoScaleOnRender()) {
                canvas.translate(xTranslate, yTranslate);
                canvas.scale(scale, scale);
                canvas.translate(-xTranslate, -yTranslate);
            }

            canvas.translate(xTranslate, yTranslate);
            component.render(drawContext, editor, canvas);
            //render over, for stuff, like inventory that is almost not transparent
            if (InertiaBase.instance.getHudManager().getHighlightedComponent() == component) {
                canvas.drawRect(0, 0, component.getCachedWidth(), component.getCachedHeight(), new Color(255, 255, 255, 150));
            }
            canvas.restore();

            iteratingYOffset += component.getScaledHeight();
            iteratingYOffset += component.getBottomPadding().getValue();
            if (!isLastComponent) {
                iteratingYOffset += halfComponentSpacing;
            }
        }
        canvas.restore();
    }

    public void removeComponent(HudComponent hudComponent) {
        this.components.remove(hudComponent);
        if (this.components.isEmpty()) {
            InertiaBase.instance.getHudManager().getGroups().remove(this);
        }
        hudComponent.setGroup(null);
    }

    public void addComponentsFromGroup(HudGroup add, int atIndex) {
        for (int i = 0; i < add.components.size(); i++) {
            HudComponent hudComponent = add.components.get(i);

            this.addComponent(hudComponent, atIndex);
            atIndex++;//if we are moving two or more objects, add after the component we just added
            i--;//when addToGroup calls removeGroup, it removes the component, so we iterate down one
        }
    }

    public void addComponentsFromGroup(HudGroup add) {
        addComponentsFromGroup(add, this.components.size());
    }

    public HudComponent addComponent(HudComponent add, int atIndex) {
        if (add.getGroup() != null) {
            add.getGroup().removeComponent(add);
        }

        this.components.add(atIndex, add);
        add.setGroup(this);
        return add;
    }

    public HudComponent addComponent(HudComponent add) {
        return this.addComponent(add, this.components.size());
    }

    public boolean isHovered(float mouseX, float mouseY) {
        return UIUtils.isHovered(mouseX, mouseY, this.x, this.y, this.scaledWidth, this.scaledHeight);
    }

    public void copyPropertiesFrom(HudGroup copyFrom) {

        for (Value<?> value : this.valuesGroup.getValues()) {
            //TODO: map by name
            for (Value<?> copyFromIteratingValue : copyFrom.valuesGroup.getValues()) {
                if (value.getId().equals(copyFromIteratingValue.getId())) {
                    //:flushed: :pensive: we could just use raw type but that's no fun, on the real, I spent like 3+ hours trying to figure out how to not do this and found nothing/:, changing properties to Object instead of ?, works but causes other "issues" I don't want
                    Value<Object> firstHudPropertyObjectType = (Value<Object>) value;
                    Value<Object> copyFromHudPropertyObjectType = (Value<Object>) copyFromIteratingValue;

                    firstHudPropertyObjectType.setValue(copyFromHudPropertyObjectType.getValue());
                    break;
                }
            }
        }

        this.setComponentsXAlignmentState(copyFrom.getComponentsXAlignmentState());
        this.setComponentsYAlignmentState(copyFrom.getComponentsYAlignmentState());
    }

    public int getHudComponentIndexAtY(float y) {
        float iteratingY = 0;
        for (int i = 0; i < this.enabledComponents.size(); i++) {
            HudComponent component = this.enabledComponents.get(i);

            float clickHeight = this.getHeightForComponent(i, component);
            if (y >= iteratingY && y < iteratingY + clickHeight) {
                return i;
            }
            iteratingY += clickHeight;
        }
        return -1;
    }

    public float getHeightForIndex(int index) {

        if (index == 0) {
            return 0;
        } else if (index == this.enabledComponents.size()) {
            return this.getScaledHeight();
        }

        float iteratingY = 0;
        for (int i = 0; i < this.enabledComponents.size(); i++) {
            HudComponent component = this.enabledComponents.get(i);

            if (i == index) {
                return iteratingY;
            }
            iteratingY += this.getHeightForComponent(i, component);
        }
        return 0;
    }

    public double getScaleHeightOfAllComponents() {
        double scaledHeight = 0;
        for (int i = 0; i < this.enabledComponents.size(); i++) {
            HudComponent component = this.enabledComponents.get(i);

            scaledHeight += this.getHeightForComponent(i, component);
        }
        return scaledHeight;
    }

    //gets the component height with padding and all that, generally used for when the user clicks on a component
    //so that when they click the padding of a component they actually click the component, not "air"/nothing
    public float getHeightForComponent(int componentIndex, HudComponent component) {
        float height = 0;

        float halfComponentSpacing = this.componentSpacing.getValue() / 2;
        boolean isFirstComponent = componentIndex == 0;
        boolean isLastComponent = componentIndex == this.components.size() - 1;

        if (!isFirstComponent) {
            height += halfComponentSpacing;
        }
        height += component.getTopPadding().getValue() + component.getScaledHeight() + component.getBottomPadding().getValue();
        if (!isLastComponent) {
            height += halfComponentSpacing;
        }

        return height;
    }

    @Override
    public String getLanguageBaseKey() {
        return "icb.hudgroup";
    }

    @Override
    public JsonElement toJson() {

        JsonObject jsonObject = new JsonObject();

        {
            JsonObject alignmentPercentages = new JsonObject();
            jsonObject.add("Alignment Percentages", alignmentPercentages);

            alignmentPercentages.addProperty("Left", this.leftAlignmentPercentage);
            alignmentPercentages.addProperty("XMiddle", this.middleXAlignmentPercentage);
            alignmentPercentages.addProperty("Right", this.rightAlignmentPercentage);
            alignmentPercentages.addProperty("Top", this.topAlignmentPercentage);
            alignmentPercentages.addProperty("YMiddle", this.middleYAlignmentPercentage);
            alignmentPercentages.addProperty("Bottom", this.bottomAlignmentPercentage);
        }

        jsonObject.addProperty("X Alignment State", this.componentsXAlignmentState.getId());
        jsonObject.addProperty("Y Alignment State", this.componentsYAlignmentState.getId());

        jsonObject.add("Values Group", this.valuesGroup.toJson());

        JsonObject components = new JsonObject();
        jsonObject.add("Components", components);
        for (HudComponent hudComponent : this.components) {
            components.add(hudComponent.getId(), hudComponent.toJson());
        }

        return jsonObject;
    }

    @Override
    public void fromJson(JsonElement data) {
        JsonObject main = data.getAsJsonObject();

        {
            JsonObject alignmentPercentages = main.getAsJsonObject("Alignment Percentages");
            this.leftAlignmentPercentage = alignmentPercentages.get("Left").getAsFloat();
            this.middleXAlignmentPercentage = alignmentPercentages.get("XMiddle").getAsFloat();
            this.rightAlignmentPercentage = alignmentPercentages.get("Right").getAsFloat();
            this.topAlignmentPercentage = alignmentPercentages.get("Top").getAsFloat();
            this.middleYAlignmentPercentage = alignmentPercentages.get("YMiddle").getAsFloat();
            this.bottomAlignmentPercentage = alignmentPercentages.get("Bottom").getAsFloat();
        }

        this.componentsXAlignmentState = this.componentXAlignment.getModeFromString(main.get("X Alignment State").getAsString());
        this.componentsYAlignmentState = this.componentYAlignment.getModeFromString(main.get("Y Alignment State").getAsString());

        this.valuesGroup.fromJson(main.getAsJsonObject("Values Group"));

        JsonObject components = main.getAsJsonObject("Components");
        for (Map.Entry<String, JsonElement> entry : components.asMap().entrySet()) {
            String componentId = entry.getKey();

            var foundComponentOptional = InertiaBase.instance.getHudManager().getComponents().stream().filter(hudComponent -> hudComponent.getId().equals(componentId)).findFirst();
            foundComponentOptional.ifPresent(hudComponent -> {
                this.addComponent(hudComponent);
                hudComponent.fromJson(entry.getValue());
            });
        }

    }

    public static class XAlignment {

        public static final ModeValue.Mode AUTOMATIC = new ModeValue.Mode("automatic");
        public static final ModeValue.Mode LEFT = new ModeValue.Mode("left");
        public static final ModeValue.Mode MIDDLE = new ModeValue.Mode("middle");
        public static final ModeValue.Mode RIGHT = new ModeValue.Mode("right");
    }

    public static class YAlignment {

        public static final ModeValue.Mode AUTOMATIC = new ModeValue.Mode("automatic");
        public static final ModeValue.Mode TOP = new ModeValue.Mode("top");
        public static final ModeValue.Mode MIDDLE = new ModeValue.Mode("middle");
        public static final ModeValue.Mode BOTTOM = new ModeValue.Mode("bottom");
    }


}
