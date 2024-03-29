package net.kyrptonaught.diggusmaximus.mixin;

import net.kyrptonaught.diggusmaximus.DiggingPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

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

}