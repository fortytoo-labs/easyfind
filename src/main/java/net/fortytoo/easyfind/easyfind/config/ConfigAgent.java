package net.fortytoo.easyfind.easyfind.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class ConfigAgent extends MidnightConfig {
    public enum ReplaceNeighbor { CURRENT, NEXT, PREVIOUS }
    
    // Cosmetics
    @Entry public static boolean darkBG = true;
    @Entry public static boolean showDescription = true;
    @Entry public static boolean coloredRarity = true;
    
    // Behaviour
    @Entry public static ReplaceNeighbor replaceNeighbor = ReplaceNeighbor.CURRENT;
    @Entry public static boolean forcedReplace = false;
    @Entry public static boolean ignoreExisting = false;
    
    @Entry public static boolean saveHistory = true;
}
