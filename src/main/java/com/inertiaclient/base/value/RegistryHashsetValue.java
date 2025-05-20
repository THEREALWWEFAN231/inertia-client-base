package com.inertiaclient.base.value;

import com.inertiaclient.base.value.group.ValueGroup;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public abstract class RegistryHashsetValue<T> extends HashsetValue<T> {

    private Registry<T> registry;

    public RegistryHashsetValue(String id, ValueGroup parent, HashSet<T> defaultValue) {
        super(id, parent, defaultValue);
    }

    public RegistryHashsetValue(String id, ValueGroup parent, Collection<T> defaultValue) {
        super(id, parent, defaultValue);
    }

    public RegistryHashsetValue(String id, ValueGroup parent, T... defaultValue) {
        super(id, parent, defaultValue);
    }

    public RegistryHashsetValue(String id, ValueGroup parent) {
        super(id, parent);
    }

    public void _afterConstructor() {
        super._afterConstructor();
        this.registry = this.getRegistry();
    }


    protected abstract Registry<T> getRegistry();

    @Override
    public String getKeyFromEntry(T entry) {
        return this.registry.getId(entry).toString();
    }

    @Override
    public T getEntryFromKey(String entryKey) {
        return this.registry.getOptionalValue(Identifier.of(entryKey)).get();
    }

    @Override
    public List<T> getAllPossibleEntries() {
        return this.registry.stream().toList();
    }

    @Override
    public Stream<T> getAllPossibleEntriesStream() {
        return this.registry.stream();
    }
}
