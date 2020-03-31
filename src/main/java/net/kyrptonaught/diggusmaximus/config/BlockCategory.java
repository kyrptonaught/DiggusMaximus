package net.kyrptonaught.diggusmaximus.config;

import blue.endless.jankson.Comment;
import net.kyrptonaught.kyrptconfig.config.AbstractConfigFile;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockCategory implements AbstractConfigFile {
    @Comment("Consider blocks in the same tag as the same block")
    public boolean tagGrouping = false;

    @Comment("Enable block custom grouping")
    public boolean customGrouping = false;
    @Comment("BlockID to be considered the same block when excavating (IDs separated by commas)")
    public List<String> groups = new ArrayList<>();

    public transient HashMap<Identifier, Identifier> lookup = new HashMap<>();

    public void generateLookup() {
        lookup.clear();
        groups.forEach(group -> {
            String[] items = group.split(",");
            for (int i = 1; i < items.length; i++) {
                lookup.put(Identifier.tryParse(items[i]), Identifier.tryParse(items[0]));
            }
        });
    }
}
