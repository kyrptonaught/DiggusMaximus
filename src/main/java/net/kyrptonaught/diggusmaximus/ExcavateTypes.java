package net.kyrptonaught.diggusmaximus;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExcavateTypes {
    public static Direction facing;

    private List<BlockPos> layers() {
        List<BlockPos> cube = new ArrayList<>();
        if (facing.getAxis().isHorizontal()) {
            cube.add(new BlockPos(0, 1, 0));
            cube.add(new BlockPos(0, -1, 0));
            if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                cube.add(new BlockPos(1, 0, 0));
                cube.add(new BlockPos(-1, 0, 0));
            } else {
                cube.add(new BlockPos(0, 0, 1));
                cube.add(new BlockPos(0, 0, -1));
            }
        } else {
            cube.add(new BlockPos(1, 0, 0));
            cube.add(new BlockPos(-1, 0, 0));
            cube.add(new BlockPos(0, 0, 1));
            cube.add(new BlockPos(0, 0, -1));
        }
        return cube;
    }

    public static List<BlockPos> threebythree(BlockPos startPos, BlockPos curPos) {
        List<BlockPos> cube = new ArrayList<>();
        if (startPos.equals(curPos)) {
            if (facing.getAxis().isHorizontal()) {
                cube.add(new BlockPos(0, 1, 0));
                cube.add(new BlockPos(0, -1, 0));
                if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                    cube.add(new BlockPos(1, 0, 0));
                    cube.add(new BlockPos(-1, 0, 0));
                    cube.add(new BlockPos(1, 1, 0));
                    cube.add(new BlockPos(-1, 1, 0));
                    cube.add(new BlockPos(1, -1, 0));
                    cube.add(new BlockPos(-1, -1, 0));
                } else {
                    cube.add(new BlockPos(0, 0, 1));
                    cube.add(new BlockPos(0, 0, -1));
                    cube.add(new BlockPos(0, 1, 1));
                    cube.add(new BlockPos(0, 1, -1));
                    cube.add(new BlockPos(0, -1, 1));
                    cube.add(new BlockPos(0, -1, -1));
                }
            } else {
                cube.add(new BlockPos(1, 0, 0));
                cube.add(new BlockPos(-1, 0, 0));
                cube.add(new BlockPos(1, 0, 1));
                cube.add(new BlockPos(-1, 0, 1));
                cube.add(new BlockPos(0, 0, 1));
                cube.add(new BlockPos(0, 0, -1));
                cube.add(new BlockPos(1, 0, -1));
                cube.add(new BlockPos(-1, 0, -1));
            }
        }
        return cube;
    }

    public static List<BlockPos> threebythreeTunnel(BlockPos startPos, BlockPos curPos) {
        List<BlockPos> cube = threebythree(startPos, curPos);
        cube.add(BlockPos.ORIGIN.offset(facing.getOpposite()));
        cube.addAll(cube.stream().map(blockPos -> blockPos.offset(facing.getOpposite())).collect(Collectors.toList()));
        return cube;
    }

    public static List<BlockPos> onebytwo(BlockPos startPos, BlockPos curPos) {
        List<BlockPos> cube = new ArrayList<>();
        if (startPos.getY() == curPos.getY())
            cube.add(new BlockPos(0, -1, 0));
        return cube;
    }

    public static List<BlockPos> onebytwoTunnel(BlockPos startPos, BlockPos curPos) {
        List<BlockPos> cube = new ArrayList<>();
        if (startPos.getY() == curPos.getY()) {
            cube.add(new BlockPos(0, -1, 0));
            cube.add(new BlockPos(0, -1, 0).offset(facing.getOpposite()));
        }
        cube.add(BlockPos.ORIGIN.offset(facing.getOpposite()));
        return cube;
    }

    public final static List<BlockPos> standard = new ArrayList<>();

    static {
        standard.add(new BlockPos(0, 1, 0));
        standard.add(new BlockPos(0, 0, 1));
        standard.add(new BlockPos(0, -1, 0));
        standard.add(new BlockPos(1, 0, 0));
        standard.add(new BlockPos(0, 0, -1));
        standard.add(new BlockPos(-1, 0, 0));
    }

    public final static List<BlockPos> standardDiag = new ArrayList<>();

    static {
        standardDiag.addAll(BlockPos.stream(-1, -1, -1, 1, 1, 1).map(BlockPos::toImmutable).collect(Collectors.toList()));
    }
}