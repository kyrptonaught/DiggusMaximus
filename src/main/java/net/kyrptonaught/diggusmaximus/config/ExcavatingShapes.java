package net.kyrptonaught.diggusmaximus.config;

import blue.endless.jankson.Comment;
import net.kyrptonaught.diggusmaximus.ExcavateTypes;
import net.kyrptonaught.kyrptconfig.config.AbstractConfigFile;

public class ExcavatingShapes implements AbstractConfigFile {
    @Comment("Should shape excavating be enabled")
    public boolean enableShapes = false;

    @Comment("Should shape excavating include different blocks")
    public boolean includeDifBlocks = false;

    @Comment("Shape activation key")
    public String shapeKey = "";

    @Comment("Shape type cycle key, sneak to reverse cycle")
    public String cycleKey = "";

    @Comment("Currently selected shape")
    public ExcavateTypes.shape selectedShape = ExcavateTypes.shape.LAYER;

}
