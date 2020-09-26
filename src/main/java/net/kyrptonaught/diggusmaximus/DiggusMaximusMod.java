package net.kyrptonaught.diggusmaximus;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.kyrptonaught.diggusmaximus.config.Blacklist;
import net.kyrptonaught.diggusmaximus.config.BlockCategory;
import net.kyrptonaught.diggusmaximus.config.ConfigOptions;
import net.kyrptonaught.kyrptconfig.config.ConfigManager;
import net.kyrptonaught.kyrptconfig.config.NonConflicting.AddNonConflictingKeyBind;
import net.kyrptonaught.kyrptconfig.config.NonConflicting.NonConflictingKeyBindData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;

public class DiggusMaximusMod implements ModInitializer, AddNonConflictingKeyBind {
    public static final String MOD_ID = "diggusmaximus";
    public static ConfigManager configManager = new ConfigManager.MultiConfigManager(MOD_ID);


    @Override
    public void onInitialize() {
        configManager.registerFile("config.json5", new ConfigOptions());
        configManager.registerFile("blacklist.json5", new Blacklist());
        configManager.registerFile("grouping.json5", new BlockCategory());
        configManager.load();
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
    public static boolean doParseKeycode = true;

    @Environment(EnvType.CLIENT)
    public static boolean isKeybindPressed() {
        if (doParseKeycode) {
            keycode = getKeybinding().orElse(null);
            doParseKeycode = false;
        }
        if (keycode == null) // Invalid key
            return false;
        if (keycode == InputUtil.UNKNOWN_KEY)
            return true; // Always pressed for empty or explicitly "key.keyboard.unknown"
        boolean pressed;
        if (keycode.getCategory() == InputUtil.Type.MOUSE)
            pressed = GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), keycode.getCode()) == 1;
        else
            pressed = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), keycode.getCode()) == 1;
        if (getOptions().invertActivation) return !pressed;
        return pressed;
    }

    public static Identifier getIDFromConfigLookup(Identifier blockID) {
        return getGrouping().lookup.getOrDefault(blockID, blockID);
    }

    @Override
    public void addKeyBinding(List<NonConflictingKeyBindData> list) {
        InputUtil.Key key = getKeybinding().orElse(InputUtil.UNKNOWN_KEY);
        NonConflictingKeyBindData bindData = new NonConflictingKeyBindData("key.diggusmaximus.excavate", "key.categories.diggusmaximus", key.getCategory(), key.getCode(), setKey -> {
            getOptions().keybinding = setKey.getTranslationKey();
            configManager.save();
            doParseKeycode = true;
        });
        list.add(bindData);
    }

    public static Optional<InputUtil.Key> getKeybinding() {
        if (getOptions().keybinding.isEmpty())
            return Optional.of(InputUtil.UNKNOWN_KEY);
        try {
            return Optional.of(InputUtil.fromTranslationKey(getOptions().keybinding));
        } catch (IllegalArgumentException e) {
            System.out.println(MOD_ID + ": unknown key entered");
            return Optional.empty();
        }
    }
}
