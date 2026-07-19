package com.github.chore3.extranametag.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class NametagStack {
    private static final int MAX_LINES = 16;
    private final Deque<String> lines = new ArrayDeque<>();

    public void push(String text) {
        if (text == null || text.isEmpty()) return;
        if (lines.size() >= MAX_LINES) return;
        lines.addLast(text);
    }

    public boolean pop() {
        if (lines.isEmpty()) return false;
        lines.removeLast();
        return true;
    }

    public void clear() {
        lines.clear();
    }

    public List<String> getLinesBottomToTop() {
        return new ArrayList<>(lines);
    }

    public int size() {
        return lines.size();
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (String s : lines) {
            list.add(StringTag.valueOf(s));
        }
        tag.put("lines", list);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        lines.clear();
        ListTag list = tag.getList("lines", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            lines.addLast(list.getString(i));
        }
    }

    public void copyFrom(NametagStack other) {
        this.clear();
        for (String s : other.getLinesBottomToTop()) {
            this.push(s);
        }
    }
}
