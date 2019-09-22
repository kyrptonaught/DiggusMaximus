package net.kyrptonaught.diggusmaximus.mixin;


import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;
import net.kyrptonaught.diggusmaximus.StartExcavatePacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class MixinBlockBreak {


    @Inject(method = "onBreak", at = @At(value = "HEAD"), cancellable = true)
    private void DIGGUS$BLOCKBREAK(World world_1, BlockPos blockPos_1, BlockState blockState_1, PlayerEntity playerEntity_1, CallbackInfo ci) {
        if (DiggusMaximusMod.getOptions().enabled) {
            if (DiggusMaximusMod.isKeybindPressed()) {
                StartExcavatePacket.sendExcavatePacket(blockPos_1, Registry.BLOCK.getId(blockState_1.getBlock()).toString());
                ci.cancel();
            }
        }
    }
}
