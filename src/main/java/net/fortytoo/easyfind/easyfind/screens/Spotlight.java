package net.fortytoo.easyfind.easyfind.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fortytoo.easyfind.easyfind.screens.widgets.ResultListWidget;
import net.fortytoo.easyfind.easyfind.screens.widgets.ResultWidget;
import net.fortytoo.easyfind.easyfind.screens.widgets.SearchboxWidget;
import net.fortytoo.easyfind.easyfind.utils.FuzzyFind;
import net.fortytoo.easyfind.easyfind.utils.ItemHistory;
import net.fortytoo.easyfind.easyfind.utils.RegistryProvider;
import net.fortytoo.easyfind.easyfind.utils.SearchResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.Objects;
import java.util.Queue;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class Spotlight extends Screen {
    private SearchboxWidget searchboxWidget;
    private ResultListWidget resultListWidget;

    private final ItemHistory itemHistory;

    private String prevQuery;
    private Item lastClickItemEntry;
    
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
                this.giveItem();
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
    public void tick() {
        if (client != null) {
            assert client.player != null;
            if (!client.player.getAbilities().creativeMode) this.close();
        }
    }
    
    private void search(final String query) {
        if (this.prevQuery != null && this.prevQuery.equals(query)) {
            return;
        }
        
        this.prevQuery = query;
        this.resultListWidget.children().clear();
        
        if (query.isEmpty()) {
            this.itemHistory.getItemHistory().forEach(item -> resultListWidget.children().add(
                    new ResultWidget(
                            super.textRenderer,
                            item,
                            0,
                            player
                    )));
        }
        else {
            FuzzyFind.search(RegistryProvider.getItems(), query).forEach(item -> 
                    resultListWidget.children().add(
                            new ResultWidget(
                                    super.textRenderer,
                                    item.getReferent(),
                                    item.getScore(),
                                    player
            )));
        }
        
        if (!resultListWidget.children().isEmpty()) {
            resultListWidget.setSelected(resultListWidget.children().getFirst());
        } else {
            resultListWidget.setSelected(null);
        }
        
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

    private boolean doubleClick(final double mouseY, final int button) {
        final ResultWidget selectedEntry = this.resultListWidget.getSelectedOrNull();
        if (selectedEntry != null) {
            final long timeMs = Util.getMeasuringTimeMs();
            if (timeMs - this.lastClickTime <= 420
                    && this.lastClickItemEntry != null
                    && this.lastClickItemEntry.equals(selectedEntry.getItem())) {
                if (button == 0) {
                    this.giveItem();
                }
                return true;
            }

            this.lastClickTime = timeMs;
            this.lastClickItemEntry = selectedEntry.getItem();
        }
        return false;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.resultListWidget.isMouseOver(mouseX, mouseY)
                && this.doubleClick(mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    // TODO: Configurable, also refactor this.
    public void giveItem() {
        this.check((client, entry) -> {
            assert client.player != null;
            if (client.player.getAbilities().creativeMode) {
                final Item item = entry.getItem();
                if (client.player.networkHandler.hasFeature(item.getRequiredFeatures())) {
                    final PlayerInventory inventory = client.player.getInventory();
                    final ItemStack itemStack = new ItemStack(item);
                    final float audioPitch = ((client.player.getRandom().nextFloat() - client.player.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f;
                    
                    int slot;

                    this.itemHistory.push(item);
                    
                    // Check if player already has the item in the hotbar, if so, select them
                    for (slot = 0; slot <= 8; slot++) {
                        if (inventory.main.get(slot).isOf(item)) {
                            inventory.selectedSlot = slot;
                            this.close();
                            return;
                        }
                    }
                    
                    // Add to stack if there is an empty slot, replace selected if isn't
                    final int emptySlot = inventory.getEmptySlot();
                    if (emptySlot == -1 || emptySlot > 8) slot = inventory.selectedSlot;
                    else slot = emptySlot;
                    
                    client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(slot + 36, itemStack));
                    inventory.selectedSlot = slot;
                    
                    client.player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2f, audioPitch);
                    this.close();
                }
            }
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
}
 