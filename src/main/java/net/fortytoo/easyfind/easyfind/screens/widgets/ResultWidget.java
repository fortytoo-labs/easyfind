package net.fortytoo.easyfind.easyfind.screens.widgets;

import net.fortytoo.easyfind.easyfind.config.ConfigAgent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;

import java.awt.*;

public class ResultWidget extends AlwaysSelectedEntryListWidget.Entry<ResultWidget> {
    private final TextRenderer textRenderer;
    private final Item item;
    private final boolean isEnabled;

    public ResultWidget(final TextRenderer textRenderer, final Item item, boolean isEnabled) {
        this.textRenderer = textRenderer;
        this.item = item;
        this.isEnabled = isEnabled;
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
        final Text text;
        final ItemStack itemStack = new ItemStack(this.item);
        final Rarity rarity = item.getRarity(itemStack);
        Text meta = Text.translatable(item.getTranslationKey() + ".desc").formatted(Formatting.GRAY);
        
        if (isEnabled) {
            assert rarity != null;
            text = Text.translatable(item.getTranslationKey())
                    .formatted(ConfigAgent.coloredRarity ? rarity.formatting : Formatting.WHITE);
        }
        else {
            text = Text.translatable(item.getTranslationKey()).formatted(Formatting.STRIKETHROUGH, Formatting.GRAY);
            meta = Text.translatable("item.disabled").formatted(Formatting.RED);
        }
        
        context.drawItem(itemStack, x + 2, y + 2);
        
        // assume .desc means the item doesn't have a description
        // could be done better.
        if (ConfigAgent.showDescription && !meta.getString().contains(".desc")) {
            context.drawText(this.textRenderer, text, x + 22, y + 1, Color.WHITE.getRGB(), false);
            context.drawText(this.textRenderer, meta, x + 22, y + 12, Color.GRAY.getRGB(), false);
        }
        else context.drawText(this.textRenderer, text, x + 22, y + 6, Color.WHITE.getRGB(), false);
    }
}
