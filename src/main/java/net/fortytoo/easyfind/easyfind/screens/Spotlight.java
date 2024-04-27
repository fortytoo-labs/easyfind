package net.fortytoo.easyfind.easyfind.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fortytoo.easyfind.easyfind.screens.widgets.Searchbox;
import net.fortytoo.easyfind.easyfind.utils.FuzzyFind;
import net.fortytoo.easyfind.easyfind.utils.RegistryProvider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class Spotlight extends Screen {
    private final int inputHeight = 16;
    private Searchbox searchbox;
    private String prevQuery;
    private RegistryProvider registryProvider;
    
    public Spotlight() {
        super(Text.translatable("efs.title"));
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

        final int resultBoxX = super.width / 2 - resultBoxWidth / 2;
        final int resultBoxY = super.height / 6;

        final int searchFieldX = super.width / 2 - resultBoxWidth / 2;
        final int searchFieldY = resultBoxY;

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
    }
    
    private void search(final String query) {
        if (this.prevQuery != null && this.prevQuery.equals(query)) {
            return;
        }
        this.prevQuery = query;
        FuzzyFind.search(registryProvider.getItems(), query);
    }
    
    public void close() {
        super.client.setScreen(null);
    }
}
 