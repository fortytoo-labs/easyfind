package net.fortytoo.easyfind.easyfind.screens.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

import java.awt.*;

public class ResultListWidget extends AlwaysSelectedEntryListWidget<ResultWidget> {
    public ResultListWidget(MinecraftClient minecraftClient, int width, int height, int y) {
        super(minecraftClient, width, height, y, 14);
    }
    
    @Override
    public void renderWidget(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (this.children().isEmpty()) {
            final Text text = Text.translatable("efs.404");
            context.drawText(
                    this.client.textRenderer,
                    text,
                    this.getX() + this.width / 2 - this.client.textRenderer.getWidth(text) / 2,
                    this.getY() + this.height / 2 - 5,
                    Color.PINK.getRGB(),
                    true
            );
            return;
        }
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    public ResultWidget at(final int n) {
        if (n < 0 || n >= this.children().size()) {
            return null;
        }
        return this.children().get(n);
    }
    
    @Override
    public void setFocused(boolean focused) {
        super.setFocused(false);
    }
}
