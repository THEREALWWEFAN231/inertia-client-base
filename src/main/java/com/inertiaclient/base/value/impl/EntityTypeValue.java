package com.inertiaclient.base.value.impl;

import com.inertiaclient.base.utils.CollectionUtils;
import com.inertiaclient.base.value.RegistryHashsetValue;
import com.inertiaclient.base.value.group.ValueGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.*;

public class EntityTypeValue extends RegistryHashsetValue<EntityType<?>> {

    public static HashMap<SpawnGroup, ArrayList<EntityType<?>>> entityTypesBySpawnGroup;

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
        return Registries.ENTITY_TYPE;
    }

    public void addAllInGroup(SpawnGroup spawnGroup) {
        for (EntityType entityType : Registries.ENTITY_TYPE.stream().toList()) {
            if (entityType.getSpawnGroup() == spawnGroup) {
                this.addHard(entityType);
            }
        }
    }

    public void removeAllInGroup(SpawnGroup spawnGroup) {
        for (EntityType entityType : Registries.ENTITY_TYPE.stream().toList()) {
            if (entityType.getSpawnGroup() == spawnGroup) {
                this.removeHard(entityType);
            }
        }
    }

    public boolean isAllInGroupSelected(SpawnGroup spawnGroup) {
        for (EntityType entityType : Registries.ENTITY_TYPE.stream().toList()) {
            if (entityType.getSpawnGroup() == spawnGroup && !this.getValue().contains(entityType)) {
                return false;
            }
        }
        return true;
    }

    public static HashSet<EntityType<?>> getLivingEntities() {
        HashSet<EntityType<?>> entities = new HashSet<>();

        for (EntityType<?> entityType : Registries.ENTITY_TYPE.stream().toList()) {
            if (entityType.getSpawnGroup() != SpawnGroup.MISC) {
                entities.add(entityType);
            }
        }

        entities.add(EntityType.ARMOR_STAND);
        entities.add(EntityType.IRON_GOLEM);
        entities.add(EntityType.SNOW_GOLEM);
        entities.add(EntityType.VILLAGER);
        entities.add(EntityType.PLAYER);

        return entities;
    }

    public static HashSet<EntityType<?>> getMinecarts() {
        HashSet<EntityType<?>> entities = new HashSet<>();

        entities.add(EntityType.MINECART);
        entities.add(EntityType.TNT_MINECART);
        entities.add(EntityType.CHEST_MINECART);
        entities.add(EntityType.HOPPER_MINECART);
        entities.add(EntityType.COMMAND_BLOCK_MINECART);
        entities.add(EntityType.SPAWNER_MINECART);

        return entities;
    }

    public static HashSet<EntityType<?>> getBoats() {
        HashSet<EntityType<?>> entities = new HashSet<>();

        entities.add(EntityType.ACACIA_BOAT);
        entities.add(EntityType.ACACIA_CHEST_BOAT);
        entities.add(EntityType.BIRCH_BOAT);
        entities.add(EntityType.BIRCH_CHEST_BOAT);
        entities.add(EntityType.CHERRY_BOAT);
        entities.add(EntityType.CHERRY_CHEST_BOAT);
        entities.add(EntityType.DARK_OAK_BOAT);
        entities.add(EntityType.DARK_OAK_CHEST_BOAT);
        entities.add(EntityType.JUNGLE_BOAT);
        entities.add(EntityType.JUNGLE_CHEST_BOAT);
        entities.add(EntityType.MANGROVE_BOAT);
        entities.add(EntityType.MANGROVE_CHEST_BOAT);
        entities.add(EntityType.OAK_BOAT);
        entities.add(EntityType.OAK_CHEST_BOAT);
        entities.add(EntityType.PALE_OAK_BOAT);
        entities.add(EntityType.PALE_OAK_CHEST_BOAT);
        entities.add(EntityType.SPRUCE_BOAT);
        entities.add(EntityType.SPRUCE_CHEST_BOAT);

        return entities;
    }

    public static HashSet<EntityType<?>> getInertiaMisc() {
        HashSet<EntityType<?>> entities = new HashSet<>();

        entities.add(EntityType.FIREWORK_ROCKET);
        entities.add(EntityType.ITEM_FRAME);
        entities.add(EntityType.GLOW_ITEM_FRAME);
        entities.add(EntityType.ITEM);
        entities.add(EntityType.BLOCK_DISPLAY);
        entities.add(EntityType.ITEM_DISPLAY);
        entities.add(EntityType.PAINTING);
        entities.add(EntityType.SNOWBALL);
        entities.add(EntityType.TNT);

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
        for (EntityType entityType : Registries.ENTITY_TYPE.stream().toList()) {
            CollectionUtils.addToArrayListHashMap(entityTypesBySpawnGroup, entityType.getSpawnGroup(), entityType);
        }
    }

}
