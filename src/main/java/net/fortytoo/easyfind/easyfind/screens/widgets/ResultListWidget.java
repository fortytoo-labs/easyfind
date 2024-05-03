package net.fortytoo.easyfind.easyfind.screens.widgets;

import net.fortytoo.easyfind.easyfind.screens.Spotlight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ResultListWidget extends AlwaysSelectedEntryListWidget<ResultWidget> {
    private Spotlight spotlight;
    
    public ResultListWidget(Spotlight screen, MinecraftClient minecraftClient, int width, int height, int y) {
        super(minecraftClient, width, height, y, 14);
        this.spotlight = screen;
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
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            spotlight.giveItem();
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
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
