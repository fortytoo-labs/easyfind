package net.fortytoo.easyfind.easyfind;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fortytoo.easyfind.easyfind.payloads.GiveItemPayload;
import net.fortytoo.easyfind.easyfind.utils.LogUtil;

public class EasyFind implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(GiveItemPayload.ID, GiveItemPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(GiveItemPayload.ID, (payload, context) -> {
            context.player().giveItemStack(payload.itemStack());
        });
        
        LogUtil.info("Welcome to EasyFind!");
    }
}

