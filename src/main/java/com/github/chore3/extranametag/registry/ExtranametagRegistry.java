package com.github.chore3.extranametag.registry;

import com.github.chore3.extranametag.data.NametagStack;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public final class ExtranametagRegistry {
    private ExtranametagRegistry() {}
    public static void onRegisterCaps(RegisterCapabilitiesEvent event) {
        event.register(NametagStack.class);
    }
}
