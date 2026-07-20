package com.github.chore3.extranametag;

import com.github.chore3.extranametag.command.NametagCommand;
import com.github.chore3.extranametag.network.NametagNetwork;
import com.github.chore3.extranametag.registry.ExtranametagRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("extranametag")
public class Extranametag {
    public static final String MOD_ID = "extranametag";

    @SuppressWarnings("removal")
    public Extranametag() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(ExtranametagRegistry::onRegisterCaps);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(NametagNetwork::register);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event){
        NametagCommand.register(event.getServer().getCommands().getDispatcher());
    }
}
