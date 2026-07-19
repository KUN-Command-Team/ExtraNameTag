package com.github.chore3.extranametag.registry;

import com.github.chore3.extranametag.Extranametag;
import com.github.chore3.extranametag.network.NametagStackPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Extranametag.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class NametagClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> NametagStackPacket.setClientHandler(NametagClientState::upsert));
    }
}
