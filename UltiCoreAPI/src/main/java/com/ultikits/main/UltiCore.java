package com.ultikits.main;

import org.bukkit.plugin.java.JavaPlugin;

public class UltiCore extends JavaPlugin {

    private static UltiCore plugin;

    @Override
    public void onEnable() {
        plugin = this;
        getServer().getConsoleSender().sendMessage("UltiCoreAPI已加载!");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("UltiCoreAPI已卸载!");
    }

    public static UltiCore getInstance(){
        return plugin;
    }
}
