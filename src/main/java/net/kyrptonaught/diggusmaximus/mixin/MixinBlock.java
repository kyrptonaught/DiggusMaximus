package net.kyrptonaught.diggusmaximus.mixin;

import net.kyrptonaught.diggusmaximus.DiggingPlayerEntity;
import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Block.class)
public class MixinBlock {

    @Redirect(method = "afterBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"))
    private void DIGGUS$CANCELEXHAUSTION(PlayerEntity player, float exhaustion) {
        if (!((DiggingPlayerEntity) player).isExcavating()) {
            player.addExhaustion(exhaustion);
            return;
        }
        if (!DiggusMaximusMod.getOptions().playerExhaustion)
            return;

        player.addExhaustion(exhaustion * DiggusMaximusMod.getOptions().exhaustionMultiplier);
    }
}
