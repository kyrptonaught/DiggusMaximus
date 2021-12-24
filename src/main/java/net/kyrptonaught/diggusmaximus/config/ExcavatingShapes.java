package net.kyrptonaught.diggusmaximus.config;

import blue.endless.jankson.Comment;
import net.kyrptonaught.diggusmaximus.ExcavateTypes;
import net.kyrptonaught.diggusmaximus.client.DiggusKeyBinding;
import net.kyrptonaught.kyrptconfig.config.ConfigWDefaults;
import net.kyrptonaught.kyrptconfig.keybinding.CustomKeyBinding;

public class ExcavatingShapes extends ConfigWDefaults {
    @Comment("Should shape excavating be enabled")
    public boolean enableShapes = false;

    @Comment("Should shape excavating include different blocks")
    public boolean includeDifBlocks = false;

    @Comment("Shape activation key")
    public CustomKeyBinding shapeKey = new DiggusKeyBinding(false, false, "key.keyboard.unknown");

    @Comment("Shape type cycle key, sneak to reverse cycle")
    public CustomKeyBinding cycleKey = new DiggusKeyBinding(false, false, "key.keyboard.unknown");

    @Comment("Currently selected shape")
    public ExcavateTypes.shape selectedShape = ExcavateTypes.shape.LAYER;

    @Override
    public ExcavatingShapes getDefaults() {
        return (ExcavatingShapes) super.getDefaults();
    }

    @Override
    public void afterLoad() {
        shapeKey.copyFromDefault(getDefaults().shapeKey);
        cycleKey.copyFromDefault(getDefaults().cycleKey);
    }
}
