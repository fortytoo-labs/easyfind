package net.fortytoo.easyfind.easyfind;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fortytoo.easyfind.easyfind.config.ConfigAgent;
import net.fortytoo.easyfind.easyfind.utils.LogUtil;

public class EasyFind implements ModInitializer {
    @Override
    public void onInitialize() {
        MidnightConfig.init("easyfind", ConfigAgent.class);
        LogUtil.info("Welcome to EasyFind!");
    }
}