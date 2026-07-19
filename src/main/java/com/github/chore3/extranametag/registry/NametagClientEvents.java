package com.github.chore3.extranametag.registry;

import com.github.chore3.extranametag.Extranametag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Extranametag.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class NametagClientEvents {
    private NametagClientEvents() {}

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        NametagClientState.clear();
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            NametagClientState.remove(event.getEntity().getUUID());
        }
    }
}

