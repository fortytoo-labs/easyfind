package net.fortytoo.easyfind.easyfind.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class ConfigAgent extends MidnightConfig {
    public enum ReplaceNeighbor { CURRENT, NEXT, PREVIOUS }
    
    // Cosmetics
    @Entry(category = "text") public static boolean blurredBG = true;
    @Entry(category = "text") public static boolean showDescription = true;
    @Entry(category = "text") public static boolean coloredRarity = true;
    
    // Behaviour
    @Entry(category = "text") public static ReplaceNeighbor replaceNeighbor = ReplaceNeighbor.CURRENT;
    @Comment(category = "text") public static Comment replaceNeighborDescription;
    @Entry(category = "text") public static boolean forcedReplace = false;
    @Comment(category = "text") public static Comment forcedReplaceDescription;
    
    @Entry(category = "text") public static boolean saveHistory = true;
    @Entry(category = "text") public static boolean showDisabledItem;
    
    // TBA
    public static boolean creativeHotbarAgent;
}
