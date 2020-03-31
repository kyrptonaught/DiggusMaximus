package net.kyrptonaught.diggusmaximus.mixin;

import net.kyrptonaught.diggusmaximus.DiggingPlayerEntity;
import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements DiggingPlayerEntity {
    private boolean isExcavating = false;

    @Override
    public Boolean isExcavating() {
        return isExcavating;
    }

    @Override
    public void setExcavating(boolean isExcavating) {
        this.isExcavating = isExcavating;
    }

    @Inject(method = "addExhaustion", at = @At(value = "HEAD"), cancellable = true)
    private void DIGGUS$CANCELEXHAUSTION(float exhaustion, CallbackInfo ci) {
        if (((DiggingPlayerEntity) this).isExcavating() && !DiggusMaximusMod.getOptions().playerExhaustion)
            ci.cancel();
        exhaustion *= DiggusMaximusMod.getOptions().exhaustionMultiplier;
    }
}