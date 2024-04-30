package net.fortytoo.easyfind.easyfind.utils;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RegistryProvider {

    private static final Collection<String> ITEMS;

    static {
        List<String> items = new ArrayList<>();
        for (Item item : Registries.ITEM) {
            items.add(item.getTranslationKey());
        }
        ITEMS = Collections.unmodifiableCollection(items);
    }

    public static Collection<String> getItems() {
        return ITEMS;
    }
}
