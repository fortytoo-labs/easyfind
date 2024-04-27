package net.fortytoo.easyfind.easyfind.utils;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

import java.util.Collection;
import java.util.List;

public class FuzzyFind {
    public static List<ExtractedResult> search(Collection<String> itemList, final String query) {
        return FuzzySearch.extractTop(
                query,
                itemList,
                10,
                10
        );
    }
}
