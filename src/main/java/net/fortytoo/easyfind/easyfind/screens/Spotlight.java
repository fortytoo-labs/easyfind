package net.fortytoo.easyfind.easyfind.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fortytoo.easyfind.easyfind.config.ConfigAgent;
import net.fortytoo.easyfind.easyfind.screens.widgets.ResultListWidget;
import net.fortytoo.easyfind.easyfind.screens.widgets.ResultWidget;
import net.fortytoo.easyfind.easyfind.screens.widgets.SearchboxWidget;
import net.fortytoo.easyfind.easyfind.screens.widgets.results.ItemEntry;
import net.fortytoo.easyfind.easyfind.utils.FuzzyFind;
import net.fortytoo.easyfind.easyfind.utils.ItemHistory;
import net.fortytoo.easyfind.easyfind.utils.RegistryProvider;
import net.fortytoo.easyfind.easyfind.utils.SearchResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.Queue;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class Spotlight extends Screen {
    private SearchboxWidget searchboxWidget;
    private ResultListWidget resultListWidget;

    private final ItemHistory itemHistory;

    private String prevQuery;
    private ResultWidget lastClickItemEntry;
    
    private boolean isShiftDown = false;
    
    private long lastClickTime;
    
    final int inputHeight = 16;
    
    ClientPlayerEntity player;

    public Spotlight(ItemHistory itemHistory) {
        super(Text.translatable("efs.title"));
        this.itemHistory = itemHistory;
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    @Override
    protected void init() {
        super.init();

        if (client != null) {
            player = client.player;
        }

        // TODO: change these
        final int resultBoxWidth = Math.min(super.width / 2, 300);
        final int resultBoxHeight = Math.min(super.height / 2, 300);

        final int searchFieldX = super.width / 2 - resultBoxWidth / 2;
        final int searchFieldY = (super.height / 6) + 6;
        
        final int resultBoxX = super.width / 2 - resultBoxWidth / 2;
        final int resultBoxY = (super.height / 6) + inputHeight + 6;
        
        this.searchboxWidget = new SearchboxWidget(
                this,
                super.textRenderer,
                searchFieldX,
                searchFieldY,
                resultBoxWidth,
                inputHeight
        );
        
        this.searchboxWidget.setChangedListener(this::search);
        this.searchboxWidget.setResultConsumer((result, entry) -> {
            if (Objects.requireNonNull(result) == SearchResult.EXECUTE) {
                this.execute();
            }
        });
        
        this.setFocused(this.searchboxWidget);
        super.addDrawableChild(this.searchboxWidget);
        
        // Item Lists
        resultListWidget = new ResultListWidget(
                this,
                super.client,
                resultBoxWidth,
                resultBoxHeight,
                resultBoxY
        );

        resultListWidget.setX(resultBoxX);
        super.addDrawableChild(resultListWidget);
        
        this.updateResults();
    }
    
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (ConfigAgent.blurredBG) this.applyBlur(delta);
        this.renderDarkening(context);
    }
    
    @Override
    public void tick() {
        if (client != null) {
            assert client.player != null;
            if (!client.player.getAbilities().creativeMode) this.close();
        }
    }
    
    // TODO: Extendable search
    private void search(final String query) {
        if (this.prevQuery != null && this.prevQuery.equals(query)) {
            return;
        }
        
        this.prevQuery = query;
        this.resultListWidget.children().clear();
        
        // Item search
        if (query.isEmpty()) {
            if (!ConfigAgent.saveHistory) return;
            this.itemHistory.getItemHistory().forEach(this::addItemToResult);
        }
        else {
            FuzzyFind.search(RegistryProvider.getItems(), query).forEach(item -> this.addItemToResult(item.getReferent()));
        }
        
        if (!resultListWidget.children().isEmpty()) {
            resultListWidget.setSelected(resultListWidget.children().getFirst());
        } else {
            resultListWidget.setSelected(null);
        }
        
        resultListWidget.setScrollAmount(0);
    }
    
    private void addItemToResult(Item item) {
        boolean hasFeature = this.player.networkHandler.hasFeature(item.getRequiredFeatures());
        if (!hasFeature && !ConfigAgent.showDisabledItem) return;
        resultListWidget.children().add(new ItemEntry(super.textRenderer, item, hasFeature));
    }

    private void check(final BiConsumer<MinecraftClient, ResultWidget> entryConsumer) {
        final ResultWidget entry = this.resultListWidget.getSelectedOrNull();
        if (entry == null) {
            return;
        }
        if (super.client == null || super.client.player == null) {
            return;
        }
        entryConsumer.accept(super.client, entry);
    }

    private void selectEntry(final ResultWidget entry) {
        this.resultListWidget.setSelected(entry);
        if (!entry.getEntry().equals(this.lastClickItemEntry)) {
            this.lastClickItemEntry = null;
        }
    }

    private boolean entryClickHandler(final double mouseY, final int button) {
        // entry select
        final int entryY = this.resultListWidget.getEntryY(mouseY);
        if (entryY >= 0) {
            final int entryIndex = entryY / this.resultListWidget.getEntryHeight();
            final ResultWidget entry = this.resultListWidget.at(entryIndex);
            if (entry != null) {
                this.selectEntry(entry);
            }
        }
        
        // double click exec
        final ResultWidget selectedEntry = this.resultListWidget.getSelectedOrNull();
        if (selectedEntry != null) {
            final long timeMs = Util.getMeasuringTimeMs();
            if (timeMs - this.lastClickTime <= 420
                    && this.lastClickItemEntry != null
                    && this.lastClickItemEntry.equals(selectedEntry.getEntry())) {
                if (button == 0) {
                    this.execute();
                }
                return true;
            }

            this.lastClickTime = timeMs;
            this.lastClickItemEntry = selectedEntry.getEntry();
        }
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.isShiftDown = (modifiers & GLFW.GLFW_MOD_SHIFT) == GLFW.GLFW_MOD_SHIFT;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.resultListWidget.isMouseOver(mouseX, mouseY)
                && this.entryClickHandler(mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    public void execute() {
        this.check((client, entry) -> {
            assert client.player != null;
            
            ItemEntry itemEntry = (ItemEntry) entry.getEntry();
            
            itemEntry.execute(client);
            
            this.close();
        });
    }

    public void updateResults() {
        this.search(this.searchboxWidget.getText());
    }

    public ResultListWidget getResultList() {
        return resultListWidget;
    }
    
    public SearchboxWidget getSearchboxWidget() {
        return searchboxWidget;
    }

    public Queue<Item> getItemHistory() {
        return itemHistory.getItemHistory();
    }
    
    @Override
    public void close() {
        if (ConfigAgent.keepScreenOn == ConfigAgent.KeepScreen.SHIFT) {
            if (this.isShiftDown) return;
        }
        super.close();
    }
}
 