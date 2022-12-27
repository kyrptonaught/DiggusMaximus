package net.kyrptonaught.diggusmaximus.config;

import blue.endless.jankson.Comment;
import net.kyrptonaught.kyrptconfig.TagHelper;
import net.kyrptonaught.kyrptconfig.config.AbstractConfigFile;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class BlockCategory implements AbstractConfigFile {

    @Comment("Enable default block groupings for certain tags (e.g. Stone & Deepslate Ores)")
    public boolean defaultTagGrouping = true;

    private final List<String> defaultTagGroups = Arrays.asList(
            "#coal_ores", "#iron_ores", "#lapis_ores", "#redstone_ores",
            "#copper_ores", "#gold_ores", "#emerald_ores", "#diamond_ores");

    @Comment("Enable block custom grouping")
    public boolean customGrouping = false;

    @Comment("BlockID to be considered the same block when excavating (IDs separated by commas)")
    public List<String> groups = new ArrayList<>();

    public transient HashMap<Identifier, Identifier> lookup = new HashMap<>();

    public void generateLookup() {
        lookup.clear();
        if (defaultTagGrouping) {
            appendGroupsToLookup(defaultTagGroups);
        }

        if (customGrouping) {
            appendGroupsToLookup(groups);
        }
    }

    private void appendGroupsToLookup(List<String> groups) {
        groups.forEach(group -> {
            List<Identifier> expanded = new ArrayList<>();
            for (String item : group.split(",")) {
                if (item.startsWith("#"))
                    expanded.addAll(TagHelper.getBlockIDsInTag(new Identifier(item.replaceAll("#", ""))));
                else expanded.add(Identifier.tryParse(item));
            }

            for (int i = 1; i < expanded.size(); i++) {
                lookup.put(expanded.get(i), expanded.get(0));
            }
        });
    }
}
