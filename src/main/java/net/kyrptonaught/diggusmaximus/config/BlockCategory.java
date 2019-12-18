package net.kyrptonaught.diggusmaximus.config;

import blue.endless.jankson.Comment;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockCategory implements AbstractConfigFile {

    @Comment("BlockID to be considered the same block when excavating")
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
