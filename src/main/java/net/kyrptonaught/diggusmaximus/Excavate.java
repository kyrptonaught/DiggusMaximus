package net.kyrptonaught.diggusmaximus;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.stream.Collectors;

class Excavate {
    private BlockPos startPos;
    private PlayerEntity player;
    private Block startBlock;
    private int mined = 1;
    private ItemStack tool;
    private World world;
    private static final Set<BlockPos> cube = BlockPos.stream(-1, -1, -1, 1, 1, 1).map(BlockPos::toImmutable).collect(Collectors.toSet());
    private final int maxMined = Math.min(DiggusMaximusMod.getOptions().maxMinedBlocks, 2048);
    private final double maxDistance = Math.min(DiggusMaximusMod.getOptions().maxMineDistance + 1, 128);
    private Deque<BlockPos> points = new ArrayDeque<>();

    Excavate(BlockPos pos, Block block, PlayerEntity player) {
        this.startPos = pos;
        this.player = player;
        this.tool = player.inventory.getMainHandStack();
        this.startBlock = block;
        this.world = player.getEntityWorld();
    }

    void startExcavate() {
        points.add(startPos);
        while (!points.isEmpty()) {
            spread(points.remove());
        }
        if (DiggusMaximusMod.getOptions().playerExhaustion)
            player.addExhaustion(0.005F * mined);
    }

    private void spread(BlockPos pos) {
        if (DiggusMaximusMod.getOptions().mineDiag) {
            for (BlockPos dirPos : cube) {
                if (isValidPos(dirPos))
                    point(pos.add(dirPos));
            }
        } else {
            point(pos.north());
            point(pos.east());
            point(pos.south());
            point(pos.west());
            point(pos.up());
            point(pos.down());
        }
    }

    private boolean isValidPos(BlockPos pos) {
        return (Math.abs(pos.getX()) + Math.abs(pos.getY()) + Math.abs(pos.getZ())) != 0;
    }

    private void point(BlockPos pos) {
        if (player.getEntityWorld().getBlockState(pos).getBlock().equals(startBlock) && canMine(pos)) {
            points.add(pos);
            mined++;
            mine(pos);
        }
    }

    private void mine(BlockPos pos) {
        if (DiggusMaximusMod.getOptions().toolDuribility)
            tool.postMine(world, world.getBlockState(pos), pos, player);
        player.incrementStat(Stats.MINED.getOrCreateStat(startBlock));
        dropStacks(world, pos);
        world.clearBlockState(pos, false);
        startBlock.onBroken(world, pos, world.getBlockState(pos));
    }

    private void dropStacks(World world, BlockPos pos) {
        if (player.isCreative()) return;
        LootContext.Builder lootContext$Builder_1 = (new LootContext.Builder((ServerWorld) world)).setRandom(((ServerWorld) world).random).put(LootContextParameters.POSITION, pos).put(LootContextParameters.TOOL, tool).put(LootContextParameters.THIS_ENTITY, player).putNullable(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(pos));
        startBlock.getDroppedStacks(world.getBlockState(pos), lootContext$Builder_1).forEach((stack) -> {
            if (DiggusMaximusMod.getOptions().autoPickup) {
                player.inventory.insertStack(stack);
                if (stack.getCount() > 0)
                    Block.dropStack(world, pos, stack);
            } else Block.dropStack(world, pos, stack);
        });
        startBlock.onStacksDropped(world.getBlockState(pos), world, pos, tool);
    }


    private boolean canMine(BlockPos pos) {
        return mined < maxMined && isWithinDistance(pos) && toolHasDurability() && (player.isCreative() || player.isUsingEffectiveTool(player.world.getBlockState(pos)));
    }

    private boolean isWithinDistance(BlockPos pos) {
        return pos.isWithinDistance(startPos, maxDistance);
    }

    private boolean toolHasDurability() {
        if (player.isCreative()) return true;
        if (!tool.isDamageable())
            return !DiggusMaximusMod.getOptions().requiresTool;
        return player.inventory.getMainHandStack().getDamage() != player.inventory.getMainHandStack().getMaxDamage();
    }
}