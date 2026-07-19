package com.github.chore3.extranametag.network;

import com.github.chore3.extranametag.Extranametag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.UUID;

public class NametagNetwork {
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Extranametag.MOD_ID, "main"),
            () -> "1", "1"::equals, "1"::equals
    );

    public static void register() {
        CHANNEL.registerMessage(
                0,
                NametagStackPacket.class,
                NametagStackPacket::encode,
                NametagStackPacket::decode,
                NametagStackPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }

    public static void sendResult(ServerPlayer player, CompoundTag tag, UUID entityId) {
        CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new NametagStackPacket(tag, entityId)
        );
    }
}
