package com.ultikits.main;

import com.ultikits.api.VersionWrapper;
import com.ultikits.checker.UltiCoreAPIVersionChecker;
import com.ultikits.commands.CoreCommands;
import com.ultikits.inventoryapi.PageRegister;
import com.ultikits.utils.Metrics;
import com.ultikits.utils.VersionAdaptor;
import com.ultikits.utils.YamlFileUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class UltiCore extends JavaPlugin {

    private static UltiCore plugin;
    private static Economy econ;
    private static PageRegister pageRegister;
    private static boolean isVaultLoaded;
    public static YamlFileUtils yaml;
    public static YamlConfiguration languageUtils;
    public static String language;
    public static VersionWrapper versionAdaptor = new VersionAdaptor().match();

    @Override
    public void onEnable() {
        plugin = this;
        startBStates();
        setupVault();
        pageRegister = new PageRegister(plugin);
        yaml = new YamlFileUtils();
        setLocalLanguage();
        saveDefaultConfig();
        yaml.saveYamlFile(getDataFolder().getPath() + File.separator + "lang", language + ".yml", language + ".yml", true);
        File file = new File(getDataFolder().getPath() + File.separator + "lang", language + ".yml");
        languageUtils = YamlConfiguration.loadConfiguration(file);

        Objects.requireNonNull(this.getCommand("ulticore")).setExecutor(new CoreCommands());

        getServer().getConsoleSender().sendMessage("UltiCoreAPI已加载!");
        UltiCoreAPIVersionChecker.runTask();
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("UltiCoreAPI已卸载!");
    }

    public static UltiCore getInstance(){
        return plugin;
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
        isVaultLoaded = true;
        return econ != null;
    }

    public static PageRegister getPageRegister() {
        return pageRegister;
    }

    public static Economy getEcon(){
        return econ;
    }

    public static boolean getVaultInstalled(){
        return isVaultLoaded;
    }

    private void setLocalLanguage() {
        Locale defaultLocale = Locale.getDefault();
        List<String> langs = Arrays.asList("en", "zh");
        language = defaultLocale.getLanguage();
        if (!langs.contains(language)) {
            language = "en";
        }
    }

    public static String getWords(String key){
        return languageUtils.getString(key);
    }

    public void startBStates() {
        Metrics metrics = new Metrics(plugin, 9293);
    }
}
