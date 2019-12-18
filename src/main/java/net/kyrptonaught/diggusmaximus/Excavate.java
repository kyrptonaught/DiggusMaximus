package net.kyrptonaught.diggusmaximus;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

class Excavate {
    private BlockPos startPos;
    private PlayerEntity player;
    private Identifier startID;
    private int mined = 1;
    private World world;
    private Deque<BlockPos> points = new ArrayDeque<>();

    Excavate(BlockPos pos, PlayerEntity player) {
        this.startPos = pos;
        this.player = player;
        this.world = player.getEntityWorld();
        Block block = world.getBlockState(pos).getBlock();
        if (ExcavateHelper.configAllowsMining(Registry.BLOCK.getId(block).toString()))
            this.startID = DiggusMaximusMod.getIDFromConfigLookup(Registry.BLOCK.getId(block));
        TagRegistry.block(startID).entries().forEach(System.out::println);
    }

    void startExcavate() {
        if (startID == null) return;
        points.add(startPos);
        while (!points.isEmpty()) {
            spread(points.remove());
        }
        if (DiggusMaximusMod.getOptions().playerExhaustion)
            player.addExhaustion(0.005F * mined);
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
        Identifier block = DiggusMaximusMod.getIDFromConfigLookup(Registry.BLOCK.getId(ExcavateHelper.getBlockAt(world, pos)));
        if (block.equals(startID) && ExcavateHelper.canMine(player, mined, startPos, pos) && ((ServerPlayerEntity) player).interactionManager.tryBreakBlock(pos)) {
            points.add(pos);
            mined++;
            if (DiggusMaximusMod.getOptions().autoPickup) {
                List<ItemEntity> drops = world.getEntities(ItemEntity.class, new Box(pos), null);
                drops.forEach(item -> {
                    ItemStack stack = item.getStack();
                    player.inventory.insertStack(stack);
                    if (stack.getCount() <= 0)
                        item.remove();
                });
            }
        }
    }
}