package net.kyrptonaught.diggusmaximus.config;

import blue.endless.jankson.Comment;
import com.google.common.collect.Sets;
import net.kyrptonaught.kyrptconfig.config.AbstractConfigFile;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Blacklist implements AbstractConfigFile {
    @Comment("Function as whitelist instead")
    public boolean isWhitelist = false;

    @Comment("Block IDs to blacklist from being mined")
    public HashSet<String> blacklistedBlocks = new HashSet<>();

    public transient HashSet<String> lookup = new HashSet<>();

    public void generateLookup() {
        lookup.clear();
        blacklistedBlocks.forEach(entry -> {
            if (entry.startsWith("#")) {
                entry = entry.replaceAll("#", "");
                if (BlockTags.getTagGroup().contains(new Identifier(entry)))
                    BlockTags.getTagGroup().getTag(new Identifier(entry)).values().forEach(block -> {
                        lookup.add(Registry.BLOCK.getId(block).toString());
                    });
            } else lookup.add(entry);
        });
    }
}