package net.fortytoo.easyfind.easyfind.utils;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RegistryProvider {

    private static final Collection<Item> ITEMS;

    // TODO: Hide item that is a part of experimental features.
    //  See FeatureSet -> ToggleableFeature
    static {
        List<Item> items = new ArrayList<>();
        for (Item item : Registries.ITEM) {
            items.add(item);
        }
        ITEMS = Collections.unmodifiableCollection(items);
    }

    public static Collection<Item> getItems() {
        return ITEMS;
    }
}
