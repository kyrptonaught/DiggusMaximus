package net.kyrptonaught.diggusmaximus.client;

import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class KeyBinding {
    public boolean respectsInvert;
    public boolean unknownIsActivated;
    public String rawKey;
    public InputUtil.Key keycode;
    public boolean doParseKeycode = true;

    public KeyBinding(boolean respectsInvert, boolean unknownIsActivated) {
        this.respectsInvert = respectsInvert;
        this.unknownIsActivated = unknownIsActivated;
    }

    public void setRaw(String key) {
        rawKey = key;
        doParseKeycode = true;
        holding = false;
    }

    boolean holding = false;

    public boolean wasPressed() {
        boolean pressed = isKeybindPressed();
        if (!holding) {
            holding = pressed;
            return pressed;
        }
        if (!pressed)
            holding = false;
        return false;
    }

    public boolean isKeybindPressed() {
        if (doParseKeycode) {
            keycode = getKeybinding().orElse(null);
            doParseKeycode = false;
        }
        if (keycode == null) // Invalid key
            return false;
        if (keycode == InputUtil.UNKNOWN_KEY)
            return unknownIsActivated; // Always pressed for empty or explicitly "key.keyboard.unknown"
        boolean pressed;
        if (keycode.getCategory() == InputUtil.Type.MOUSE)
            pressed = GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), keycode.getCode()) == 1;
        else
            pressed = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), keycode.getCode()) == 1;
        if (respectsInvert && DiggusMaximusMod.getOptions().invertActivation) return !pressed;
        return pressed;
    }

    public Optional<InputUtil.Key> getKeybinding() {
        if (rawKey.isEmpty())
            return Optional.of(InputUtil.UNKNOWN_KEY);
        try {
            return Optional.of(InputUtil.fromTranslationKey(rawKey));
        } catch (IllegalArgumentException e) {
            System.out.println(DiggusMaximusMod.MOD_ID + ": unknown key entered");
            return Optional.empty();
        }
    }
}
