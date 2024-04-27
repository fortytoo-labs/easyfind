package net.fortytoo.easyfind.easyfind.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fortytoo.easyfind.easyfind.screens.Spotlight;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyFindClient implements ClientModInitializer {
    private static KeyBinding openEFS;
    public static final Logger LOGGER = LoggerFactory.getLogger("EFSpotlight");
    
    @Override
    public void onInitializeClient() {
        openEFS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "efs.spotlight",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                "efs.category"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openEFS.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new Spotlight());
                }
            }
        });
    }
}
