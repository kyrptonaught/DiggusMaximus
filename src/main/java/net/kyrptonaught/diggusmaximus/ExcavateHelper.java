package net.kyrptonaught.diggusmaximus;

import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class ExcavateHelper {
    static int maxMined = Math.min(DiggusMaximusMod.getOptions().maxMinedBlocks, 2048);
    private static double maxDistance = Math.min(DiggusMaximusMod.getOptions().maxMineDistance + 1, 128);

    public static void resetMaximums() {
        maxMined = Math.min(DiggusMaximusMod.getOptions().maxMinedBlocks, 2048);
        maxDistance = Math.min(DiggusMaximusMod.getOptions().maxMineDistance + 1, 128);
    }

    static void pickupDrops(World world, BlockPos pos, PlayerEntity player) {
        List<ItemEntity> drops = world.getEntitiesByClass(ItemEntity.class, new Box(pos), EntityPredicates.VALID_ENTITY);
        drops.forEach(item -> {
            ItemStack stack = item.getStack();
            player.getInventory().insertStack(stack);
            if (stack.getCount() <= 0)
                item.discard();
        });

    }

    static boolean isTheSameBlock(Identifier startID, Identifier newID, World world, int shapeSelection) {
        if (shapeSelection > -1 && DiggusMaximusMod.getExcavatingShapes().includeDifBlocks)
            return true;

        // Not sure if this makes sense anymore with the all the tags that exist now. For example this will now consider bamboo and all types of dirt as the same block since they are all in "bamboo_plantable_on"
        /* if (DiggusMaximusMod.getGrouping().tagGrouping) {
            Block newBlock = Registry.BLOCK.get(newID);
            if (Registry.BLOCK.get(startID).getRegistryEntry().streamTags().anyMatch(blockTagKey1 -> newBlock.getRegistryEntry().isIn(blockTagKey1)))
                newID = startID;

        } */
        if (DiggusMaximusMod.getGrouping().defaultTagGrouping || DiggusMaximusMod.getGrouping().customGrouping) {
            newID = DiggusMaximusMod.getIDFromConfigLookup(newID);
            startID = DiggusMaximusMod.getIDFromConfigLookup(startID);
        }
        return startID.equals(newID);
    }


    static boolean configAllowsMining(String blockID) {
        return DiggusMaximusMod.getBlackList().isWhitelist == DiggusMaximusMod.getBlackList().lookup.contains(blockID);
    }

    static boolean isValidPos(BlockPos pos) {
        return (Math.abs(pos.getX()) + Math.abs(pos.getY()) + Math.abs(pos.getZ())) != 0;
    }

    static Block getBlockAt(World world, BlockPos pos) {
        return (world.getBlockState(pos).getBlock());
    }

    static boolean canMine(PlayerEntity player, Item tool, World world, BlockPos startPos, BlockPos pos) {
        return isWithinDistance(startPos, pos) && checkTool(player, tool) && isBreakableBlock(getBlockAt(world, pos));
    }

    private static boolean isBreakableBlock(Block block) {
        return block.getHardness() >= 0;
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