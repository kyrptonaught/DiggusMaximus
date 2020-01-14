package net.kyrptonaught.diggusmaximus;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.kyrptonaught.diggusmaximus.config.ConfigManager;
import net.kyrptonaught.diggusmaximus.config.ConfigOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class DiggusMaximusMod implements ModInitializer {
    public static final String MOD_ID = "diggusmaximus";
    public static ConfigManager configManager = new ConfigManager();

    public static HashSet<UUID> ExcavatingPlayers = new HashSet<>();

    @Override
    public void onInitialize() {
        configManager.loadAll();
        configManager.getBlackList().generateHash();
        configManager.getGrouping().generateLookup();
        StartExcavatePacket.registerReceivePacket();
    }


    public static InputUtil.KeyCode keycode;

    @Environment(EnvType.CLIENT)
    public static boolean isKeybindPressed() {
        if (keycode == null)
            keycode = InputUtil.fromName(getOptions().keybinding);
        if (keycode.getCategory() == InputUtil.Type.MOUSE)
            return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), keycode.getKeyCode()) == 1;
        return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), keycode.getKeyCode()) == 1;
    }

    public static ConfigOptions getOptions() {
        return configManager.getOptions();
    }

    public static Identifier getIDFromConfigLookup(Identifier blockID) {
        return configManager.getGrouping().lookup.getOrDefault(blockID, blockID);
    }
}
