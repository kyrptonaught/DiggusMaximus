package net.kyrptonaught.diggusmaximus.client;

import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;
import net.kyrptonaught.kyrptconfig.config.ConfigDefaultCopyable;
import net.kyrptonaught.kyrptconfig.keybinding.CustomKeyBinding;
import net.minecraft.client.util.InputUtil;

public class DiggusKeyBinding extends CustomKeyBinding {
    public boolean respectsInvert;

    public DiggusKeyBinding(boolean respectsInvert, boolean unknownIsActivated, String defaultKey) {
        super(DiggusMaximusMod.MOD_ID, unknownIsActivated);
        this.respectsInvert = respectsInvert;
        this.defaultKey = defaultKey;
    }

    public DiggusKeyBinding copyKeyFrom(CustomKeyBinding other) {
        setRaw(other.rawKey);
        return this;
    }

    public boolean isKeybindPressed() {
        boolean pressed = super.isKeybindPressed();
        if (parsedKey == null) // Invalid key
            return false;
        if (parsedKey == InputUtil.UNKNOWN_KEY)
            return unknownIsActivated; // Always pressed for empty or explicitly "key.keyboard.unknown"
        if (respectsInvert && DiggusMaximusMod.getOptions().invertActivation) return !pressed;
        return pressed;
    }

    @Override
    public void copyFromDefault(ConfigDefaultCopyable otherDefault) {
        super.copyFromDefault(otherDefault);
        this.respectsInvert = ((DiggusKeyBinding) otherDefault).respectsInvert;
    }
}
