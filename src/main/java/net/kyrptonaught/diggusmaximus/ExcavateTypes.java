package net.kyrptonaught.diggusmaximus;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExcavateTypes {
    public enum shape {
        HorizontalLayer("horizontal layer"), Layer("layer"), Hole("hole"), onebytwo("1x2"), onebytwoTunnel("1x2 tunnel"),
        threebythree("3x3"), threebythreetunnel("3x3 tunnel");
        String name;

        shape(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }
    }

    public static List<BlockPos> getSpreadType(int shapeSelection, Direction facing, BlockPos startPos, BlockPos curPos) {
        if (shapeSelection == -1)
            return DiggusMaximusMod.getOptions().mineDiag ? ExcavateTypes.standardDiag : ExcavateTypes.standard;

        switch (ExcavateTypes.shape.values()[shapeSelection]) {
            case Hole:
                return ExcavateTypes.hole(facing);
            case HorizontalLayer:
                return ExcavateTypes.horizontalLayer();
            case Layer:
                return ExcavateTypes.layers(facing);
            case onebytwo:
                return ExcavateTypes.onebytwo(startPos, curPos);
            case onebytwoTunnel:
                return ExcavateTypes.onebytwoTunnel(startPos, curPos, facing);
            case threebythree:
                return ExcavateTypes.threebythree(startPos, curPos, facing);
            case threebythreetunnel:
                return ExcavateTypes.threebythreeTunnel(startPos, curPos, facing);
        }
        return ExcavateTypes.standard;
    }

    public static List<BlockPos> horizontalLayer() {
        List<BlockPos> cube = new ArrayList<>();
        cube.add(new BlockPos(1, 0, 0));
        cube.add(new BlockPos(0, 0, 1));
        cube.add(new BlockPos(-1, 0, 0));
        cube.add(new BlockPos(0, 0, -1));
        return cube;
    }

    public static List<BlockPos> layers(Direction facing) {
        if (facing.getAxis() == Direction.Axis.Y)
            return horizontalLayer();

        List<BlockPos> cube = new ArrayList<>();
        cube.add(new BlockPos(0, 1, 0));
        cube.add(new BlockPos(0, -1, 0));
        if (facing.getAxis() == Direction.Axis.Z) {
            cube.add(new BlockPos(1, 0, 0));
            cube.add(new BlockPos(-1, 0, 0));
        } else {
            cube.add(new BlockPos(0, 0, 1));
            cube.add(new BlockPos(0, 0, -1));
        }
        return cube;
    }

    public static List<BlockPos> hole(Direction facing) {
        List<BlockPos> cube = new ArrayList<>();
        cube.add(BlockPos.ORIGIN.offset(facing.getOpposite()));
        return cube;
    }

    public static List<BlockPos> threebythree(BlockPos startPos, BlockPos curPos, Direction facing) {
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

    public static List<BlockPos> threebythreeTunnel(BlockPos startPos, BlockPos curPos, Direction facing) {
        List<BlockPos> cube = threebythree(startPos, curPos, facing);
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

    public static List<BlockPos> onebytwoTunnel(BlockPos startPos, BlockPos curPos, Direction facing) {
        List<BlockPos> cube = hole(facing);
        if (startPos.getY() == curPos.getY()) {
            cube.add(new BlockPos(0, -1, 0));
        }
        return cube;
    }

    public final static List<BlockPos> standard = new ArrayList<>();
    public final static List<BlockPos> standardDiag = new ArrayList<>();

    static {
        standard.add(new BlockPos(0, 1, 0));
        standard.add(new BlockPos(0, 0, 1));
        standard.add(new BlockPos(0, -1, 0));
        standard.add(new BlockPos(1, 0, 0));
        standard.add(new BlockPos(0, 0, -1));
        standard.add(new BlockPos(-1, 0, 0));

        standardDiag.addAll(BlockPos.stream(-1, -1, -1, 1, 1, 1).map(BlockPos::toImmutable).collect(Collectors.toList()));
    }
}