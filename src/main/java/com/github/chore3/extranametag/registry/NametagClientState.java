package com.github.chore3.extranametag.registry;

import com.github.chore3.extranametag.data.NametagStack;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class NametagClientState {
    private static final Map<UUID, NametagStack> STACKS = new ConcurrentHashMap<>();

    private NametagClientState() {}

    public static void upsert(UUID entityId, CompoundTag tag) {
        NametagStack stack = new NametagStack();
        if (tag != null) {
            stack.deserializeNBT(tag);
        }
        STACKS.put(entityId, stack);
    }

    public static @Nullable NametagStack getStack(UUID entityId) {
        return STACKS.get(entityId);
    }

    public static List<String> getLines(UUID entityId) {
        NametagStack stack = STACKS.get(entityId);
        if (stack == null) {
            return Collections.emptyList();
        }
        return stack.getLinesBottomToTop();
    }

    public static void remove(UUID entityId) {
        STACKS.remove(entityId);
    }

    public static void clear() {
        STACKS.clear();
    }
}
