package net.fortytoo.easyfind.easyfind.screens.widgets;

import net.fortytoo.easyfind.easyfind.screens.Spotlight;
import net.fortytoo.easyfind.easyfind.utils.SearchResult;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiConsumer;

public class SearchboxWidget extends TextFieldWidget {
    private final Spotlight spotlight;
    private final TextRenderer textRenderer;
    private BiConsumer<SearchResult, ResultWidget> resultConsumer = null;
    
    public SearchboxWidget(Spotlight screen, TextRenderer textRenderer, int x, int y, int width, int height) {
        super(textRenderer, x, y - 4, width, height + 4, Text.translatable("efs.title"));
        this.spotlight = screen;
        this.textRenderer = textRenderer;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            this.spotlight.getResultList().skipSelection();
            this.spotlight.setFocused(this.spotlight.getResultList());
        }
        
        final SearchResult result = SearchResult.fromKeyCode(keyCode);
        if (result != null) {
            final ResultWidget resultWidgetEntry = this.spotlight.getResultList().getSelectedOrNull();
            if (resultWidgetEntry != null) {
                this.resultConsumer.accept(result, resultWidgetEntry);
            }
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void setResultConsumer(BiConsumer<SearchResult, ResultWidget> resultConsumer) {
        this.resultConsumer = resultConsumer;
    }
}
