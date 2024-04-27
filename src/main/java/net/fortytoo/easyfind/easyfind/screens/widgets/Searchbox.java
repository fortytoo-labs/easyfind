package net.fortytoo.easyfind.easyfind.screens.widgets;

import net.fortytoo.easyfind.easyfind.screens.Spotlight;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class Searchbox extends TextFieldWidget {
    private final Spotlight spotlight;
    
    public Searchbox(Spotlight screen, TextRenderer textRenderer, int x, int y, int width, int height, Text text, Text placeholder) {
        super(textRenderer, x, y, width, height, text);
        this.spotlight = screen;
        this.setPlaceholder(placeholder);
    }
}
