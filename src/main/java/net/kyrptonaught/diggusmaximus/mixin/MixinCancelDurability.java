package net.kyrptonaught.diggusmaximus.mixin;

import net.kyrptonaught.diggusmaximus.DiggingPlayerEntity;
import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ItemStack.class)
public class MixinCancelDurability {
    @Inject(method = "Lnet/minecraft/item/ItemStack;damage(ILjava/util/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "HEAD"), cancellable = true)
    private void DIGGUS$CANCELDURABILITY(int amount, Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (player != null && ((DiggingPlayerEntity) player).isExcavating() && !DiggusMaximusMod.getOptions().toolDurability)
            cir.setReturnValue(false);

    }
}
