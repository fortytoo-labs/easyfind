package net.fortytoo.easyfind.easyfind.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class ConfigAgent extends MidnightConfig {
    public enum ReplaceNeighbor { CURRENT, NEXT, PREVIOUS }
    
    // Cosmetics
    @Entry(category = "text") public static boolean darkenBG = true;
    @Entry(category = "text") public static boolean showDescription = true;
    @Entry(category = "text") public static boolean coloredRarity = true;
    
    // Behaviour
    @Entry(category = "text") public static ReplaceNeighbor replaceNeighbor = ReplaceNeighbor.CURRENT;
    @Entry(category = "text") public static boolean forcedReplace = false;
    @Entry(category = "text")
    public static boolean ignoreExisting = false;
    
    @Entry(category = "text") public static boolean saveHistory = true;
    @Entry(category = "text") public static boolean showDisabledItem;
}
