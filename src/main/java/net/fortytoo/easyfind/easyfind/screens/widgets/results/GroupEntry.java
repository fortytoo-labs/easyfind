package net.fortytoo.easyfind.easyfind.screens.widgets.results;

import net.fortytoo.easyfind.easyfind.screens.widgets.ResultWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemGroup;

public class GroupEntry extends ResultWidget {
    public GroupEntry(TextRenderer textRenderer, ItemGroup itemGroup) {
        super(null, textRenderer);
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        
    }
}
