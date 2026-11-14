package com.inertiaclient.base.value.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inertiaclient.base.value.Value;
import com.inertiaclient.base.value.WrappedColor;
import com.inertiaclient.base.value.group.ValueGroup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.HashMap;

//hashmap may not contain a BlockEntityType, you should use getColorFromHashMap or getColorForBlockEntity to get the color, not getValue
public class BlockEntityColorValue extends Value<HashMap<BlockEntityType<?>, WrappedColor>, BlockEntityColorValue.BlockEntityColorChangeListener> {

    public BlockEntityColorValue(String id, ValueGroup parent, HashMap<BlockEntityType<?>, WrappedColor> defaultValue) {
        super(id, parent, defaultValue);
    }

    public BlockEntityColorValue(String id, ValueGroup parent) {
        //super(id, parent, new HashMap<>());
        super(id, parent, getDefaultColors());
    }

    public static HashMap<BlockEntityType<?>, WrappedColor> getDefaultColors() {
        //TODO: select colors for rest of blocks, brewing stand, etable etc
        var defaultColors = new HashMap<BlockEntityType<?>, WrappedColor>();
        defaultColors.put(BlockEntityTypes.CHEST, new WrappedColor(new Color(181, 141, 0)));
        defaultColors.put(BlockEntityTypes.TRAPPED_CHEST, new WrappedColor(new Color(196, 57, 0)));
        defaultColors.put(BlockEntityTypes.ENDER_CHEST, new WrappedColor(new Color(126, 0, 126)));
        defaultColors.put(BlockEntityTypes.FURNACE, new WrappedColor(new Color(128, 128, 128)));
        defaultColors.put(BlockEntityTypes.DISPENSER, new WrappedColor(new Color(128, 128, 128)));
        defaultColors.put(BlockEntityTypes.DROPPER, new WrappedColor(new Color(128, 128, 128)));
        defaultColors.put(BlockEntityTypes.HOPPER, new WrappedColor(new Color(128, 128, 128)));
        defaultColors.put(BlockEntityTypes.PISTON, new WrappedColor(new Color(128, 128, 128)));
        defaultColors.put(BlockEntityTypes.SMOKER, new WrappedColor(new Color(128, 128, 128)));
        defaultColors.put(BlockEntityTypes.BLAST_FURNACE, new WrappedColor(new Color(128, 128, 128)));
        defaultColors.put(BlockEntityTypes.SHULKER_BOX, new WrappedColor(new Color(255, 175, 255)));//Color.pink
        defaultColors.put(BlockEntityTypes.BARREL, new WrappedColor(new Color(139, 69, 19)));
        defaultColors.put(BlockEntityTypes.COMMAND_BLOCK, new WrappedColor(new Color(199, 141, 106)));
        defaultColors.put(BlockEntityTypes.MOB_SPAWNER, new WrappedColor(new Color(36, 108, 156)));//27, 44, 55
        defaultColors.put(BlockEntityTypes.TRIAL_SPAWNER, new WrappedColor(new Color(79, 117, 133)));
        defaultColors.put(BlockEntityTypes.VAULT, new WrappedColor(new Color(33, 129, 170)));
        defaultColors.put(BlockEntityTypes.DECORATED_POT, new WrappedColor(new Color(160, 104, 44)));
        defaultColors.put(BlockEntityTypes.CAMPFIRE, new WrappedColor(new Color(162, 118, 73)));
        defaultColors.put(BlockEntityTypes.SIGN, new WrappedColor(new Color(150, 162, 88)));
        defaultColors.put(BlockEntityTypes.HANGING_SIGN, new WrappedColor(new Color(150, 162, 88)));
        defaultColors.put(BlockEntityTypes.BEACON, new WrappedColor(new Color(134, 221, 238)));
        defaultColors.put(BlockEntityTypes.BREWING_STAND, new WrappedColor(new Color(249, 255, 153)));
        defaultColors.put(BlockEntityTypes.ENCHANTING_TABLE, new WrappedColor(new Color(56, 63, 124)));
        defaultColors.put(BlockEntityTypes.JUKEBOX, new WrappedColor(new Color(113, 59, 51)));
        defaultColors.put(BlockEntityTypes.COMPARATOR, new WrappedColor(new Color(192, 122, 122)));
        defaultColors.put(BlockEntityTypes.STRUCTURE_BLOCK, new WrappedColor(new Color(66, 38, 133)));
        defaultColors.put(BlockEntityTypes.JIGSAW, new WrappedColor(new Color(66, 38, 133)));
        defaultColors.put(BlockEntityTypes.CONDUIT, new WrappedColor(new Color(140, 148, 116)));
        defaultColors.put(BlockEntityTypes.LECTERN, new WrappedColor(new Color(133, 96, 68)));
        defaultColors.put(BlockEntityTypes.BELL, new WrappedColor(new Color(186, 186, 59)));
        defaultColors.put(BlockEntityTypes.BEEHIVE, new WrappedColor(new Color(118, 92, 73)));
        defaultColors.put(BlockEntityTypes.CHISELED_BOOKSHELF, new WrappedColor(new Color(118, 92, 73)));
        defaultColors.put(BlockEntityTypes.SHELF, new WrappedColor(new Color(118, 92, 73)));
        defaultColors.put(BlockEntityTypes.POTENT_SULFUR, new WrappedColor(new Color(152, 152, 110)));
        defaultColors.put(BlockEntityTypes.COPPER_GOLEM_STATUE, new WrappedColor(new Color(128, 90, 42)));
        defaultColors.put(BlockEntityTypes.CRAFTER, new WrappedColor(new Color(133, 133, 102)));
        defaultColors.put(BlockEntityTypes.DAYLIGHT_DETECTOR, new WrappedColor(new Color(100, 100, 0)));
        defaultColors.put(BlockEntityTypes.SCULK_SENSOR, new WrappedColor(new Color(0, 56, 104)));
        defaultColors.put(BlockEntityTypes.CALIBRATED_SCULK_SENSOR, new WrappedColor(new Color(0, 56, 104)));
        defaultColors.put(BlockEntityTypes.SCULK_CATALYST, new WrappedColor(new Color(0, 56, 104)));
        defaultColors.put(BlockEntityTypes.SCULK_SHRIEKER, new WrappedColor(new Color(0, 56, 104)));
        defaultColors.put(BlockEntityTypes.BRUSHABLE_BLOCK, new WrappedColor(new Color(168, 168, 110)));
        return defaultColors;
    }

