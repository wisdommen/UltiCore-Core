package com.ultikits.abstracts;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;

public abstract class AbstractConfigReviewable extends AbstractConfig {

    public AbstractConfigReviewable(){
    }

    public AbstractConfigReviewable(String name, String filePath) {
        super(name, filePath);
    }

    @Override
    public void init() {
        if (file.exists()) {
            load();
            review();
            return;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        doInit(config);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        load();
    }

    public void review(){
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.getDouble("config_version") < (double) map.get("config_version")) {
            doInit(config);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
