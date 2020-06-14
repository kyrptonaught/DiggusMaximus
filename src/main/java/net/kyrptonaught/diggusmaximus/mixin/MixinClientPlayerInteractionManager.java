package net.kyrptonaught.diggusmaximus.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;
import net.kyrptonaught.diggusmaximus.StartExcavatePacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {
    @Shadow
    private GameMode gameMode;

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "sendPlayerAction", at = @At(value = "HEAD"), cancellable = true)
    private void DIGGUS$BLOCKBREAK(PlayerActionC2SPacket.Action action, BlockPos blockPos, Direction direction, CallbackInfo ci) {
        if (DiggusMaximusMod.getOptions().enabled && DiggusMaximusMod.isKeybindPressed()) {
            PlayerActionC2SPacket.Action requiredAction = this.gameMode == GameMode.CREATIVE ? PlayerActionC2SPacket.Action.START_DESTROY_BLOCK : PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK;
            if (action.equals(requiredAction) || this.client.world.getBlockState(blockPos).calcBlockBreakingDelta(this.client.player, this.client.player.world, blockPos) >= 1.0f) {
                StartExcavatePacket.sendExcavatePacket(blockPos);
                ci.cancel();
            }
        }
    }
}