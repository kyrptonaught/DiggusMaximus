package net.kyrptonaught.diggusmaximus.config;


import blue.endless.jankson.Comment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Blacklist {
    @Comment("Block IDs to blacklist from being mined")
    public List<String> blacklistedBlocks = new ArrayList<>();

    public void generateHash() {
        blacklist.clear();
        blacklist.addAll(blacklistedBlocks);
    }

    public transient HashSet<String> blacklist = new HashSet<>();
}
