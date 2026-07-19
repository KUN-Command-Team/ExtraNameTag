package com.github.chore3.extranametag.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class NametagStackPacket {
    private final CompoundTag tag;
    private final UUID entityId;
    private static BiConsumer<UUID, CompoundTag> clientHandler = (__, ____) -> {};

    public static void setClientHandler(BiConsumer<UUID, CompoundTag> handler) {
        clientHandler = handler;
    }

    public NametagStackPacket(CompoundTag tag, UUID entityId) {
        this.tag = tag;
        this.entityId = entityId;
    }

    public static void encode(NametagStackPacket packet, FriendlyByteBuf buf) {
        buf.writeNbt(packet.tag);
        buf.writeUUID(packet.entityId);
    }

    public static NametagStackPacket decode(FriendlyByteBuf buf) {
        return new NametagStackPacket(buf.readNbt(), buf.readUUID());
    }

    public static void handle(NametagStackPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        CompoundTag tag = packet.tag;
        UUID entityId = packet.entityId;
        context.enqueueWork(() -> clientHandler.accept(entityId, tag));
        context.setPacketHandled(true);
    }
}
