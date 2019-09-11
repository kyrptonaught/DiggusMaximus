package net.kyrptonaught.diggusmaximus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.kyrptonaught.diggusmaximus.config.ConfigManager;
import net.kyrptonaught.diggusmaximus.config.ConfigOptions;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class DiggusMaximusMod implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "diggusmaximus";
    private static final String KEY_BINDING_CATEGORY = "key.categories." + MOD_ID;
    public static FabricKeyBinding keyBinding;
    public static ConfigManager configManager = new ConfigManager();

    @Override
    public void onInitialize() {
        configManager.loadAll();
        configManager.blacklist.generateHash();
        StartExcavatePacket.registerReceivePacket();
    }

    @Override
    public void onInitializeClient() {
        keyBinding = FabricKeyBinding.Builder.create(
                new Identifier(MOD_ID, "excavate"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                KEY_BINDING_CATEGORY
        ).build();
        KeyBindingRegistry.INSTANCE.addCategory(KEY_BINDING_CATEGORY);
        KeyBindingRegistry.INSTANCE.register(keyBinding);
    }

    public static ConfigOptions getOptions() {
        return configManager.config;
    }
}
