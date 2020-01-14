package net.kyrptonaught.diggusmaximus;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Deque;

class Excavate {
    private BlockPos startPos;
    private PlayerEntity player;
    private Identifier startID;
    private Item startTool;
    private int mined = 0;
    private World world;
    private Deque<BlockPos> points = new ArrayDeque<>();

    Excavate(BlockPos pos, PlayerEntity player) {
        this.startPos = pos;
        this.player = player;
        this.world = player.getEntityWorld();
        Block block = world.getBlockState(pos).getBlock();
        Identifier id = Registry.BLOCK.getId(block);
        if (ExcavateHelper.configAllowsMining(id.toString()))
            this.startID = id;
        this.startTool = player.getMainHandStack().getItem();
    }

    void startExcavate() {
        forceExcavateAt(startPos);
        if (startID == null) return;
        DiggusMaximusMod.ExcavatingPlayers.add(player.getUuid());
        while (!points.isEmpty()) {
            spread(points.remove());
        }
        DiggusMaximusMod.ExcavatingPlayers.remove(player.getUuid());
    }

    private void spread(BlockPos pos) {
        if (DiggusMaximusMod.getOptions().mineDiag) {
            for (BlockPos dirPos : ExcavateHelper.cube) {
                if (ExcavateHelper.isValidPos(dirPos))
                    excavateAt(pos.add(dirPos));
            }
        } else {
            excavateAt(pos.north());
            excavateAt(pos.east());
            excavateAt(pos.south());
            excavateAt(pos.west());
            excavateAt(pos.up());
            excavateAt(pos.down());
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