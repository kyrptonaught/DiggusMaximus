package net.kyrptonaught.diggusmaximus;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallback;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

class Excavate {
    private BlockPos startPos;
    private PlayerEntity player;
    private Block startBlock;
    private int mined = 1;
    private ItemStack tool;
    private World world;

    Excavate(BlockPos pos, Block block, PlayerEntity player) {
        this.startPos = pos;
        this.player = player;
        this.tool = player.inventory.getMainHandStack();
        this.startBlock = block;
        this.world = player.getEntityWorld();
    }

    private Deque<BlockPos> points = new ArrayDeque<>();

    void startExcavate() {
        points.add(startPos);
        while (!points.isEmpty()) {
            spiral(points.remove());
        }
        if (DiggusMaximusMod.getOptions().playerExhaustion)
            player.addExhaustion(0.005F * mined);
    }

    private void spiral(BlockPos pos) {
        point(pos.offset(Direction.NORTH));
        point(pos.offset(Direction.EAST));
        point(pos.offset(Direction.SOUTH));
        point(pos.offset(Direction.WEST));
        point(pos.offset(Direction.UP));
        point(pos.offset(Direction.DOWN));
    }

    private void point(BlockPos pos) {
        if (player.getEntityWorld().getBlockState(pos).getBlock().equals(startBlock) && canMine(pos)) {
            points.add(pos);
            mined++;
            mine(pos);
        }
    }
    private void mine(BlockPos pos) {
        // world.getLevelProperties().getScheduledEvents().addEvent(pos.toString(), world.getTime()+ 40, (var1, var2, var3) -> System.out.println("yooo" + var3));
        if (DiggusMaximusMod.getOptions().toolDuribility)
            tool.postMine(world, world.getBlockState(pos), pos, player);
        player.incrementStat(Stats.MINED.getOrCreateStat(startBlock));
        dropStacks(world, pos);
        //startBlock.onBreak(world, pos, world.getBlockState(pos), player);
        world.clearBlockState(pos, false);
        startBlock.onBroken(world, pos, world.getBlockState(pos));
    }

    private void dropStacks(World world, BlockPos pos) {
        if (player.isCreative()) return;
        Block.getDroppedStacks(world.getBlockState(pos), (ServerWorld) world, pos, null, player, tool).forEach((stack) -> {
            if (DiggusMaximusMod.getOptions().autoPickup) {
                player.inventory.insertStack(stack);
                if (stack.getCount() > 0)
                    Block.dropStack(world, pos, stack);
            } else Block.dropStack(world, pos, stack);
        });
        startBlock.onStacksDropped(world.getBlockState(pos), world, pos, tool);
    }

    private int maxMined = Math.min(DiggusMaximusMod.getOptions().maxMinedBlocks, 256);

    private boolean canMine(BlockPos pos) {
        return mined < maxMined && isWithinDistance(pos) && toolHasDurability() && (player.isCreative() || player.isUsingEffectiveTool(player.world.getBlockState(pos)));
    }

    private double distance = Math.min(DiggusMaximusMod.getOptions().maxMineDistance + 1, 256);

    private boolean isWithinDistance(BlockPos pos) {
        return pos.isWithinDistance(startPos, distance);
    }

    private boolean toolHasDurability() {
        if (player.isCreative()) return true;
        if (!tool.isDamageable())
            return !DiggusMaximusMod.getOptions().requiresTool;
        return player.inventory.getMainHandStack().getDamage() != player.inventory.getMainHandStack().getMaxDamage();
    }
}