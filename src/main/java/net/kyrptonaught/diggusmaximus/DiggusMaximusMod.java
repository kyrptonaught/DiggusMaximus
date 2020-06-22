package net.kyrptonaught.diggusmaximus;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.kyrptonaught.diggusmaximus.config.Blacklist;
import net.kyrptonaught.diggusmaximus.config.BlockCategory;
import net.kyrptonaught.diggusmaximus.config.ConfigOptions;
import net.kyrptonaught.kyrptconfig.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class DiggusMaximusMod implements ModInitializer {
    public static final String MOD_ID = "diggusmaximus";
    public static ConfigManager configManager = new ConfigManager.MultiConfigManager(MOD_ID);

    @Override
    public void onInitialize() {
        configManager.registerFile("config.json5", new ConfigOptions());
        configManager.registerFile("blacklist.json5", new Blacklist());
        configManager.registerFile("grouping.json5", new BlockCategory());
        configManager.loadAll();
        getGrouping().generateLookup();
        StartExcavatePacket.registerReceivePacket();
    }

    public static ConfigOptions getOptions() {
        return (ConfigOptions) configManager.getConfig("config.json5");
    }

    public static Blacklist getBlackList() {
        return (Blacklist) configManager.getConfig("blacklist.json5");
    }

    public static BlockCategory getGrouping() {
        return (BlockCategory) configManager.getConfig("grouping.json5");
    }

    public static InputUtil.Key keycode;

    @Environment(EnvType.CLIENT)
    public static boolean isKeybindPressed() {
        if (keycode == null)
            keycode = InputUtil.fromTranslationKey(getOptions().keybinding);
        if (keycode.getCategory() == InputUtil.Type.MOUSE)
            return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), keycode.getCode()) == 1;
        return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), keycode.getCode()) == 1;
    }

    public static Identifier getIDFromConfigLookup(Identifier blockID) {
        return getGrouping().lookup.getOrDefault(blockID, blockID);
    }
}
