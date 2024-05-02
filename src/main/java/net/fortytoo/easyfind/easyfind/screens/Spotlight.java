package net.fortytoo.easyfind.easyfind.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fortytoo.easyfind.easyfind.screens.widgets.ResultListWidget;
import net.fortytoo.easyfind.easyfind.screens.widgets.ResultWidget;
import net.fortytoo.easyfind.easyfind.screens.widgets.SearchboxWidget;
import net.fortytoo.easyfind.easyfind.utils.FuzzyFind;
import net.fortytoo.easyfind.easyfind.utils.RegistryProvider;
import net.fortytoo.easyfind.easyfind.utils.SearchResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class Spotlight extends Screen {
    private SearchboxWidget searchboxWidget;
    private ResultListWidget resultListWidget;

    private final int inputHeight = 16;
    private String prevQuery;
    
    public Spotlight() {
        super(Text.translatable("efs.title"));
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    @Override
    protected void init() {
        super.init();

        // TODO: change these
        final int resultBoxWidth = Math.min(super.width / 2, 300);
        final int resultBoxHeight = Math.min(super.height / 2, 300);

        final int searchFieldX = super.width / 2 - resultBoxWidth / 2;
        final int searchFieldY = super.height / 6;
        
        final int resultBoxX = super.width / 2 - resultBoxWidth / 2;
        final int resultBoxY = super.height / 6 + inputHeight + 1;
        
        this.searchboxWidget = new SearchboxWidget(
                this,
                super.textRenderer,
                searchFieldX,
                searchFieldY,
                resultBoxWidth,
                inputHeight,
                Text.translatable("efs.title"),
                Text.translatable("efs.placeholder")
        );
        
        this.searchboxWidget.setChangedListener(this::search);
        this.searchboxWidget.setResultConsumer((result, entry) -> {
            if (Objects.requireNonNull(result) == SearchResult.EXECUTE) {
                this.giveItem();
            }
        });
        
        this.setFocused(this.searchboxWidget);
        super.addDrawableChild(this.searchboxWidget);
        
        // Item Lists
        resultListWidget = new ResultListWidget(
                super.client,
                resultBoxWidth,
                resultBoxHeight,
                resultBoxY
        );
        resultListWidget.setX(resultBoxX);
        super.addDrawableChild(resultListWidget);
        
        this.updateResults();
    }
    
    private void search(final String query) {
        if (this.prevQuery != null && this.prevQuery.equals(query)) {
            return;
        }
        
        this.prevQuery = query;
        this.resultListWidget.children().clear();
        
        FuzzyFind.search(RegistryProvider.getItems(), query).forEach(item -> {
            resultListWidget.children().add(new ResultWidget(
                    super.textRenderer,
                    item.getReferent(),
                    item.getScore()
            ));
        });

        // select first children
        if (!resultListWidget.children().isEmpty()) {
            resultListWidget.setSelected(resultListWidget.children().getFirst());
        } else {
            resultListWidget.setSelected(null);
        }

        // reset scroll position
        resultListWidget.setScrollAmount(0);
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
    
    public void giveItem() {
        this.check((client, entry) -> {
            assert client.player != null;
            client.player.networkHandler.sendCommand("give @p " + entry.getItem());
        });
    }

    public void updateResults() {
        this.search(this.searchboxWidget.getText());
    }

    public ResultListWidget getResultList() {
        return resultListWidget;
    }

    public void close() {
        assert super.client != null;
        super.client.setScreen(null);
    }
}
 