package net.kyrptonaught.diggusmaximus.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Supplier;

public class ConfigManager {
    private static final Jankson JANKSON = Jankson.builder().build();
    private final File configFile, ignoreFile, groupFile;
    private AbstractConfigFile config, blacklist, grouping;

    public ConfigManager() {
        File dir = new File(FabricLoader.getInstance().getConfigDirectory() + "/" + DiggusMaximusMod.MOD_ID);
        if (!Files.exists(dir.toPath())) {
            try {
                Files.createDirectories(dir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.configFile = new File(dir, "config.json5");
        this.ignoreFile = new File(dir, "blacklist.json5");
        this.groupFile = new File(dir, "grouping.json5.bak");

    }

    public void saveAll() {
        save(configFile, config);
        save(ignoreFile, blacklist);
        save(groupFile, grouping);
    }

    public void loadAll() {
        config = load(configFile, config, ConfigOptions.class, ConfigOptions::new);
        blacklist = load(ignoreFile, blacklist, Blacklist.class, Blacklist::new);
        grouping = load(groupFile, grouping, BlockCategory.class, BlockCategory::new);
        saveAll();
    }

    public Blacklist getBlackList() {
        return (Blacklist) blacklist;
    }

    public ConfigOptions getOptions() {
        return (ConfigOptions) config;
    }

    public BlockCategory getGrouping() {
        return (BlockCategory) grouping;
    }

    private void save(File saveFile, AbstractConfigFile configFile) {
        try {
            if (!saveFile.exists() && !saveFile.createNewFile()) {
                System.out.println(DiggusMaximusMod.MOD_ID + " Failed to save " + saveFile.getName() + " ! Overwriting with default config.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileOutputStream out = new FileOutputStream(saveFile, false)) {
            if (!saveFile.exists())
                saveFile.createNewFile();
            String json = JANKSON.toJson(configFile).toJson(true, true, 0);
            out.write(json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(DiggusMaximusMod.MOD_ID + " Failed to save " + saveFile.getName() + " ! Overwriting with default config.");
        }
    }

    private AbstractConfigFile load(File saveFile, AbstractConfigFile configFile, Class configClass, Supplier<AbstractConfigFile> reset) {
        if (!saveFile.exists() || !saveFile.canRead()) {
            System.out.println(DiggusMaximusMod.MOD_ID + " Config not found! Creating one.");
            configFile = reset.get();
        }
        boolean failed = false;
        try {
            JsonObject configJson = JANKSON.load(saveFile);
            String regularized = configJson.toJson(false, false, 0);
            configFile = (AbstractConfigFile) JANKSON.fromJson(regularized, configClass);
        } catch (Exception e) {
            e.printStackTrace();
            failed = true;
        }
        if (failed || (configFile == null)) {
            System.out.println(DiggusMaximusMod.MOD_ID + " Failed to load config! Overwriting with default config.");
            configFile = reset.get();
        }
        return configFile;
    }
}
