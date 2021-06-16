package net.kyrptonaught.diggusmaximus.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.StringListListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.diggusmaximus.DiggusMaximusClientMod;
import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;
import net.kyrptonaught.diggusmaximus.ExcavateHelper;
import net.kyrptonaught.diggusmaximus.config.Blacklist;
import net.kyrptonaught.diggusmaximus.config.BlockCategory;
import net.kyrptonaught.diggusmaximus.config.ConfigOptions;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.HashSet;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    /*
    @Override
    public String getModId() {
        return DiggusMaximusMod.MOD_ID;
    }
     */
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (screen) -> {
            ConfigOptions options = DiggusMaximusMod.getOptions();
            //ExcavatingShapes shapes = DiggusMaximusMod.getExcavatingShapes();
            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(screen).setTitle(new TranslatableText("Diggus Maximus Config"));
            builder.setSavingRunnable(() -> {
                DiggusMaximusMod.configManager.save();
                DiggusMaximusClientMod.activationKey.setRaw(options.keybinding);
                //DiggusMaximusClientMod.shapeKey.setRaw(shapes.shapeKey);
                //DiggusMaximusClientMod.cycleShapeKey.setRaw(shapes.cycleKey);
                DiggusMaximusMod.getGrouping().generateLookup();
                ExcavateHelper.resetMaximums();
            });
            ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

            ConfigCategory category = builder.getOrCreateCategory(new TranslatableText("key.diggusmaximus.config.category"));
            category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.enabled"), options.enabled).setSaveConsumer(val -> options.enabled = val).setDefaultValue(true).build());
            category.addEntry(entryBuilder.startKeyCodeField(new TranslatableText("key.diggusmaximus.config.hotkey"), DiggusMaximusClientMod.activationKey.getKeybinding().orElse(InputUtil.UNKNOWN_KEY)).setSaveConsumer(key -> options.keybinding = key.getTranslationKey()).build());
            category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.invertactivation"), options.invertActivation).setSaveConsumer(val -> options.invertActivation = val).setDefaultValue(false).build());
            category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.sneaktoexcavate"), options.sneakToExcavate).setSaveConsumer(val -> options.sneakToExcavate = val).setDefaultValue(false).build());

            category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.minediag"), options.mineDiag).setSaveConsumer(val -> options.mineDiag = val).setDefaultValue(true).build());
            category.addEntry(entryBuilder.startIntField(new TranslatableText("key.diggusmaximus.config.maxmine"), options.maxMinedBlocks).setSaveConsumer(val -> options.maxMinedBlocks = val).setDefaultValue(40).setMin(1).setMax(2048).build());
            category.addEntry(entryBuilder.startIntField(new TranslatableText("key.diggusmaximus.config.maxdistance"), options.maxMineDistance).setSaveConsumer(val -> options.maxMineDistance = val).setDefaultValue(10).setMin(1).setMax(128).build());

            category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.autopickup"), options.autoPickup).setSaveConsumer(val -> options.autoPickup = val).setDefaultValue(true).build());
            category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.requirestool"), options.requiresTool).setSaveConsumer(val -> options.requiresTool = val).setDefaultValue(false).build());
            category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.toolduribility"), options.toolDurability).setSaveConsumer(val -> options.toolDurability = val).setDefaultValue(true).build());
            category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.stopontoolbreak"), options.stopOnToolBreak).setSaveConsumer(val -> options.stopOnToolBreak = val).setDefaultValue(true).build());
            category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.dontbreaktool"), options.dontBreakTool).setSaveConsumer(val -> options.dontBreakTool = val).setDefaultValue(true).build());
            category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.playerexhaustion"), options.playerExhaustion).setSaveConsumer(val -> options.playerExhaustion = val).setDefaultValue(true).build());
            category.addEntry(entryBuilder.startFloatField(new TranslatableText("key.diggusmaximus.config.exhaustionmultiplier"), options.exhaustionMultiplier).setSaveConsumer(val -> options.exhaustionMultiplier = val).setDefaultValue(1.0f).build());

            category.addEntry(entryBuilder.startStrList(new TranslatableText("key.diggusmaximus.config.toollist"), new ArrayList<>(options.tools)).setSaveConsumer(val -> options.tools = new HashSet<>(val)).setDefaultValue(new ArrayList<>())
                    .setCreateNewInstance(baseListEntry -> new StringListListEntry.StringListCell("minecraft:)", baseListEntry)).build());

            Blacklist blacklist = DiggusMaximusMod.getBlackList();
            ConfigCategory blacklistCat = builder.getOrCreateCategory(new TranslatableText("key.diggusmaximus.config.blacklistcat"));
            blacklistCat.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.invertlist"), blacklist.isWhitelist).setSaveConsumer(val -> blacklist.isWhitelist = val).setDefaultValue(false).build());

            blacklistCat.addEntry(entryBuilder.startStrList(new TranslatableText("key.diggusmaximus.config.blacklist"), new ArrayList<>(blacklist.blacklistedBlocks)).setSaveConsumer(val -> blacklist.blacklistedBlocks = new HashSet<>(val)).setDefaultValue(new ArrayList<>())
                    .setCreateNewInstance(baseListEntry -> new StringListListEntry.StringListCell("minecraft:", baseListEntry)).build());

            BlockCategory grouping = DiggusMaximusMod.getGrouping();
            ConfigCategory groupcat = builder.getOrCreateCategory(new TranslatableText("key.diggusmaximus.config.groupcat"));
            groupcat.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.taggrouping"), grouping.tagGrouping).setSaveConsumer(val -> grouping.tagGrouping = val).setDefaultValue(false).build());
            groupcat.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.customgrouping"), grouping.customGrouping).setSaveConsumer(val -> grouping.customGrouping = val).setDefaultValue(false).build());
            groupcat.addEntry(entryBuilder.startStrList(new TranslatableText("key.diggusmaximus.config.grouplist"), grouping.groups).setSaveConsumer(val -> grouping.groups = val).setDefaultValue(new ArrayList<>())
                    .setCreateNewInstance(baseListEntry -> new StringListListEntry.StringListCell("minecraft:", baseListEntry)).build());

            /*
            ConfigCategory shapeCat = builder.getOrCreateCategory(new TranslatableText("key.diggusmaximus.config.shapecat"));
            shapeCat.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.ignoreshapes"), shapes.enableShapes).setSaveConsumer(val -> shapes.enableShapes = val).setDefaultValue(true).build());
            shapeCat.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("key.diggusmaximus.config.ignoreblock"), shapes.ignoreBlock).setSaveConsumer(val -> shapes.ignoreBlock = val).setDefaultValue(true).build());
            shapeCat.addEntry(entryBuilder.startKeyCodeField(new TranslatableText("key.diggusmaximus.config.shapekey"), DiggusMaximusClientMod.shapeKey.getKeybinding().orElse(InputUtil.UNKNOWN_KEY)).setSaveConsumer(key -> shapes.shapeKey = key.getTranslationKey()).setDefaultValue(InputUtil.UNKNOWN_KEY).build());
            shapeCat.addEntry(entryBuilder.startKeyCodeField(new TranslatableText("key.diggusmaximus.config.cyclekey"), DiggusMaximusClientMod.cycleShapeKey.getKeybinding().orElse(InputUtil.UNKNOWN_KEY)).setSaveConsumer(key -> shapes.cycleKey = key.getTranslationKey()).setDefaultValue(InputUtil.UNKNOWN_KEY).build());
            shapeCat.addEntry(entryBuilder.startEnumSelector(new TranslatableText("key.diggusmaximus.config.selectedshape"), ExcavateTypes.shape.class, shapes.selectedShape).setEnumNameProvider(anEnum -> new TranslatableText("diggusmaximus.shape." + anEnum.name())).setSaveConsumer(val -> shapes.selectedShape = val).setDefaultValue(ExcavateTypes.shape.Layer).build());
              */
            return builder.build();
        };
    }
}
