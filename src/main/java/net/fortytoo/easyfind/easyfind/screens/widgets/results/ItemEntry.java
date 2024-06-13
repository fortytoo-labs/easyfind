package net.fortytoo.easyfind.easyfind.screens.widgets.results;

import net.fortytoo.easyfind.easyfind.config.ConfigAgent;
import net.fortytoo.easyfind.easyfind.screens.widgets.ResultEntry;
import net.fortytoo.easyfind.easyfind.screens.widgets.ResultWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;

import java.awt.*;

public final class ItemEntry extends ResultWidget implements ResultEntry {
    private final boolean isEnabled;
    private final Item item;
    
    public ItemEntry(TextRenderer textRenderer, Item item, boolean isEnabled) {
        super(null, textRenderer);
        this.item = item;
        this.isEnabled = isEnabled;
    }

    public Item getItem() {
        return this.item;
    }
    
    @Override
    public int execute(MinecraftClient client) {
        assert client.player != null;
        if (!client.player.getAbilities().creativeMode) return -1;
        
        if (client.player.networkHandler.hasFeature(item.getRequiredFeatures())) {
            int slot;
            final PlayerInventory inventory = client.player.getInventory();
            final ItemStack itemStack = new ItemStack(this.item);
            final float audioPitch = ((client.player.getRandom().nextFloat() - client.player.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f;

            // Check if player already has the item in the hotbar, if so, select them
            if (!ConfigAgent.ignoreExisting) {
                for (slot = 0; slot <= 8; slot++) {
                    if (inventory.main.get(slot).isOf(item)) {
                        inventory.selectedSlot = slot;
                        return -1;
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
            
            return slot;
        }
        
        return -1;
    }
    
    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        final Rarity rarity = item.getComponents().get(DataComponentTypes.RARITY);
        final Text text;
        final ItemStack itemStack = new ItemStack(this.item);
        Text meta = Text.translatable(item.getTranslationKey() + ".desc").formatted(Formatting.GRAY);

        if (isEnabled) {
            assert rarity != null;
            text = Text.translatable(item.getTranslationKey())
                    .formatted(ConfigAgent.coloredRarity ? rarity.getFormatting() : Formatting.WHITE);
        }
        else {
            text = Text.translatable(item.getTranslationKey()).formatted(Formatting.STRIKETHROUGH, Formatting.GRAY);
            meta = Text.translatable("item.disabled").formatted(Formatting.RED);
        }

        context.drawItem(itemStack, x + 2, y + 2);

        // assume .desc means the item doesn't have a description
        if (ConfigAgent.showDescription && !meta.getString().contains(".desc")) {
            context.drawText(super.textRenderer, text, x + 22, y + 1, Color.WHITE.getRGB(), false);
            context.drawText(super.textRenderer, meta, x + 22, y + 12, Color.GRAY.getRGB(), false);
        }
        else context.drawText(super.textRenderer, text, x + 22, y + 6, Color.WHITE.getRGB(), false);
    }
}
