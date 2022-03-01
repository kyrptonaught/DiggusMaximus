package net.kyrptonaught.diggusmaximus.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;
import net.kyrptonaught.diggusmaximus.ExcavateHelper;
import net.kyrptonaught.diggusmaximus.ExcavateTypes;
import net.kyrptonaught.diggusmaximus.config.Blacklist;
import net.kyrptonaught.diggusmaximus.config.BlockCategory;
import net.kyrptonaught.diggusmaximus.config.ConfigOptions;
import net.kyrptonaught.diggusmaximus.config.ExcavatingShapes;
import net.kyrptonaught.kyrptconfig.config.screen.ConfigScreen;
import net.kyrptonaught.kyrptconfig.config.screen.ConfigSection;
import net.kyrptonaught.kyrptconfig.config.screen.items.BooleanItem;
import net.kyrptonaught.kyrptconfig.config.screen.items.EnumItem;
import net.kyrptonaught.kyrptconfig.config.screen.items.KeybindItem;
import net.kyrptonaught.kyrptconfig.config.screen.items.lists.BlockIconList;
import net.kyrptonaught.kyrptconfig.config.screen.items.lists.ItemIconList;
import net.kyrptonaught.kyrptconfig.config.screen.items.number.FloatItem;
import net.kyrptonaught.kyrptconfig.config.screen.items.number.IntegerItem;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.HashSet;


@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (screen) -> {
            ConfigOptions options = DiggusMaximusMod.getOptions();
            ExcavatingShapes shapes = DiggusMaximusMod.getExcavatingShapes();
            ConfigScreen configScreen = new ConfigScreen(screen, new TranslatableText("Diggus Maximus Config"));
            configScreen.setSavingEvent(() -> {
                DiggusMaximusMod.configManager.save();
                DiggusMaximusMod.getGrouping().generateLookup();
                DiggusMaximusMod.getBlackList().generateLookup();
                ExcavateHelper.resetMaximums();
            });

            ConfigSection mainSection = new ConfigSection(configScreen, new TranslatableText("key.diggusmaximus.config.category"));

            mainSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.enabled"), options.enabled, true).setSaveConsumer(val -> options.enabled = val));
            mainSection.addConfigItem(new KeybindItem(new TranslatableText("key.diggusmaximus.config.hotkey"), options.keybinding.rawKey, "key.keyboard.grave.accent").setSaveConsumer(val -> options.keybinding.setRaw(val)));
            mainSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.invertactivation"), options.invertActivation, false).setSaveConsumer(val -> options.invertActivation = val));
            mainSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.sneaktoexcavate"), options.sneakToExcavate, false).setSaveConsumer(val -> options.sneakToExcavate = val));

            mainSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.minediag"), options.mineDiag, true).setSaveConsumer(val -> options.mineDiag = val));
            mainSection.addConfigItem(new IntegerItem(new TranslatableText("key.diggusmaximus.config.maxmine"), options.maxMinedBlocks, 40).setMinMax(1, 2048).setSaveConsumer(val -> options.maxMinedBlocks = (int) val));
            mainSection.addConfigItem(new IntegerItem(new TranslatableText("key.diggusmaximus.config.maxdistance"), options.maxMineDistance, 10).setMinMax(1, 128).setSaveConsumer(val -> options.maxMineDistance = (int) val));

            mainSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.minediag"), options.mineDiag, true).setSaveConsumer(val -> options.mineDiag = val));


            mainSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.autopickup"), options.autoPickup, true).setSaveConsumer(val -> options.autoPickup = val));
            mainSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.requirestool"), options.requiresTool, false).setSaveConsumer(val -> options.requiresTool = val));
            mainSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.toolduribility"), options.toolDurability, true).setSaveConsumer(val -> options.toolDurability = val));
            mainSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.stopontoolbreak"), options.stopOnToolBreak, true).setSaveConsumer(val -> options.stopOnToolBreak = val));
            mainSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.dontbreaktool"), options.dontBreakTool, true).setSaveConsumer(val -> options.dontBreakTool = val));
            mainSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.playerexhaustion"), options.playerExhaustion, true).setSaveConsumer(val -> options.playerExhaustion = val));
            mainSection.addConfigItem(new FloatItem(new TranslatableText("key.diggusmaximus.config.exhaustionmultiplier"), options.exhaustionMultiplier, 1.0f).setSaveConsumer(val -> options.exhaustionMultiplier = val));

            mainSection.addConfigItem(new ItemIconList(new TranslatableText("key.diggusmaximus.config.toollist"), new ArrayList<>(options.tools), new ArrayList<>(), false).setSaveConsumer(val -> options.tools = new HashSet<>(val)));

            Blacklist blacklist = DiggusMaximusMod.getBlackList();
            ConfigSection blacklistSection = new ConfigSection(configScreen, new TranslatableText("key.diggusmaximus.config.blacklistcat"));
            blacklistSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.invertlist"), blacklist.isWhitelist, false).setSaveConsumer(val -> blacklist.isWhitelist = val));
            blacklistSection.addConfigItem(new BlockIconList(new TranslatableText("key.diggusmaximus.config.blacklist"), new ArrayList<>(blacklist.blacklistedBlocks), new ArrayList<>(), true).setSaveConsumer(val -> blacklist.blacklistedBlocks = new HashSet<>(val)));

            BlockCategory grouping = DiggusMaximusMod.getGrouping();
            ConfigSection groupSection = new ConfigSection(configScreen, new TranslatableText("key.diggusmaximus.config.groupcat"));
            groupSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.customgrouping"), grouping.customGrouping, false).setSaveConsumer(val -> grouping.customGrouping = val));
            groupSection.addConfigItem(new GroupingList(new TranslatableText("key.diggusmaximus.config.grouplist"), new ArrayList<>(grouping.groups), new ArrayList<>()).setSaveConsumer(val -> grouping.groups = val));

            ConfigSection shapeSection = new ConfigSection(configScreen, new TranslatableText("key.diggusmaximus.config.shapecat"));
            shapeSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.enableshapes"), shapes.enableShapes, false).setSaveConsumer(val -> shapes.enableShapes = val));
            shapeSection.addConfigItem(new BooleanItem(new TranslatableText("key.diggusmaximus.config.includedifblock"), shapes.includeDifBlocks, false).setSaveConsumer(val -> shapes.includeDifBlocks = val));
            shapeSection.addConfigItem(new KeybindItem(new TranslatableText("key.diggusmaximus.config.shapekey"), shapes.shapeKey.rawKey, "key.keyboard.unknown").setSaveConsumer(val -> shapes.shapeKey.setRaw(val)));
            shapeSection.addConfigItem(new KeybindItem(new TranslatableText("key.diggusmaximus.config.cyclekey"), shapes.cycleKey.rawKey, "key.keyboard.unknown").setSaveConsumer(val -> shapes.cycleKey.setRaw(val)));
            shapeSection.addConfigItem(new EnumItem<>(new TranslatableText("key.diggusmaximus.config.selectedshape"), ExcavateTypes.shape.values(), shapes.selectedShape, ExcavateTypes.shape.LAYER).setSaveConsumer(val -> shapes.selectedShape = val));

            return configScreen;
        };
    }
}