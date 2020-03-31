package net.kyrptonaught.diggusmaximus.config;

import blue.endless.jankson.Comment;
import net.kyrptonaught.kyrptconfig.config.AbstractConfigFile;

import java.util.HashSet;

public class Blacklist implements AbstractConfigFile {
    @Comment("Function as whitelist instead")
    public boolean isWhitelist = false;

    @Comment("Block IDs to blacklist from being mined")
    public HashSet<String> blacklistedBlocks = new HashSet<>();

}