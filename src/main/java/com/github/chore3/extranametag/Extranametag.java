package com.github.chore3.extranametag;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("extranametag")
public class Extranametag {
    public static final String MOD_ID = "extranametag";

    @SuppressWarnings("removal")
    public Extranametag() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }
}
