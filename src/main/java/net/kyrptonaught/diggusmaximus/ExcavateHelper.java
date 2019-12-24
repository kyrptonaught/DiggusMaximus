package net.kyrptonaught.diggusmaximus;

import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExcavateHelper {
    static final Set<BlockPos> cube = BlockPos.stream(-1, -1, -1, 1, 1, 1).map(BlockPos::toImmutable).collect(Collectors.toSet());
    private static int maxMined = Math.min(DiggusMaximusMod.getOptions().maxMinedBlocks, 2048);
    private static double maxDistance = Math.min(DiggusMaximusMod.getOptions().maxMineDistance + 1, 128);

    public static void resetMaximums() {
        maxMined = Math.min(DiggusMaximusMod.getOptions().maxMinedBlocks, 2048);
        maxDistance = Math.min(DiggusMaximusMod.getOptions().maxMineDistance + 1, 128);
    }

    static void pickupDrops(World world, BlockPos pos, PlayerEntity player) {
        List<ItemEntity> drops = world.getEntities(ItemEntity.class, new Box(pos), null);
        drops.forEach(item -> {
            ItemStack stack = item.getStack();
            player.inventory.insertStack(stack);
            if (stack.getCount() <= 0)
                item.remove();
        });
    }

    static boolean configAllowsMining(String blockID) {
        return DiggusMaximusMod.configManager.getBlackList().isWhitelist == DiggusMaximusMod.configManager.getBlackList().blacklist.contains(blockID);
    }

    static boolean isValidPos(BlockPos pos) {
        return (Math.abs(pos.getX()) + Math.abs(pos.getY()) + Math.abs(pos.getZ())) != 0;
    }

    static Block getBlockAt(World world, BlockPos pos) {
        return (world.getBlockState(pos).getBlock());
    }

    static boolean canMine(PlayerEntity player, int mined, BlockPos startPos, BlockPos pos) {
        return mined < maxMined && isWithinDistance(startPos, pos) && canUseTool(player);
    }

    private static boolean isWithinDistance(BlockPos startPos, BlockPos pos) {
        return pos.isWithinDistance(startPos, maxDistance);
    }

    private static boolean canUseTool(PlayerEntity player) {
        if (player.isCreative()) return true;
        if (!player.inventory.getMainHandStack().isDamageable())
            return !DiggusMaximusMod.getOptions().requiresTool;
        return player.inventory.getMainHandStack().getDamage() != player.inventory.getMainHandStack().getMaxDamage();
    }
}
