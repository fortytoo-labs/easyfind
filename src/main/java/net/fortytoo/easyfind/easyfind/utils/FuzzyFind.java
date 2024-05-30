package net.fortytoo.easyfind.easyfind.utils;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import net.minecraft.item.Item;

import java.util.Collection;
import java.util.List;

public class FuzzyFind {
    public static List<BoundExtractedResult<Item>> search(Collection<Item> itemList, final String query) {
        return FuzzySearch.extractTop(
                query,
                itemList,
                Item::toString,
                24,
                50
        );
    }
}
