package com.ultikits.main;

import com.ultikits.api.VersionWrapper;
import com.ultikits.inventoryapi.PageRegister;
import com.ultikits.utils.LanguageUtils;
import com.ultikits.utils.VersionAdaptor;
import org.bukkit.plugin.java.JavaPlugin;

public class UltiCoreAPI {

    String language = "en_us";
    String languageFileSavePath;
    private static PageRegister pageRegister;
    public static LanguageUtils languageUtils;
    public static VersionWrapper versionAdaptor = new VersionAdaptor().match();

    public UltiCoreAPI(JavaPlugin plugin, String languageFileSavePath){
        this.languageFileSavePath = languageFileSavePath;
        languageUtils = LanguageUtils.getInstance(languageFileSavePath, language);
        pageRegister = new PageRegister(plugin);
    }

    public UltiCoreAPI(JavaPlugin plugin, String languageFileSavePath, String language){
        this.languageFileSavePath = languageFileSavePath;
        this.language = language;
        languageUtils = LanguageUtils.getInstance(languageFileSavePath, language);
        pageRegister = new PageRegister(plugin);
    }

    public static PageRegister getPageRegister() {
        return pageRegister;
    }
}
