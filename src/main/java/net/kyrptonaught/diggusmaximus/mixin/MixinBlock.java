package net.kyrptonaught.diggusmaximus.mixin;

import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Block.class)
public class MixinBlock {

    @Redirect(method = "afterBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"))
    private void DIGGUS$CANCELEXHAUSTION(PlayerEntity playerEntity, float exhaustion) {
        if (!DiggusMaximusMod.ExcavatingPlayers.contains(playerEntity.getUuid()) || DiggusMaximusMod.getOptions().playerExhaustion)
            playerEntity.addExhaustion(exhaustion);
    }
}