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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;

public class DiggusMaximusMod implements ModInitializer, AddNonConflictingKeyBind {
    public static final String MOD_ID = "diggusmaximus";
    public static ConfigManager configManager = new ConfigManager.MultiConfigManager(MOD_ID);
    private static final Logger LOGGER = LogManager.getLogger();

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
    private static boolean parse = true;

    @Environment(EnvType.CLIENT)
    public static boolean isKeybindPressed() {
        if (parse) {
            if (getOptions().keybinding.isEmpty())
                keycode = InputUtil.UNKNOWN_KEY;
            else
                keycode = getKeybinding().orElse(null);
            parse = false;
        }
        if (keycode == null) // Invalid key
            return false;
        if (keycode == InputUtil.UNKNOWN_KEY)
            return true; // Empty or explicitly "key.keyboard.unknown"
        if (keycode.getCategory() == InputUtil.Type.MOUSE)
            return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), keycode.getCode()) == 1;
        return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), keycode.getCode()) == 1;
    }

    public static Identifier getIDFromConfigLookup(Identifier blockID) {
        return getGrouping().lookup.getOrDefault(blockID, blockID);
    }

    @Override
    public void addKeyBinding(List<NonConflictingKeyBindData> list) {
        getKeybinding().ifPresent(key -> {
            NonConflictingKeyBindData bindData = new NonConflictingKeyBindData("key.diggusmaximus.excavate", "key.categories.diggusmaximus", key.getCategory(), key.getCode(), setKey -> {
                getOptions().keybinding = setKey.getTranslationKey();
                configManager.save();
                keycode = null;
            });
            list.add(bindData);
        });
    }

    public static Optional<InputUtil.Key> getKeybinding() {
        try {
            return Optional.of(InputUtil.fromTranslationKey(getOptions().keybinding));
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage()); // "Unknown key name: ..."
            return Optional.empty();
        }
    }
}
