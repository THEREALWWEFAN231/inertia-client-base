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

import java.awt.Color;
import java.util.HashMap;

//hashmap may not contain a BlockEntityType, you should use getColorFromHashMap or getColorForBlockEntity to get the color, not getValue
public class BlockEntityColorValue extends Value<HashMap<BlockEntityType<?>, WrappedColor>> {

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
        defaultColors.put(BlockEntityTypes.CHEST, new WrappedColor(new Color(255, 200, 0)));
        defaultColors.put(BlockEntityTypes.TRAPPED_CHEST, new WrappedColor(Color.red));
        defaultColors.put(BlockEntityTypes.ENDER_CHEST, new WrappedColor(new Color(255, 0, 255)));
        defaultColors.put(BlockEntityTypes.FURNACE, new WrappedColor(new Color(128, 128, 128)));
        defaultColors.put(BlockEntityTypes.DISPENSER, new WrappedColor(new Color(128, 128, 128)));
        defaultColors.put(BlockEntityTypes.DROPPER, new WrappedColor(new Color(128, 128, 128)));
        defaultColors.put(BlockEntityTypes.HOPPER, new WrappedColor(new Color(128, 128, 128)));
        defaultColors.put(BlockEntityTypes.SHULKER_BOX, new WrappedColor(new Color(255, 175, 255)));//Color.pink
        defaultColors.put(BlockEntityTypes.BARREL, new WrappedColor(new Color(139, 69, 19)));
        defaultColors.put(BlockEntityTypes.COMMAND_BLOCK, new WrappedColor(new Color(199, 141, 106)));
        defaultColors.put(BlockEntityTypes.MOB_SPAWNER, new WrappedColor(new Color(36, 108, 156)));//27, 44, 55
        defaultColors.put(BlockEntityTypes.TRIAL_SPAWNER, new WrappedColor(new Color(79, 117, 133)));
        return defaultColors;
    }

    @Override
    protected HashMap<BlockEntityType<?>, WrappedColor> copyDefaultValue(HashMap<BlockEntityType<?>, WrappedColor> value) {
        return new HashMap<>(value);
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
    }

}
