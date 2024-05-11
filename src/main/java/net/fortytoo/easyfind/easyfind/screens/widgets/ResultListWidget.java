package net.fortytoo.easyfind.easyfind.screens.widgets;

import net.fortytoo.easyfind.easyfind.screens.Spotlight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ResultListWidget extends AlwaysSelectedEntryListWidget<ResultWidget> {
    final private Spotlight spotlight;
    final private int entryWidth;
    
    public ResultListWidget(Spotlight screen, 
                            final MinecraftClient minecraftClient,
                            final int width,
                            final int height,
                            final int top,
                            final int bottom) {
        super(minecraftClient, width, height, top, bottom, 24);
        this.spotlight = screen;
        this.entryWidth = width;
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
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta) {
        // no history found
        if (spotlight.getSearchboxWidget().getText().isEmpty() && spotlight.getItemHistory().isEmpty()) return;
        
        // avoid displaying not found on blank search query
        if (spotlight.getSearchboxWidget().getText().isEmpty() && this.children().isEmpty()) return;
        
        fill(matrices, this.left - 1, this.top - 1, this.right + 1, this.bottom + 1, Color.WHITE.getRGB()); // border
        fill(matrices, this.left, this.top, this.right, this.bottom, Color.BLACK.getRGB()); // background
        
        if (this.children().isEmpty()) {
            final Text text = Text.translatable("efs.404");
            this.client.textRenderer.draw(
                    matrices,
                    text,
                    this.left + this.width / 2 - this.client.textRenderer.getWidth(text) / 2,
                    this.top + this.height / 2 - 5,
                    Color.PINK.getRGB()
            );
        }
        
        // manual scissor
        enableScissor(this.left, this.top, this.right, this.bottom);
        super.render(matrices, mouseX, mouseY, delta);
        disableScissor();
    }
    
    @Override
    protected void renderEntry(MatrixStack matrices, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
        super.renderEntry(matrices, mouseX, mouseY, delta, index, x - 14, y, entryWidth, entryHeight);
    }
    
    @Override
    protected void drawSelectionHighlight(MatrixStack matrices, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
        int i = this.left + (this.width - entryWidth) / 2 - 14;
        int j = this.left + (this.width + entryWidth) / 2 + ((this.getMaxScroll() > 0) ? 8 : 14);
        fill(matrices, i, y - 2, j, y + entryHeight + 2, borderColor);
        fill(matrices,i + 1, y - 1, j - 1, y + entryHeight + 1, fillColor);
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
        return mouseX >= (double) this.left
                && mouseX <= (double) this.right
                && mouseY >= (double) this.top
                && mouseY <= (double) this.bottom;
    }
    
    public void skipSelection() {
        this.spotlight.getResultList().moveSelection(MoveDirection.DOWN);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            spotlight.giveItem();
            return true;
        }
        
        ResultListWidget resultList = this.spotlight.getResultList();
        ResultWidget selected = resultList.getSelectedOrNull();

        switch (keyCode) {
            // select previous entry
            case GLFW.GLFW_KEY_UP -> {
                if (selected != null && resultList.getEntry(0).equals(selected)) {
                    this.spotlight.setFocused(this.spotlight.getSearchboxWidget());
                }
                resultList.moveSelection(MoveDirection.UP);
                return true;
            }
            // select next entry
            case GLFW.GLFW_KEY_DOWN -> {
                resultList.moveSelection(MoveDirection.DOWN);
                return true;
            }
        }
        
        // TODO: Focus on searchbox when query being entered
        //       Comparing keyCode to GLFW ascii or something
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            spotlight.setFocused(spotlight.getSearchboxWidget());
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
