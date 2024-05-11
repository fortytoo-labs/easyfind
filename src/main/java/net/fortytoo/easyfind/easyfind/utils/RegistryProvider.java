package net.fortytoo.easyfind.easyfind.utils;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RegistryProvider {
    private static final Collection<Item> ITEMS;
    
    static {
        List<Item> items = new ArrayList<>();
        for (Item item : Registry.ITEM) {
            items.add(item);
        }
        ITEMS = Collections.unmodifiableCollection(items);
    }

    public static Collection<Item> getItems() {
        return ITEMS;
    }
}
