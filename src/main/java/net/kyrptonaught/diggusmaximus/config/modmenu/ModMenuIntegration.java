package net.kyrptonaught.diggusmaximus.config.modmenu;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.StringListListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;
import net.kyrptonaught.diggusmaximus.ExcavateHelper;
import net.kyrptonaught.diggusmaximus.config.Blacklist;
import net.kyrptonaught.diggusmaximus.config.BlockCategory;
import net.kyrptonaught.diggusmaximus.config.ConfigOptions;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public String getModId() {
        return DiggusMaximusMod.MOD_ID;
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return (screen) -> {
            ConfigOptions options = DiggusMaximusMod.getOptions();
            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(screen).setTitle("Diggus Maximus Config");
            builder.setSavingRunnable(() -> {
                DiggusMaximusMod.configManager.saveAll();
                DiggusMaximusMod.keycode = null;
                DiggusMaximusMod.configManager.getBlackList().generateHash();
                DiggusMaximusMod.configManager.getGrouping().generateLookup();
                ExcavateHelper.resetMaximums();
            });
            ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

            ConfigCategory category = builder.getOrCreateCategory("key.diggusmaximus.config.category");
            category.addEntry(entryBuilder.startBooleanToggle("key.diggusmaximus.config.enabled", options.enabled).setSaveConsumer(val -> options.enabled = val).setDefaultValue(true).build());

            category.addEntry(new KeyBindEntry("key.diggusmaximus.config.hotkey", options.keybinding, key -> options.keybinding = key));
            category.addEntry(entryBuilder.startBooleanToggle("key.diggusmaximus.config.minediag", options.mineDiag).setSaveConsumer(val -> options.mineDiag = val).setDefaultValue(true).build());
            category.addEntry(entryBuilder.startIntField("key.diggusmaximus.config.maxmine", options.maxMinedBlocks).setSaveConsumer(val -> options.maxMinedBlocks = val).setDefaultValue(40).setMin(1).setMax(2048).build());
            category.addEntry(entryBuilder.startIntField("key.diggusmaximus.config.maxdistance", options.maxMineDistance).setSaveConsumer(val -> options.maxMineDistance = val).setDefaultValue(10).setMin(1).setMax(128).build());

            category.addEntry(entryBuilder.startBooleanToggle("key.diggusmaximus.config.autopickup", options.autoPickup).setSaveConsumer(val -> options.autoPickup = val).setDefaultValue(true).build());
            category.addEntry(entryBuilder.startBooleanToggle("key.diggusmaximus.config.requirestool", options.requiresTool).setSaveConsumer(val -> options.requiresTool = val).setDefaultValue(false).build());
            category.addEntry(entryBuilder.startBooleanToggle("key.diggusmaximus.config.toolduribility", options.toolDuribility).setSaveConsumer(val -> options.toolDuribility = val).setDefaultValue(true).build());
            category.addEntry(entryBuilder.startBooleanToggle("key.diggusmaximus.config.playerexhaustion", options.playerExhaustion).setSaveConsumer(val -> options.playerExhaustion = val).setDefaultValue(true).build());

            Blacklist blacklist = DiggusMaximusMod.configManager.getBlackList();
            ConfigCategory blacklistCat = builder.getOrCreateCategory("key.diggusmaximus.config.blacklistcat");
            blacklistCat.addEntry(entryBuilder.startBooleanToggle("key.diggusmaximus.config.invertlist", blacklist.isWhitelist).setSaveConsumer(val -> blacklist.isWhitelist = val).setDefaultValue(false).build());
            blacklistCat.addEntry(entryBuilder.startStrList("key.diggusmaximus.config.blacklist", blacklist.blacklistedBlocks).setSaveConsumer(val -> blacklist.blacklistedBlocks = val).setDefaultValue(new ArrayList<>())
                    .setCreateNewInstance(baseListEntry -> new StringListListEntry.StringListCell("minecraft:", (StringListListEntry) baseListEntry)).build());

            BlockCategory grouping = DiggusMaximusMod.configManager.getGrouping();
            ConfigCategory groupcat = builder.getOrCreateCategory("key.diggusmaximus.config.groupcat");
            groupcat.addEntry(entryBuilder.startBooleanToggle("key.diggusmaximus.config.taggrouping", grouping.tagGrouping).setSaveConsumer(val -> grouping.tagGrouping = val).setDefaultValue(false).build());
            groupcat.addEntry(entryBuilder.startBooleanToggle("key.diggusmaximus.config.customgrouping", grouping.customGrouping).setSaveConsumer(val -> grouping.customGrouping = val).setDefaultValue(false).build());
            groupcat.addEntry(entryBuilder.startStrList("key.diggusmaximus.config.grouplist", grouping.groups).setSaveConsumer(val -> grouping.groups = val).setDefaultValue(new ArrayList<>())
                    .setCreateNewInstance(baseListEntry -> new StringListListEntry.StringListCell("minecraft:", (StringListListEntry) baseListEntry)).build());
            return builder.build();
        };
    }
}
