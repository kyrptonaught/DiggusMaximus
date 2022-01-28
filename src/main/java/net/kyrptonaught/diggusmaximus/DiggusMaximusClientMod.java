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

/*
        WorldRenderEvents.BLOCK_OUTLINE.register(new Identifier(DiggusMaximusMod.MOD_ID, "highlight"), (worldRenderContext, blockOutlineContext) -> {
            VertexConsumer outlineVertexConsumerProvider3 = worldRenderContext.consumers().getBuffer(RenderLayer.getLines());
            drawOutLine(worldRenderContext.matrixStack(), outlineVertexConsumerProvider3, worldRenderContext.world(), new BlockPos(0, 70, 0), worldRenderContext.camera().getPos());
            return true;
        });

 */
    }

    /*
        private void drawOutLine(MatrixStack matrices, VertexConsumer vertexConsumer, World world, BlockPos pos, Vec3d cameraPos) {
            double x = (double) pos.getX() - cameraPos.getX(), y = (double) pos.getY() - cameraPos.getY(), z = (double) pos.getZ() - cameraPos.getZ();
            float red = .5f, green = .5f, blue = .5f, alpha = .5f;
            MatrixStack.Entry entry = matrices.peek();
            VoxelShape shape = VoxelShapes.fullCube();//world.getBlockState(pos).getOutlineShape(world, pos);
            shape.forEachEdge((k, l, m, n, o, p) -> {
                float q = (float) (n - k);
                float r = (float) (o - l);
                float s = (float) (p - m);
                float t = MathHelper.sqrt(q * q + r * r + s * s);
                vertexConsumer.vertex(entry.getPositionMatrix(), (float) (k + x), (float) (l + y), (float) (m + z)).color(red, green, blue, alpha).normal(entry.getNormalMatrix(), q /= t, r /= t, s /= t).next();
                vertexConsumer.vertex(entry.getPositionMatrix(), (float) (n + x), (float) (o + y), (float) (p + z)).color(red, green, blue, alpha).normal(entry.getNormalMatrix(), q, r, s).next();
            });
        }
     */
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
