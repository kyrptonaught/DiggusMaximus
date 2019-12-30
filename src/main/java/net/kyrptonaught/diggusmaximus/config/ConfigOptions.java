package net.kyrptonaught.diggusmaximus.config;

import blue.endless.jankson.Comment;

public class ConfigOptions implements AbstractConfigFile {
    @Comment("Mod enabled or disabled")
    public boolean enabled = true;

    @Comment("Activation key")
    public String keybinding = "key.keyboard.grave.accent";

    @Comment("Should mine diagonally")
    public boolean mineDiag = true;

    @Comment("Maximum number of blocks to mine")
    public int maxMinedBlocks = 40;

    @Comment("Maximum distance from start to mine")
    public int maxMineDistance = 10;

    @Comment("Automatically pick up drops")
    public boolean autoPickup = true;

    @Comment("Tool required to excavate")
    public boolean requiresTool = false;

    @Comment("Stop before tool breaks")
    public boolean dontBreakTool = true;

    @Comment("Stop excavating when tool breaks")
    public boolean stopOnToolBreak = true;

    @Comment("Should tool take durability")
    public boolean toolDuribility = true;

    @Comment("Should player get exhaustion")
    public boolean playerExhaustion = true;
}
