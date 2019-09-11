package net.kyrptonaught.diggusmaximus.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.diggusmaximus.DiggusMaximusMod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigManager {
    private static final Jankson JANKSON = Jankson.builder().build();
    private final File configFile, ignoreFile;
    public ConfigOptions config;
    public Blacklist blacklist;

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

    }

    public void saveAll() {
        save(configFile, true);
        save(ignoreFile, false);
    }

    public void loadAll() {
        load(configFile, true);
        load(ignoreFile, false);
    }

    private void save(File saveFile, boolean isConfig) {
        try {
            if (!saveFile.exists() && !saveFile.createNewFile()) {
                System.out.println(DiggusMaximusMod.MOD_ID + " Failed to save config! Overwriting with default config.");
                resetToDefault(isConfig);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileOutputStream out = new FileOutputStream(saveFile, false)) {
            String result = JANKSON.toJson(isConfig ? config : blacklist).toJson(true, true, 0);
            if (!saveFile.exists())
                saveFile.createNewFile();
            out.write(result.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(DiggusMaximusMod.MOD_ID + " Failed to save config! Overwriting with default config.");
            resetToDefault(isConfig);
        }
    }

    private void load(File saveFile, boolean isConfig) {
        if (!saveFile.exists() || !saveFile.canRead()) {
            System.out.println(DiggusMaximusMod.MOD_ID + " Config not found! Creating one.");
            resetToDefault(isConfig);
            save(saveFile, isConfig);
            return;
        }
        boolean failed = false;
        try {
            JsonObject configJson = JANKSON.load(saveFile);
            String regularized = configJson.toJson(false, false, 0);
            if (isConfig) config = JANKSON.fromJson(regularized, ConfigOptions.class);
            else blacklist = JANKSON.fromJson(regularized, Blacklist.class);
        } catch (Exception e) {
            e.printStackTrace();
            failed = true;
        }
        if (failed || (isConfig && config == null) || (!isConfig && blacklist == null)) {
            System.out.println(DiggusMaximusMod.MOD_ID + " Failed to load config! Overwriting with default config.");
            resetToDefault(isConfig);
        }
        save(saveFile, isConfig);
    }

    private void resetToDefault(boolean isConfig) {
        if (isConfig) config = new ConfigOptions();
        else
            blacklist = new Blacklist();
    }
}
