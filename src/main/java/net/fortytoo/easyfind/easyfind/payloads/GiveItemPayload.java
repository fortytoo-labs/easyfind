package net.fortytoo.easyfind.easyfind.payloads;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record GiveItemPayload(ItemStack itemStack) implements CustomPayload {
    public static final Id<GiveItemPayload> ID = CustomPayload.id("efs:give_item");
    public static final PacketCodec<RegistryByteBuf, GiveItemPayload> CODEC = PacketCodec.tuple(
            ItemStack.PACKET_CODEC, GiveItemPayload::itemStack, GiveItemPayload::new
    );
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
