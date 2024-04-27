package net.fortytoo.easyfind.easyfind.utils;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class RegistryProvider {
    private final Collection<String> items = new ArrayList<>();

    public RegistryProvider() {
        // FIXME: Any better approach here?
        load();
    }

    public Collection<String> getItems() {
        return Collections.unmodifiableCollection(items);
    }

    // TODO
    private void load() {
        items.clear();
        for (Item item : Registries.ITEM) {
            items.add(item.getTranslationKey());
        }
    }
}
