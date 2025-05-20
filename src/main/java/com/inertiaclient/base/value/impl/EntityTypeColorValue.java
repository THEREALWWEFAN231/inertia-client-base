package com.inertiaclient.base.value.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inertiaclient.base.value.Value;
import com.inertiaclient.base.value.WrappedColor;
import com.inertiaclient.base.value.group.ValueGroup;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;

public class EntityTypeColorValue extends Value<EntityTypeColorValue.EntitiesColorWrapper> {

    public EntityTypeColorValue(String id, ValueGroup parent, EntityTypeColorValue.EntitiesColorWrapper defaultValue) {
        super(id, parent, defaultValue.setChangeHandler(parent.getSaveHandler()));
    }

    public EntityTypeColorValue(String id, ValueGroup parent) {
        this(id, parent, getDefaultColors());
    }

    @Override
    protected EntityTypeColorValue.EntitiesColorWrapper copyDefaultValue(EntityTypeColorValue.EntitiesColorWrapper value) {
        return new EntitiesColorWrapper(value);
    }

    @Override
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        EntitiesColorWrapper value = this.getValue();

        JsonObject groupColors = new JsonObject();
        value.spawnGroupColors.forEach((spawnGroup, wrappedColor) -> groupColors.add(spawnGroup.getName(), wrappedColor.toJson()));
        jsonObject.add("Group Colors", groupColors);

        JsonObject entityColors = new JsonObject();
        value.entityTypesColor.forEach((entityType, wrappedColor) -> entityColors.add(EntityType.getId(entityType).toString(), wrappedColor.toJson()));
        jsonObject.add("Entity Colors", entityColors);

        return jsonObject;
    }

    @Override
    public void fromJson(JsonElement data) {
        EntitiesColorWrapper value = this.getValue();
        JsonObject main = data.getAsJsonObject();

        JsonObject groupColors = main.get("Group Colors").getAsJsonObject();
        groupColors.entrySet().forEach(entry -> {
            var spawnGroupOptional = Arrays.stream(SpawnGroup.values()).filter(spawnGroup -> spawnGroup.getName().equals(entry.getKey())).findFirst();
            if (spawnGroupOptional.isPresent()) {
                try {
                    WrappedColor wrappedColor = new WrappedColor(Color.white);
                    wrappedColor.fromJson(entry.getValue());

                    value.spawnGroupColors.put(spawnGroupOptional.get(), wrappedColor);
                } catch (Exception e) {
                }
            }
        });

        JsonObject entityColors = main.get("Entity Colors").getAsJsonObject();
        value.entityTypesColor.clear();
        entityColors.entrySet().forEach(entry -> {
            String entityId = entry.getKey();

            var entityType = EntityType.get(entityId);
            if (entityType.isPresent()) {
                try {
                    WrappedColor wrappedColor = new WrappedColor(Color.white);
                    wrappedColor.fromJson(entry.getValue());

                    value.entityTypesColor.put(entityType.get(), wrappedColor);
                } catch (Exception e) {
                }
            }
        });
    }

    public static EntitiesColorWrapper getDefaultColors() {
        var color = new EntitiesColorWrapper();
        color.setPassiveColor(new WrappedColor(Color.green));
        color.setHostileColor(new WrappedColor(new Color(255, 255, 0)));
        color.setEntityColor(EntityType.PLAYER, new WrappedColor(new Color(255, 0, 0)));
        color.setEntityColor(EntityType.ITEM, new WrappedColor(new Color(0, 255, 255), true));
        color.setEntityColor(EntityType.END_CRYSTAL, new WrappedColor(new Color(255, 0, 196)));

        return color;
    }

    public static class EntitiesColorWrapper {


        private HashMap<SpawnGroup, WrappedColor> spawnGroupColors = new HashMap<>();
        //Color can be null, if it is we are assumed to use the spawn group color
        private HashMap<EntityType<?>, WrappedColor> entityTypesColor = new HashMap<>();
        @Setter
        @Accessors(chain = true)
        private Runnable changeHandler;

        public EntitiesColorWrapper() {
            for (SpawnGroup spawnGroup : SpawnGroup.values()) {
                this.spawnGroupColors.put(spawnGroup, new WrappedColor(Color.white));
            }
        }

        public EntitiesColorWrapper(EntitiesColorWrapper copyFrom) {
            this.spawnGroupColors.putAll(copyFrom.spawnGroupColors);
            this.entityTypesColor.putAll(copyFrom.entityTypesColor);
            this.changeHandler = copyFrom.changeHandler;
        }

        public boolean isLinked(EntityType<?> entityType) {
            return !this.entityTypesColor.containsKey(entityType);
        }

        public void link(EntityType<?> remove) {
            this.entityTypesColor.remove(remove);
            this.runChangeHandler();
        }

        public WrappedColor getIndividualColor(EntityType<?> entityType) {
            return this.entityTypesColor.get(entityType);
        }

        public WrappedColor setIndividualColor(EntityType<?> entityType, WrappedColor wrappedColor) {
            var rv = this.entityTypesColor.put(entityType, wrappedColor);
            this.runChangeHandler();
            return rv;
        }

        public WrappedColor getSpawnGroupColor(SpawnGroup spawnGroup) {
            return this.spawnGroupColors.get(spawnGroup);
        }

        public EntitiesColorWrapper setSpawnGroupColor(SpawnGroup spawnGroup, WrappedColor wrappedColor) {
            this.spawnGroupColors.put(spawnGroup, wrappedColor);
            this.runChangeHandler();
            return this;
        }

        public EntitiesColorWrapper setPassiveColor(WrappedColor wrappedColor) {
            this.setSpawnGroupColor(SpawnGroup.CREATURE, wrappedColor);
            this.setSpawnGroupColor(SpawnGroup.AMBIENT, wrappedColor);
            this.setSpawnGroupColor(SpawnGroup.AXOLOTLS, wrappedColor);
            this.setSpawnGroupColor(SpawnGroup.UNDERGROUND_WATER_CREATURE, wrappedColor);
            this.setSpawnGroupColor(SpawnGroup.WATER_CREATURE, wrappedColor);
            this.setSpawnGroupColor(SpawnGroup.WATER_AMBIENT, wrappedColor);
            return this;
        }

        public EntitiesColorWrapper setHostileColor(WrappedColor wrappedColor) {
            this.setSpawnGroupColor(SpawnGroup.MONSTER, wrappedColor);
            return this;
        }

        public EntitiesColorWrapper setEntityColor(EntityType entityType, WrappedColor wrappedColor) {
            this.entityTypesColor.put(entityType, wrappedColor);
            this.runChangeHandler();
            return this;
        }

        public WrappedColor getColorForEntity(Entity entity) {
            return this.getColorForEntityType(entity.getType());
        }

        public WrappedColor getColorForEntityType(EntityType entityType) {
            WrappedColor individualColor = this.getIndividualColor(entityType);
            if (individualColor != null) {
                return individualColor;
            }
            return this.spawnGroupColors.get(entityType.getSpawnGroup());
        }

        private void runChangeHandler() {
            if (this.changeHandler != null) {
                this.changeHandler.run();
            }
        }
    }
}