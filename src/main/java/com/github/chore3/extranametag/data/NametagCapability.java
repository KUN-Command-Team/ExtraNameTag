package com.github.chore3.extranametag.data;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class NametagCapability {
    private NametagCapability() {}
    public static final Capability<NametagStack> NAMETAG_STACK =
            CapabilityManager.get(new CapabilityToken<NametagStack>() {});
}
