package net.kyrptonaught.diggusmaximus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.kyrptonaught.kyrptconfig.keybinding.CustomKeyBinding;
import net.kyrptonaught.kyrptconfig.keybinding.DisplayOnlyKeyBind;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class DiggusMaximusClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        System.out.println(getActivationKey().defaultKey);

        ClientTickEvents.END_WORLD_TICK.register(clientWorld -> {
            if (DiggusMaximusMod.getExcavatingShapes().enableShapes && getCycleKey().wasPressed()) {
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

        KeyBindingHelper.registerKeyBinding(new DisplayOnlyKeyBind(
                "key.diggusmaximus.keybind.excavate",
                "key.categories.diggusmaximus",
                getActivationKey(),
                setKey -> DiggusMaximusMod.configManager.save()
        ));
        KeyBindingHelper.registerKeyBinding(new DisplayOnlyKeyBind(
                "key.diggusmaximus.keybind.shapeexcavate",
                "key.categories.diggusmaximus",
                getShapeKey(),
                setKey -> DiggusMaximusMod.configManager.save()
        ));
        KeyBindingHelper.registerKeyBinding(new DisplayOnlyKeyBind(
                "key.diggusmaximus.keybind.cycleshape",
                "key.categories.diggusmaximus",
                getCycleKey(),
                setKey -> DiggusMaximusMod.configManager.save()
        ));
    }

    public static CustomKeyBinding getActivationKey() {
        return DiggusMaximusMod.getOptions().keybinding;
    }

    public static CustomKeyBinding getShapeKey() {
        return DiggusMaximusMod.getExcavatingShapes().shapeKey;
    }

    public static CustomKeyBinding getCycleKey() {
        return DiggusMaximusMod.getExcavatingShapes().cycleKey;
    }
}
