package net.kyrptonaught.diggusmaximus.mixin;

import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;
import net.kyrptonaught.diggusmaximus.Excavate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {

    @Shadow
    @Final
    protected ServerPlayerEntity player;

    @Shadow
    protected ServerWorld world;

    @Redirect(method = "finishMining", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;tryBreakBlock(Lnet/minecraft/util/math/BlockPos;)Z"))
    public boolean sneakBreak(ServerPlayerInteractionManager serverPlayerInteractionManager, BlockPos pos) {
        if (DiggusMaximusMod.getOptions().sneakToExcavate) {
            Identifier blockId = Registry.BLOCK.getId(this.world.getBlockState(pos).getBlock());
            boolean result = serverPlayerInteractionManager.tryBreakBlock(pos);
            if (result) {
                if (DiggusMaximusMod.getOptions().sneakToExcavate && player.isSneaking()) {
                    if (pos.isWithinDistance(player.getPos(), 10)) {
                        new Excavate(pos, blockId, player, null).startExcavate(-1);
                    }
                }
            }
            return result;
        }
        return serverPlayerInteractionManager.tryBreakBlock(pos);
    }
}
