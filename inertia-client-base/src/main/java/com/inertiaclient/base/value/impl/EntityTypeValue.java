package com.inertiaclient.base.value.impl;

import com.inertiaclient.base.utils.CollectionUtils;
import com.inertiaclient.base.value.RegistryHashsetValue;
import com.inertiaclient.base.value.group.ValueGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;

import java.util.*;

public class EntityTypeValue extends RegistryHashsetValue<EntityType<?>> {

    public static HashMap<MobCategory, ArrayList<EntityType<?>>> entityTypesBySpawnGroup;

    public EntityTypeValue(String id, ValueGroup parent, HashSet<EntityType<?>> defaultValue) {
        super(id, parent, defaultValue);
    }

    public EntityTypeValue(String id, ValueGroup parent, Collection<EntityType<?>> defaultValue) {
        super(id, parent, defaultValue);
    }

    public EntityTypeValue(String id, ValueGroup parent, EntityType<?>... defaultValue) {
        super(id, parent, defaultValue);
    }

    public EntityTypeValue(String id, ValueGroup parent) {
        super(id, parent);
    }

    @Override
    protected Registry<EntityType<?>> getRegistry() {
        return BuiltInRegistries.ENTITY_TYPE;
    }

    public void addAllInGroup(MobCategory spawnGroup) {
        for (EntityType entityType : BuiltInRegistries.ENTITY_TYPE.stream().toList()) {
            if (entityType.getCategory() == spawnGroup) {
                this.addHard(entityType);
            }
        }
    }

    public void removeAllInGroup(MobCategory spawnGroup) {
        for (EntityType entityType : BuiltInRegistries.ENTITY_TYPE.stream().toList()) {
            if (entityType.getCategory() == spawnGroup) {
                this.removeHard(entityType);
            }
        }
    }

    public boolean isAllInGroupSelected(MobCategory spawnGroup) {
        for (EntityType entityType : BuiltInRegistries.ENTITY_TYPE.stream().toList()) {
            if (entityType.getCategory() == spawnGroup && !this.getValue().contains(entityType)) {
                return false;
            }
        }
        return true;
    }

    public static HashSet<EntityType<?>> getLivingEntities() {
        HashSet<EntityType<?>> entities = new HashSet<>();

        for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE.stream().toList()) {
            if (entityType.getCategory() != MobCategory.MISC) {
                entities.add(entityType);
            }
        }

        entities.add(EntityTypes.ARMOR_STAND);
        entities.add(EntityTypes.IRON_GOLEM);
        entities.add(EntityTypes.SNOW_GOLEM);
        entities.add(EntityTypes.VILLAGER);
        entities.add(EntityTypes.PLAYER);

        return entities;
    }

    public static HashSet<EntityType<?>> getMinecarts() {
        HashSet<EntityType<?>> entities = new HashSet<>();

        entities.add(EntityTypes.MINECART);
        entities.add(EntityTypes.TNT_MINECART);
        entities.add(EntityTypes.CHEST_MINECART);
        entities.add(EntityTypes.HOPPER_MINECART);
        entities.add(EntityTypes.COMMAND_BLOCK_MINECART);
        entities.add(EntityTypes.SPAWNER_MINECART);

        return entities;
    }

    public static HashSet<EntityType<?>> getBoats() {
        HashSet<EntityType<?>> entities = new HashSet<>();

        entities.add(EntityTypes.ACACIA_BOAT);
        entities.add(EntityTypes.ACACIA_CHEST_BOAT);
        entities.add(EntityTypes.BIRCH_BOAT);
        entities.add(EntityTypes.BIRCH_CHEST_BOAT);
        entities.add(EntityTypes.CHERRY_BOAT);
        entities.add(EntityTypes.CHERRY_CHEST_BOAT);
        entities.add(EntityTypes.DARK_OAK_BOAT);
        entities.add(EntityTypes.DARK_OAK_CHEST_BOAT);
        entities.add(EntityTypes.JUNGLE_BOAT);
        entities.add(EntityTypes.JUNGLE_CHEST_BOAT);
        entities.add(EntityTypes.MANGROVE_BOAT);
        entities.add(EntityTypes.MANGROVE_CHEST_BOAT);
        entities.add(EntityTypes.OAK_BOAT);
        entities.add(EntityTypes.OAK_CHEST_BOAT);
        entities.add(EntityTypes.PALE_OAK_BOAT);
        entities.add(EntityTypes.PALE_OAK_CHEST_BOAT);
        entities.add(EntityTypes.SPRUCE_BOAT);
        entities.add(EntityTypes.SPRUCE_CHEST_BOAT);

        return entities;
    }

    public static HashSet<EntityType<?>> getInertiaMisc() {
        HashSet<EntityType<?>> entities = new HashSet<>();

        entities.add(EntityTypes.FIREWORK_ROCKET);
        entities.add(EntityTypes.ITEM_FRAME);
        entities.add(EntityTypes.GLOW_ITEM_FRAME);
        entities.add(EntityTypes.ITEM);
        entities.add(EntityTypes.BLOCK_DISPLAY);
        entities.add(EntityTypes.ITEM_DISPLAY);
        entities.add(EntityTypes.PAINTING);
        entities.add(EntityTypes.SNOWBALL);
        entities.add(EntityTypes.TNT);

        return entities;
    }

    public boolean isTargeted(EntityType<?> entityType) {
        return this.getValue().contains(entityType);
    }

    public boolean isTargeted(Entity entity) {
        return this.isTargeted(entity.getType());
    }

    static {
        entityTypesBySpawnGroup = new HashMap<>();
        for (EntityType entityType : BuiltInRegistries.ENTITY_TYPE.stream().toList()) {
            CollectionUtils.addToArrayListHashMap(entityTypesBySpawnGroup, entityType.getCategory(), entityType);
        }
    }

}
