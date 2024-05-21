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
    
    public ResultListWidget(Spotlight screen, 
                            MinecraftClient minecraftClient, 
                            int width, 
                            int height, 
                            int top,
                            int bottom) {
        super(minecraftClient, width, height, top, bottom, 24);
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
    protected int getScrollbarPositionX() {
        return this.right - 6;
    }
    
    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        // no history found
        if (spotlight.getSearchboxWidget().getText().isEmpty() && spotlight.getItemHistory().isEmpty()) return;
        
        // avoid displaying not found on blank search query
        if (spotlight.getSearchboxWidget().getText().isEmpty() && this.children().isEmpty()) return;

        context.fill(this.left, this.top, this.right, this.bottom + 1, Color.BLACK.getRGB()); // background
        context.drawBorder(this.left - 1, this.top - 1, this.width + 2, this.height + 2, Color.WHITE.getRGB()); // border
        
        if (this.children().isEmpty()) {
            final Text text = Text.translatable("efs.404");
            context.drawText(
                    this.client.textRenderer,
                    text,
                    this.left + this.width / 2 - this.client.textRenderer.getWidth(text) / 2,
                    this.top + this.height / 2 - 5,
                    Color.PINK.getRGB(),
                    true
            );
        }
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
        super.renderEntry(context, mouseX, mouseY, delta, index, x - 14, y, entryWidth, entryHeight);
    }
    
    @Override
    protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
        int i = this.left + (this.width - entryWidth) / 2 - 14;
        int j = this.left + (this.width + entryWidth) / 2 + ((this.getMaxScroll() > 0) ? 8 : 14);
        context.fill(i, y - 2, j, y + entryHeight + 2, borderColor);
        context.fill(i + 1, y - 1, j - 1, y + entryHeight + 1, fillColor);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // mouse click helper
    public int getEntryY(final double mouseY) {
        return MathHelper.floor(mouseY - this.top)
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
        return mouseX >= this.left
                && mouseX <= this.left + this.width
                && mouseY >= this.top
                && mouseY <= this.top + this.height;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            spotlight.giveItem();
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
