package net.fortytoo.easyfind.easyfind.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

// What does this enum even do lol

public enum SearchResult {
    EXECUTE(new int[]{
        GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER});

    private final int[] keyCodes;

    SearchResult(final int[] keyCodes) {
        this.keyCodes = keyCodes;
    }

    public static SearchResult fromKeyCode(final int keyCode) {
        for (final SearchResult result : values()) {
            if (ArrayUtils.contains(result.keyCodes, keyCode)) {
                return result;
            }
        }
        return null;
    }
}
