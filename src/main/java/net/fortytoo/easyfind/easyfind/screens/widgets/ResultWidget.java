package net.fortytoo.easyfind.easyfind.screens.widgets;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.awt.*;

public class ResultWidget extends AlwaysSelectedEntryListWidget.Entry<ResultWidget> {

    private final TextRenderer textRenderer;
    private final Item item;
    private final int score;

    public ResultWidget(final TextRenderer textRenderer, final Item item, final int score) {
        this.textRenderer = textRenderer;
        this.item = item;
        this.score = score;
    }

    // TODO
    @Override
    public Text getNarration() {
        return Text.translatable(this.item.getTranslationKey());
    }
    
    public Item getItem() {
        return this.item;
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        final Text text = Text.translatable(this.item.getTranslationKey());
        final ItemStack itemStack = new ItemStack(this.item);
        
        context.drawItem(itemStack, x, y);
        context.drawText(this.textRenderer, text, x + 20, y + 4, Color.WHITE.getRGB(), false);
    }
}
