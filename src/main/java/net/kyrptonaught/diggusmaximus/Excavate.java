package net.kyrptonaught.diggusmaximus;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Stack;

class Excavate {
    private BlockPos startPos;
    private PlayerEntity player;
    private Block startBlock;
    private int mined = 1;
    private ItemStack tool;

    Excavate(BlockPos pos, Block block, PlayerEntity player) {
        this.startPos = pos;
        this.player = player;
        this.tool = player.inventory.getMainHandStack();
        this.startBlock = block;
    }

    private Stack<BlockPos> points = new Stack<>();

    void startExcavate() {
        spiral(startPos);
        while (!points.empty()) {
            spiral(points.remove(0));
        }
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
            points.push(pos);
            mined++;
            mine(pos);
        }
    }

    private void mine(BlockPos pos) {
        World world = player.getEntityWorld();
        if (DiggusMaximusMod.getOptions().toolDuribility)
            tool.postMine(world, world.getBlockState(pos), pos, player);
        if (DiggusMaximusMod.getOptions().playerExhaustion)
            player.addExhaustion(0.005F);
        player.incrementStat(Stats.MINED.getOrCreateStat(startBlock));
        dropStacks(world, pos);
        //startBlock.onBreak(world, pos, world.getBlockState(pos), player);
        world.clearBlockState(pos, false);
        startBlock.onBroken(world, pos, world.getBlockState(pos));
    }

    private void dropStacks(World world, BlockPos pos) {
        if (player.isCreative()) return;
        Block.getDroppedStacks(world.getBlockState(pos), (ServerWorld) world, pos, null, player, tool).forEach((stack) -> {
            if (DiggusMaximusMod.getOptions().autoPickup) player.inventory.insertStack(stack);
            else Block.dropStack(world, pos, stack);
        });
        startBlock.onStacksDropped(world.getBlockState(pos), world, pos, tool);
    }

    private boolean canMine(BlockPos pos) {
        int maxMined = Math.min(DiggusMaximusMod.getOptions().maxMinedBlocks, 256);
        return mined < maxMined && isWithinDistance(pos) && toolHasDurability() && (player.isCreative() || player.isUsingEffectiveTool(player.world.getBlockState(pos)));
    }

    private boolean isWithinDistance(BlockPos pos) {
        double distance = Math.min(DiggusMaximusMod.getOptions().maxMineDistance + 1, 256);
        return pos.isWithinDistance(startPos, distance);
    }

    private boolean toolHasDurability() {
        if (player.isCreative()) return true;
        if (!tool.isDamageable())
            return !DiggusMaximusMod.getOptions().requiresTool;
        return player.inventory.getMainHandStack().getDamage() != player.inventory.getMainHandStack().getMaxDamage();
    }
}
