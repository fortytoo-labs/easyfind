package net.fortytoo.easyfind.easyfind.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fortytoo.easyfind.easyfind.screens.Spotlight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ResultListWidget extends AlwaysSelectedEntryListWidget<ResultWidget> {
    final private Spotlight spotlight;
    final private int entryWidth;
    
    public ResultListWidget(Spotlight screen, MinecraftClient minecraftClient, int width, int height, int y) {
        super(minecraftClient, width, height, y, 24);
        this.spotlight = screen;
        this.entryWidth = width;
    }

    public void selectNextEntryInDirection(final NavigationDirection direction) {
        final ResultWidget entry = this.getNeighboringEntry(direction);
        if (entry != null) {
            this.setSelected(entry);
        }
    }
    
    @Override
    public int getRowWidth() {
        return this.entryWidth - 34;
    }

    @Override
    protected void drawHeaderAndFooterSeparators(DrawContext context) {
        RenderSystem.enableBlend();
        context.drawBorder(this.getX(), this.getY() - 1, this.getWidth(), this.getHeight() + 2, Color.WHITE.getRGB()); // border
        RenderSystem.disableBlend();
    }

    @Override
    protected void drawMenuListBackground(DrawContext context) {
        super.drawMenuListBackground(context);
    }
    
    @Override
    public void renderWidget(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        // no history found
        if (spotlight.getSearchboxWidget().getText().isEmpty() && spotlight.getItemHistory().isEmpty()) return;
        
        // avoid displaying not found on blank search query
        if (spotlight.getSearchboxWidget().getText().isEmpty() && this.children().isEmpty()) return;
        
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
    protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
        super.renderEntry(context, mouseX, mouseY, delta, index, x - 14, y, entryWidth, entryHeight);
    }
    
    @Override
    protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
        int i = this.getX() + (this.width - entryWidth) / 2 - 14;
        int j = this.getX() + (this.width + entryWidth) / 2 + (this.isScrollbarVisible() ? 8 : 14);
        context.fill(i, y - 2, j, y + entryHeight + 2, borderColor);
        context.fill(i + 1, y - 1, j - 1, y + entryHeight + 1, fillColor);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // mouse click helper
    public int getEntryY(final double mouseY) {
        return MathHelper.floor(mouseY - this.getY())
                - this.headerHeight + (int) this.getScrollAmount() - 2;
    }

    public int getEntryHeight() {
        return this.itemHeight;
    }

    public ResultWidget at(final int n) {
        if (n < 0 || n >= this.children().size()) {
            return null;
        }
        return this.children().get(n);
    }

    public boolean isMouseOver(final double mouseX, final double mouseY) {
        return mouseX >= this.getX()
                && mouseX <= this.getX() + this.width
                && mouseY >= this.getY()
                && mouseY <= this.getY() + this.height;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            spotlight.execute();
            return true;
        }
        
        // TODO: Focus on searchbox when query being entered
        //       Comparing keyCode to GLFW ascii or something
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            spotlight.setFocused(spotlight.getSearchboxWidget());
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public void setFocused(boolean focused) {
        super.setFocused(false);
    }
}
