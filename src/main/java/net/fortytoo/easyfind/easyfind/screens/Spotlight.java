package net.fortytoo.easyfind.easyfind.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fortytoo.easyfind.easyfind.screens.widgets.Result;
import net.fortytoo.easyfind.easyfind.screens.widgets.ResultList;
import net.fortytoo.easyfind.easyfind.screens.widgets.Searchbox;
import net.fortytoo.easyfind.easyfind.utils.FuzzyFind;
import net.fortytoo.easyfind.easyfind.utils.RegistryProvider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class Spotlight extends Screen {
    private RegistryProvider registryProvider;
    
    private Searchbox searchbox;
    private ResultList resultList;
    
    private final int inputHeight = 16;
    private String prevQuery;
    
    
    public Spotlight() {
        super(Text.translatable("efs.title"));
        // FIXME: Do not create a new instance of this.
        registryProvider = new RegistryProvider();
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
        
        this.searchbox = new Searchbox(
                this,
                super.textRenderer,
                searchFieldX,
                searchFieldY,
                resultBoxWidth,
                inputHeight,
                Text.translatable("efs.title"),
                Text.translatable("efs.placeholder")
        );
        
        this.searchbox.setChangedListener(this::search);
        this.setFocused(this.searchbox);
        super.addDrawableChild(this.searchbox);
        
        // Item Lists
        resultList = new ResultList(
                super.client,
                resultBoxWidth,
                resultBoxHeight,
                resultBoxY
        );
        resultList.setX(resultBoxX);
        super.addDrawableChild(resultList);
        
        this.updateResults();
    }
    
    private void search(final String query) {
        if (this.prevQuery != null && this.prevQuery.equals(query)) {
            return;
        }
        this.prevQuery = query;
        this.resultList.children().clear();
        
        FuzzyFind.search(registryProvider.getItems(), query).forEach(item -> {
            resultList.children().add(new Result(
                    super.textRenderer,
                    item.getString(),
                    item.getScore()
            ));
        });

        // select first children
        if (!resultList.children().isEmpty()) {
            resultList.setSelected(resultList.children().getFirst());
        } else {
            resultList.setSelected(null);
        }

        // reset scroll position
        resultList.setScrollAmount(0);
    }

    public void updateResults() {
        this.search(this.searchbox.getText());
    }


    public void close() {
        super.client.setScreen(null);
    }
}
 