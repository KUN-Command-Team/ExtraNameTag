package com.github.chore3.extranametag.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NametagStackPacket {
    private final boolean success;
    private static Consumer<Boolean> clientHandler = __ -> {};

    public static void setClientHandler(Consumer<Boolean> handler) {
        clientHandler = handler;
    }

    public NametagStackPacket(boolean success) {
        this.success = success;
    }

    public static void encode(NametagStackPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.success);
    }

    public static NametagStackPacket decode(FriendlyByteBuf buf) {
        return new NametagStackPacket(buf.readBoolean());
    }

    public static void handle(NametagStackPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        boolean success = packet.success;
        context.enqueueWork(() -> clientHandler.accept(success));
        context.setPacketHandled(true);
    }
}
