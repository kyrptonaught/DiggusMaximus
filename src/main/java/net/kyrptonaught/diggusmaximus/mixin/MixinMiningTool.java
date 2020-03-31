package net.kyrptonaught.diggusmaximus.mixin;

import net.kyrptonaught.diggusmaximus.DiggingPlayerEntity;
import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(MiningToolItem.class)
public class MixinMiningTool {

    @Redirect(method = "postMine", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"))
    private void DIGGUS$CANCELDURABILITY(ItemStack itemStack, int amount, LivingEntity entity, Consumer<LivingEntity> breakCallback) {
        if (!(entity instanceof PlayerEntity) || !((DiggingPlayerEntity) entity).isExcavating() || DiggusMaximusMod.getOptions().toolDurability)
            itemStack.damage(1, entity, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
    }
}