    @Override
    protected HashMap<BlockEntityType<?>, WrappedColor> copyDefaultValue(HashMap<BlockEntityType<?>, WrappedColor> value) {
        return new HashMap<>(value);
    }

    @Override
    protected void runChangeListenerForSetValue(HashMap<BlockEntityType<?>, WrappedColor> oldValue, HashMap<BlockEntityType<?>, WrappedColor> newValue) {
        //should probably check if the color has actually changed??!?!
        oldValue.forEach((blockEntityType, wrappedColor) -> {
            if (!newValue.containsKey(blockEntityType)) {
                //removed
                this.changeListener.onChange(blockEntityType, null);
            }
        });

        newValue.forEach((blockEntityType, wrappedColor) -> {
            //changed or added :shrug
            this.changeListener.onChange(blockEntityType, wrappedColor);
        });

    }

    public WrappedColor getColorFromHashMap(HashMap<BlockEntityType<?>, WrappedColor> value, BlockEntityType<?> blockEntityType) {
        return value.getOrDefault(blockEntityType, new WrappedColor(Color.white));//default color white
    }

    public WrappedColor getColorForBlockEntity(BlockEntityType<?> blockEntityType) {
        return this.getColorFromHashMap(this.getValue(), blockEntityType);
    }

    public void setColorForBlockEntity(BlockEntityType<?> blockEntityType, WrappedColor color) {
        this.getValue().put(blockEntityType, color);
        this.getParent().getSaveHandler().run();
        if (this.changeListener != null) {
            this.changeListener.onChange(blockEntityType, color);
        }
    }

    @Override
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        this.getValue().forEach((blockEntityType, wrappedColor) -> {
            jsonObject.add(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType).toString(), wrappedColor.toJson());
        });
        return jsonObject;
    }

    @Override
    public void fromJson(JsonElement data) {
        var oldValue = new HashMap<>(this.getValue());
        this.getValue().clear();

        JsonObject jsonObject = data.getAsJsonObject();
        jsonObject.entrySet().forEach(entry -> {
            try {
                BlockEntityType<?> blockEntityType = BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(Identifier.parse(entry.getKey()));
                WrappedColor wrappedColor = new WrappedColor(Color.white);
                wrappedColor.fromJson(entry.getValue());
                this.getValue().put(blockEntityType, wrappedColor);
            } catch (Exception e) {
            }
        });

        this.runChangeListenerForSetValue(oldValue, this.getValue());
    }

    public interface BlockEntityColorChangeListener {

        /**
         * @param change what was changed
         * @param color  its new color null if removed
         */
        void onChange(BlockEntityType<?> change, @Nullable WrappedColor color);
    }

}
