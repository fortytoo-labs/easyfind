package net.fortytoo.easyfind.easyfind;

import net.fabricmc.api.ModInitializer;
import net.fortytoo.easyfind.easyfind.utils.LogUtil;

public class EasyFind implements ModInitializer {
    @Override
    public void onInitialize() {
        LogUtil.info("Welcome to EasyFind!");
    }
}