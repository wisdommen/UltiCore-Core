package com.ultikits.main;

import com.ultikits.api.VersionWrapper;
import com.ultikits.inventoryapi.PageRegister;
import com.ultikits.utils.LanguageUtils;
import com.ultikits.utils.VersionAdaptor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;

public class UltiCoreAPI {
    private static JavaPlugin plugin;
    String language = "en_us";
    String dataFolder;
    private static PageRegister pageRegister;
    private static Economy econ;
    private static String databaseName;
    private static String databaseIP;
    private static String databasePort;
    private static String databaseUsername;
    private static String databasePassword;
    private static boolean isDatabaseEnabled = false;
    private static boolean isVaultInstalled;
    public static LanguageUtils languageUtils;
    public static VersionWrapper versionAdaptor = new VersionAdaptor().match();

    {
        setupVault();
    }

    public UltiCoreAPI(JavaPlugin plugin){
        UltiCoreAPI.plugin = plugin;
        pageRegister = new PageRegister(plugin);
        dataFolder = plugin.getDataFolder().getPath();
    }

    public UltiCoreAPI(JavaPlugin plugin, String dataFolder){
        UltiCoreAPI.plugin = plugin;
        this.dataFolder = dataFolder;
        languageUtils = new LanguageUtils(dataFolder, language);
        pageRegister = new PageRegister(plugin);
    }

    public UltiCoreAPI(JavaPlugin plugin, String dataFolder, String language){
        UltiCoreAPI.plugin = plugin;
        this.dataFolder = dataFolder;
        this.language = language;
        languageUtils = new LanguageUtils(dataFolder, language);
        pageRegister = new PageRegister(plugin);
    }

    public static PageRegister getPageRegister() {
        return pageRegister;
    }

    public void setUpDatabase(String databaseName, String ip, String port, String username, String password){
        UltiCoreAPI.isDatabaseEnabled = true;
        UltiCoreAPI.databaseName = databaseName;
        UltiCoreAPI.databaseIP = ip;
        UltiCoreAPI.databasePort = port;
        UltiCoreAPI.databaseUsername = username;
        UltiCoreAPI.databasePassword = password;
    }

    public static String getDatabaseName() {
        return databaseName;
    }

    public static String getDatabaseIP() {
        return databaseIP;
    }

    public static String getDatabasePort() {
        return databasePort;
    }

    public static String getDatabaseUsername() {
        return databaseUsername;
    }

    public static String getDatabasePassword() {
        return databasePassword;
    }

    public static boolean isDatabaseEnabled() {
        return isDatabaseEnabled;
    }

    public static JavaPlugin getPlugin(){
        return plugin;
    }

    public static Boolean getIsVaultInstalled() {
        return isVaultInstalled;
    }

    private boolean setupVault() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEcon() {
        return econ;
    }

    public static void startBStates(int code) {
        Metrics metrics = new Metrics(UltiCoreAPI.getPlugin(), code);
    }
}
