package net.fortytoo.easyfind.easyfind.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fortytoo.easyfind.easyfind.config.ConfigAgent;
import net.fortytoo.easyfind.easyfind.screens.widgets.ResultListWidget;
import net.fortytoo.easyfind.easyfind.screens.widgets.ResultWidget;
import net.fortytoo.easyfind.easyfind.screens.widgets.SearchboxWidget;
import net.fortytoo.easyfind.easyfind.utils.FuzzyFind;
import net.fortytoo.easyfind.easyfind.utils.ItemHistory;
import net.fortytoo.easyfind.easyfind.utils.RegistryProvider;
import net.fortytoo.easyfind.easyfind.utils.SearchResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.sound.SoundEvents;
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
    
    private ItemRenderer itemRenderer;

    private final ItemHistory itemHistory;

    private String prevQuery;
    private Item lastClickItemEntry;
    
    private static int slot;
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
            this.itemRenderer = client.getItemRenderer();
        }
        
        final int resultBoxWidth = Math.min(super.width / 2, 300);
        final int resultBoxHeight = Math.min(super.height / 2, 300);

        final int searchFieldX = super.width / 2 - resultBoxWidth / 2;
        final int searchFieldY = (super.height / 6) + 5;
        
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
        
        this.setInitialFocus(this.searchboxWidget);
        super.addDrawableChild(this.searchboxWidget);
        
        // Item Lists
        resultListWidget = new ResultListWidget(
                this,
                super.client,
                resultBoxWidth,
                resultBoxHeight,
                resultBoxY + 1,
                resultBoxY + resultBoxHeight
        );
        resultListWidget.setRenderBackground(false);
        resultListWidget.setRenderHorizontalShadows(false);
        resultListWidget.setLeftPos(resultBoxX);
        
        super.addDrawableChild(resultListWidget);
        
        this.updateResults();
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (ConfigAgent.darkBG) renderBackground(matrices);

        final int resultBoxWidth = Math.min(super.width / 2, 300);
        final int resultBoxHeight = Math.min(super.height / 2, 300);

        final int searchFieldX = super.width / 2 - resultBoxWidth / 2;
        final int searchFieldY = (super.height / 6) + 6;

        final int resultBoxX = super.width / 2 - resultBoxWidth / 2;
        final int resultBoxY = (super.height / 6) + inputHeight + 6;
        
        DrawableHelper.fill(
                matrices,
                resultBoxX,
                resultBoxY,
                resultBoxX + resultBoxWidth,
                searchFieldY + inputHeight,
                0xFF101010
        );
        
        super.render(matrices, mouseX, mouseY, delta);
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
        
        // Item search
        if (query.isEmpty()) {
            if (!ConfigAgent.saveHistory) return;
            this.itemHistory.getItemHistory().forEach(item -> {
                resultListWidget.children().add(
                    new ResultWidget(
                            super.textRenderer,
                            this.itemRenderer,
                            item
                    ));
                }
            );
        }
        else {
            FuzzyFind.search(RegistryProvider.getItems(), query).forEach(item -> {
                resultListWidget.children().add(
                    new ResultWidget(
                            super.textRenderer,
                            this.itemRenderer,
                            item.getReferent()
                    ));
                }
            );
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

    private void selectEntry(final ResultWidget entry) {
        this.resultListWidget.setSelected(entry);
        if (!entry.getItem().equals(this.lastClickItemEntry)) {
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
    
    // TODO: Configurable, also refactor this.
    public void giveItem() {
        this.check((client, entry) -> {
            assert client.player != null;
            if (client.player.getAbilities().creativeMode) {
                final Item item = entry.getItem();
                final PlayerInventory inventory = client.player.getInventory();
                final ItemStack itemStack = new ItemStack(item);
                final float audioPitch = ((client.player.getRandom().nextFloat() - client.player.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f;
                
                int slot;

                if (ConfigAgent.saveHistory) this.itemHistory.push(item);
                
                // Check if player already has the item in the hotbar, if so, select them
                if (!ConfigAgent.ignoreExisting) {
                    for (slot = 0; slot <= 8; slot++) {
                        if (inventory.main.get(slot).isOf(item)) {
                            inventory.selectedSlot = slot;
                            this.close();
                            return;
                        }
                    }
                }
                
                // Add to stack if there is an empty slot, replace selected if isn't
                final int emptySlot = inventory.getEmptySlot();
                if (ConfigAgent.forcedReplace) slot = inventory.selectedSlot;
                else if (emptySlot == -1 || emptySlot > 8) {
                    slot = inventory.selectedSlot;
                    slot = switch (ConfigAgent.replaceNeighbor) {
                        case CURRENT -> slot;
                        case NEXT -> slot + 1;
                        case PREVIOUS -> slot - 1;
                    };
                    if (ConfigAgent.replaceNeighbor != ConfigAgent.ReplaceNeighbor.CURRENT)
                        slot = switch (slot) {
                            case -1 -> 8;
                            case 9 -> 0;
                            default -> slot;
                        };
                }
                else slot = emptySlot;
                
                client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(slot + 36, itemStack));
                inventory.selectedSlot = slot;
                
                client.player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2f, audioPitch);
                this.close();
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
    
    @Override
    public void close() {
        if (ConfigAgent.keepScreenOn == ConfigAgent.KeepScreen.SHIFT) {
            if (this.isShiftDown) return;
        }
        super.close();
    }
}
 