package net.kyrptonaught.diggusmaximus;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Deque;

class Excavate {
    private final BlockPos startPos;
    private final PlayerEntity player;
    private Identifier startID;
    private final Item startTool;
    private int mined = 0;
    private final World world;
    private final Deque<BlockPos> points = new ArrayDeque<>();

    Excavate(BlockPos pos, PlayerEntity player) {
        this.startPos = pos;
        this.player = player;
        this.world = player.getEntityWorld();
        Block block = world.getBlockState(pos).getBlock();
        Identifier id = Registry.BLOCK.getId(block);
        if (ExcavateHelper.configAllowsMining(id.toString()))
            this.startID = id;
        this.startTool = player.getMainHandStack().getItem();
        ExcavateTypes.facing = ((BlockHitResult) player.raycast(10, 0, false)).getSide();
    }

    void startExcavate() {
        forceExcavateAt(startPos);
        if (startID == null) return;
        ((DiggingPlayerEntity) player).setExcavating(true);
        while (!points.isEmpty()) {
            spread(points.remove());
        }
        ((DiggingPlayerEntity) player).setExcavating(false);
    }


    private void spread(BlockPos pos) {
        for (BlockPos dirPos : DiggusMaximusMod.getOptions().mineDiag ? ExcavateTypes.standardDiag : ExcavateTypes.standard) {
            if (ExcavateHelper.isValidPos(dirPos))
                excavateAt(pos.add(dirPos));
        }
    }

    private void excavateAt(BlockPos pos) {
        if (mined >= ExcavateHelper.maxMined) return;
        Identifier block = Registry.BLOCK.getId(ExcavateHelper.getBlockAt(world, pos));
        if (ExcavateHelper.isTheSameBlock(startID, block, world) && ExcavateHelper.canMine(player, startTool, startPos, pos) && ((ServerPlayerEntity) player).interactionManager.tryBreakBlock(pos)) {
            points.add(pos);
            mined++;
            if (DiggusMaximusMod.getOptions().autoPickup)
                ExcavateHelper.pickupDrops(world, pos, player);
        }
    }

    private void forceExcavateAt(BlockPos pos) {
        if (((ServerPlayerEntity) player).interactionManager.tryBreakBlock(pos)) {
            points.add(pos);
            mined++;
            if (DiggusMaximusMod.getOptions().autoPickup)
                ExcavateHelper.pickupDrops(world, pos, player);
        }
    }
}