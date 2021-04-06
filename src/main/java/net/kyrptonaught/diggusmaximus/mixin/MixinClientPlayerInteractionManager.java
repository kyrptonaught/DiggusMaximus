package net.kyrptonaught.diggusmaximus.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.diggusmaximus.DiggusMaximusClientMod;
import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;
import net.kyrptonaught.diggusmaximus.StartExcavatePacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "breakBlock", at = @At(value = "HEAD"))
    private void DIGGUS$BREAKBLOCK(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (DiggusMaximusMod.getOptions().enabled && DiggusMaximusClientMod.activationKey.isKeybindPressed())
            DIGGUS$activate(pos, null, -1);
        /*
        else if (DiggusMaximusMod.getExcavatingShapes().enableShapes && DiggusMaximusClientMod.shapeKey.isKeybindPressed()) {
            Direction facing = null;
            HitResult result = client.player.raycast(10, 0, false);
            if (result.getType() == HitResult.Type.BLOCK)
                facing = ((BlockHitResult) result).getSide();
            int selection = DiggusMaximusMod.getExcavatingShapes().selectedShape.ordinal();
            DIGGUS$activate(pos, facing, selection);
        }

         */
    }

    @Unique
    private void DIGGUS$activate(BlockPos pos, Direction facing, int shapeSelection) {
        StartExcavatePacket.sendExcavatePacket(pos, Registry.BLOCK.getId(this.client.world.getBlockState(pos).getBlock()), facing, shapeSelection);
    }
}