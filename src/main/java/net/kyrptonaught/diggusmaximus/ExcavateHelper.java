package net.kyrptonaught.diggusmaximus;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ExcavateHelper {
    static int maxMined = Math.min(DiggusMaximusMod.getOptions().maxMinedBlocks, 2048);
    private static double maxDistance = Math.min(DiggusMaximusMod.getOptions().maxMineDistance + 1, 128);

    public static void resetMaximums() {
        maxMined = Math.min(DiggusMaximusMod.getOptions().maxMinedBlocks, 2048);
        maxDistance = Math.min(DiggusMaximusMod.getOptions().maxMineDistance + 1, 128);
    }

    static void pickupDrops(World world, BlockPos pos, PlayerEntity player) {
        List<ItemEntity> drops = world.getEntitiesByClass(ItemEntity.class, new Box(pos), null);
        drops.forEach(item -> {
            ItemStack stack = item.getStack();
            player.inventory.insertStack(stack);
            if (stack.getCount() <= 0)
                item.remove();
        });

    }

    static boolean isTheSameBlock(Identifier startID, Identifier newID, World world) {
        if (DiggusMaximusMod.getGrouping().tagGrouping) {
            Block newBlock = Registry.BLOCK.get(newID);
            for (Identifier tagID : getTagsFor(world.getTagManager().getBlocks(), Registry.BLOCK.get(startID))) {
                if (TagRegistry.block(tagID).contains(newBlock)) {
                    newID = startID;
                    break;
                }
            }
        }
        if (DiggusMaximusMod.getGrouping().customGrouping) {
            newID = DiggusMaximusMod.getIDFromConfigLookup(newID);
            startID = DiggusMaximusMod.getIDFromConfigLookup(startID);
        }
        return startID.equals(newID);
    }

    // copied from: net.minecraft.tag.TagContainer:getTagsFor
    static Collection<Identifier> getTagsFor(TagGroup<Block> container, Block object) {
        List<Identifier> list = Lists.newArrayList();
        for (Map.Entry<Identifier, Tag<Block>> identifierTagEntry : container.getTags().entrySet()) {
            if (identifierTagEntry.getValue().contains(object)) {
                list.add(identifierTagEntry.getKey());
            }
        }
        return list;
    }

    static boolean configAllowsMining(String blockID) {
        return DiggusMaximusMod.getBlackList().isWhitelist == DiggusMaximusMod.getBlackList().blacklistedBlocks.contains(blockID);
    }

    static boolean isValidPos(BlockPos pos) {
        return (Math.abs(pos.getX()) + Math.abs(pos.getY()) + Math.abs(pos.getZ())) != 0;
    }

    static Block getBlockAt(World world, BlockPos pos) {
        return (world.getBlockState(pos).getBlock());
    }

    static boolean canMine(PlayerEntity player, Item tool, BlockPos startPos, BlockPos pos) {
        return isWithinDistance(startPos, pos) && checkTool(player, tool);
    }

    private static boolean isWithinDistance(BlockPos startPos, BlockPos pos) {
        return pos.isWithinDistance(startPos, maxDistance);
    }

    private static boolean checkTool(PlayerEntity player, Item tool) {
        if (player.isCreative()) return true;
        ItemStack heldItem = player.getMainHandStack();
        if (DiggusMaximusMod.getOptions().dontBreakTool && heldItem.getDamage() + 1 == tool.getMaxDamage())
            return false;
        if (heldItem.getItem() != tool)
            if (DiggusMaximusMod.getOptions().stopOnToolBreak || DiggusMaximusMod.getOptions().requiresTool)
                return false;
        return isTool(heldItem.getItem()) || !DiggusMaximusMod.getOptions().requiresTool;
    }

    private static boolean isTool(Item isTool) {
        return isTool.isDamageable() || DiggusMaximusMod.getOptions().tools.contains(Registry.ITEM.getId(isTool).toString());
    }
}