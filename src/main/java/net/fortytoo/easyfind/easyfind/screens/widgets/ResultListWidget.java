package net.fortytoo.easyfind.easyfind.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fortytoo.easyfind.easyfind.screens.Spotlight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ResultListWidget extends AlwaysSelectedEntryListWidget<ResultWidget> {
    final private Spotlight spotlight;
    final private int entryWidth;
    
    public ResultListWidget(Spotlight screen, MinecraftClient minecraftClient, int width, int height, int y) {
        super(minecraftClient, width, height, y, 20);
        this.spotlight = screen;
        this.entryWidth = width;
    }

    public void selectNextEntryInDirection(final NavigationDirection direction) {
        final ResultWidget entry = this.getNeighboringEntry(direction);
        if (entry != null) {
            this.setSelected(entry);
        }
    }
    
    // TODO: Style the entry list
    @Override
    public int getRowWidth() {
        return this.entryWidth - 24;
    }

    @Override
    protected void drawHeaderAndFooterSeparators(DrawContext context) {
        RenderSystem.enableBlend();
        context.drawBorder(this.getX(), this.getY() - 1, this.getWidth(), 1, Color.WHITE.getRGB()); // top
        context.drawBorder(this.getX(), this.getBottom(), this.getWidth(), 1, Color.WHITE.getRGB()); // bottom
        context.drawBorder(this.getX(), this.getY(), 1, this.getHeight() + 1, Color.WHITE.getRGB()); // left
        context.drawBorder(this.getRight() - 1, this.getY() - 1, 1, this.getHeight() + 2, Color.WHITE.getRGB()); // right
        RenderSystem.disableBlend();
    }

    @Override
    protected void drawMenuListBackground(DrawContext context) {
        super.drawMenuListBackground(context);
    }
    
    @Override
    public void renderWidget(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (spotlight.getSearchboxWidget().getText().isEmpty()) return;
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
    
    @Override
    public void setFocused(boolean focused) {
        super.setFocused(false);
    }
}
