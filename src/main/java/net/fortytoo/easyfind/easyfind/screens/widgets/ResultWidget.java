package net.fortytoo.easyfind.easyfind.screens.widgets;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

import java.awt.*;

public class ResultWidget extends AlwaysSelectedEntryListWidget.Entry<ResultWidget> {

    private final TextRenderer textRenderer;
    private final String item;
    private final int score;

    public ResultWidget(final TextRenderer textRenderer, final String item, final int score) {
        this.textRenderer = textRenderer;
        this.item = item;
        this.score = score;
    }

    // TODO
    @Override
    public Text getNarration() {
        return Text.of(this.item.toString());
    }
    
    public String getItem() {
        return this.item;
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        // final int itemWidth = this.textRenderer.getWidth(this.item);
        final Text strgen = Text.translatable(this.item.toString());
        context.drawText(this.textRenderer, strgen, x + 1, y + 1, Color.WHITE.getRGB(), false);
    }
}