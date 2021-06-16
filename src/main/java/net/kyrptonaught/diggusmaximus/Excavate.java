package net.kyrptonaught.diggusmaximus;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Deque;

public class Excavate {
    private final BlockPos startPos;
    private final PlayerEntity player;
    private Identifier startID;
    private final Item startTool;
    private int mined = 0;
    private final World world;
    private final Deque<BlockPos> points = new ArrayDeque<>();

    private final Direction facing;
    private int shapeSelection = -1;

    public Excavate(BlockPos pos, Identifier blockID, PlayerEntity player, Direction facing) {
        this.startPos = pos;
        this.player = player;
        this.world = player.getEntityWorld();
        if (ExcavateHelper.configAllowsMining(blockID.toString()))
            this.startID = blockID;

        this.startTool = player.getMainHandStack().getItem();
        this.facing = facing;
    }

    public void startExcavate(int shapeSelection) {
        this.shapeSelection = shapeSelection;
        forceExcavateAt(startPos);
        if (startID == null) return;
        ((DiggingPlayerEntity) player).setExcavating(true);
        while (!points.isEmpty()) {
            spread(points.remove());
        }
        ((DiggingPlayerEntity) player).setExcavating(false);
    }

    private void spread(BlockPos pos) {
        for (BlockPos dirPos : ExcavateTypes.getSpreadType(shapeSelection, facing, startPos, pos)) {
            if (ExcavateHelper.isValidPos(dirPos))
                excavateAt(pos.add(dirPos));
        }
    }

    private void excavateAt(BlockPos pos) {
        if (mined >= ExcavateHelper.maxMined) return;
        Identifier block = Registry.BLOCK.getId(ExcavateHelper.getBlockAt(world, pos));
        if (ExcavateHelper.isTheSameBlock(startID, block, world, shapeSelection) && ExcavateHelper.canMine(player, startTool, startPos, pos) && isExcavatingAllowed(pos)) {
            points.add(pos);
            mined++;
            if (DiggusMaximusMod.getOptions().autoPickup)
                ExcavateHelper.pickupDrops(world, pos, player);
        }
    }

    private boolean isExcavatingAllowed(BlockPos pos) {
        return PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world, player, pos, world.getBlockState(pos), world.getBlockEntity(pos)) && ((ServerPlayerEntity) player).interactionManager.tryBreakBlock(pos);
    }

    private void forceExcavateAt(BlockPos pos) {
        points.add(pos);
        mined++;
        if (DiggusMaximusMod.getOptions().autoPickup)
            ExcavateHelper.pickupDrops(world, pos, player);
    }
}