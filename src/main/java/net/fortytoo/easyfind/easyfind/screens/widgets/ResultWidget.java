package net.fortytoo.easyfind.easyfind.screens.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

public class ResultWidget extends AlwaysSelectedEntryListWidget.Entry<ResultWidget> {
    protected TextRenderer textRenderer;
    private final ResultEntry resultEntry;
    
    public ResultWidget(ResultEntry resultEntry, final TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
        this.resultEntry = resultEntry;
    }
    
    // TODO: Accessibility
    @Override
    public Text getNarration() {
        return Text.translatable("efs.title");
    }

    public ResultWidget getEntry() {
        return this;
    }
    
    public int execute(MinecraftClient client) {
        if (resultEntry != null) return resultEntry.execute(client);
        return -443; // execution unsuccessful
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
    }
}
