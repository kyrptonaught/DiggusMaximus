package net.kyrptonaught.diggusmaximus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.kyrptonaught.diggusmaximus.client.KeyBinding;
import net.kyrptonaught.kyrptconfig.config.NonConflicting.AddNonConflictingKeyBind;
import net.kyrptonaught.kyrptconfig.config.NonConflicting.NonConflictingKeyBindData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;

import java.util.List;

@Environment(EnvType.CLIENT)
public class DiggusMaximusClientMod implements ClientModInitializer, AddNonConflictingKeyBind {
    public static KeyBinding activationKey, shapeKey, cycleShapeKey;

    @Override
    public void onInitializeClient() {
        activationKey = new KeyBinding(true, true);
        activationKey.setRaw(DiggusMaximusMod.getOptions().keybinding);

        shapeKey = new KeyBinding(false, false);
        cycleShapeKey = new KeyBinding(false, false);
        shapeKey.setRaw(DiggusMaximusMod.getExcavatingShapes().shapeKey);
        cycleShapeKey.setRaw(DiggusMaximusMod.getExcavatingShapes().cycleKey);
        ClientTickEvents.END_WORLD_TICK.register(clientWorld -> {
            if (DiggusMaximusMod.getExcavatingShapes().enableShapes && cycleShapeKey.wasPressed()) {
                int currentSelectiong = DiggusMaximusMod.getExcavatingShapes().selectedShape.ordinal();
                if (MinecraftClient.getInstance().player.isSneaking())
                    currentSelectiong--;
                else currentSelectiong++;
                if (currentSelectiong >= ExcavateTypes.shape.values().length)
                    currentSelectiong = 0;
                else if (currentSelectiong < 0)
                    currentSelectiong = ExcavateTypes.shape.values().length - 1;
                DiggusMaximusMod.getExcavatingShapes().selectedShape = ExcavateTypes.shape.values()[currentSelectiong];
                MinecraftClient.getInstance().player.sendMessage(new TranslatableText("diggusmaximus.shape." + ExcavateTypes.shape.values()[currentSelectiong]), true);
                DiggusMaximusMod.configManager.save();
            }
        });

    }

    @Override
    public void addKeyBinding(List<NonConflictingKeyBindData> list) {
        InputUtil.Key activation = activationKey.getKeybinding().orElse(InputUtil.UNKNOWN_KEY);
        NonConflictingKeyBindData acivationData = new NonConflictingKeyBindData("key.diggusmaximus.keybind.excavate", "key.categories.diggusmaximus", activation.getCategory(), activation.getCode(), setKey -> {
            DiggusMaximusMod.getOptions().keybinding = setKey.getTranslationKey();
            DiggusMaximusMod.configManager.save();
            activationKey.setRaw(setKey.getTranslationKey());
        });
        list.add(acivationData);

        InputUtil.Key shape = shapeKey.getKeybinding().orElse(InputUtil.UNKNOWN_KEY);
        NonConflictingKeyBindData shapeData = new NonConflictingKeyBindData("key.diggusmaximus.keybind.shapeexcavate", "key.categories.diggusmaximus", shape.getCategory(), shape.getCode(), setKey -> {
            DiggusMaximusMod.getExcavatingShapes().shapeKey = setKey.getTranslationKey();
            DiggusMaximusMod.configManager.save();
            shapeKey.setRaw(setKey.getTranslationKey());
        });
        list.add(shapeData);

        InputUtil.Key cycle = cycleShapeKey.getKeybinding().orElse(InputUtil.UNKNOWN_KEY);
        NonConflictingKeyBindData cycleData = new NonConflictingKeyBindData("key.diggusmaximus.keybind.cycleshape", "key.categories.diggusmaximus", cycle.getCategory(), cycle.getCode(), setKey -> {
            DiggusMaximusMod.getExcavatingShapes().cycleKey = setKey.getTranslationKey();
            DiggusMaximusMod.configManager.save();
            cycleShapeKey.setRaw(setKey.getTranslationKey());
        });
        list.add(cycleData);
    }
}
