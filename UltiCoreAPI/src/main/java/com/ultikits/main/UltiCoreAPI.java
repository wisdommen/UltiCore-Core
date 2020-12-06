package com.ultikits.main;

import com.ultikits.utils.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class UltiCoreAPI {
    private JavaPlugin plugin;
    String dataFolder;
    private String databaseName;
    private String databaseIP;
    private String databasePort;
    private String databaseUsername;
    private String databasePassword;
    private boolean isDatabaseEnabled = false;
    public static boolean isPapiLoaded;

    static {
        isPapiLoaded = UltiCore.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public UltiCoreAPI(JavaPlugin plugin){
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder().getAbsolutePath();
    }

    public void setUpDatabase(String databaseName, String ip, String port, String username, String password){
        this.isDatabaseEnabled = true;
        this.databaseName = databaseName;
        this.databaseIP = ip;
        this.databasePort = port;
        this.databaseUsername = username;
        this.databasePassword = password;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDatabaseIP() {
        return databaseIP;
    }

    public String getDatabasePort() {
        return databasePort;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public boolean isDatabaseEnabled() {
        return isDatabaseEnabled;
    }

    public JavaPlugin getPlugin(){
        return this.plugin;
    }

    public void startBStates(int code) {
        Metrics metrics = new Metrics(plugin, code);
    }

    public static boolean isPapiLoaded() {
        return isPapiLoaded;
    }
}
