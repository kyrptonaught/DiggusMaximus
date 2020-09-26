package net.kyrptonaught.diggusmaximus.config;

import blue.endless.jankson.Comment;
import net.kyrptonaught.kyrptconfig.config.AbstractConfigFile;

import java.util.HashSet;

public class ConfigOptions implements AbstractConfigFile {
    @Comment("Mod enabled or disabled")
    public boolean enabled = true;

    @Comment("Activation key")
    public String keybinding = "key.keyboard.grave.accent";

    @Comment("Inverts the keybinding activation")
    public boolean invertActivation = false;

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
    public boolean toolDurability = true;

    @Comment("Should player get exhaustion")
    public boolean playerExhaustion = true;

    @Comment("Multiply exhaustion when excavating")
    public float exhaustionMultiplier = 1.0f;

    @Comment("Other items to be considered tools ie: \"minecraft:stick\"")
    public HashSet<String> tools = new HashSet<>();
}
