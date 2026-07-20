package com.github.chore3.extranametag.data;

import com.github.chore3.extranametag.Extranametag;
import com.github.chore3.extranametag.network.NametagNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Extranametag.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class NametagEvents {
    private static final ResourceLocation ID = new ResourceLocation(Extranametag.MOD_ID, "nametag_stack");
    private NametagEvents() {}

    @SubscribeEvent
    public static void onAttachCaps(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity) {
            event.addCapability(ID, new NametagProvider());
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(NametagCapability.NAMETAG_STACK).ifPresent(oldCap -> {
            event.getEntity().getCapability(NametagCapability.NAMETAG_STACK).ifPresent(newCap -> {
                newCap.copyFrom(oldCap);
            });
        });
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        Entity tracker = event.getEntity();
        if (target instanceof LivingEntity livingTarget && tracker instanceof ServerPlayer serverPlayer) {
            sendStackToPlayer(serverPlayer, livingTarget);
        }
    }

    @SubscribeEvent
    public static void onGameModeChanged(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        serverPlayer.getCapability(NametagCapability.NAMETAG_STACK).ifPresent(cap ->
                NametagNetwork.sendResultToTracking(serverPlayer, cap.serializeNBT(), serverPlayer.getUUID())
        );
    }

    private static void sendStackToPlayer(ServerPlayer player, LivingEntity target) {
        target.getCapability(NametagCapability.NAMETAG_STACK).ifPresent(cap ->
                NametagNetwork.sendResult(player, cap.serializeNBT(), target.getUUID())
        );
    }
}
