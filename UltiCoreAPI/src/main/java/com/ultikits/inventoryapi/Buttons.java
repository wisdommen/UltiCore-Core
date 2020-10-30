package com.ultikits.inventoryapi;

import com.ultikits.enums.Colors;
import com.ultikits.main.UltiCoreAPI;
import org.bukkit.inventory.ItemStack;

/**
 * The enum Buttons.
 */
public enum Buttons {
    /**
     * Previous buttons.
     */
    PREVIOUS(UltiCoreAPI.languageUtils.getWords("button_previous"), UltiCoreAPI.versionAdaptor.getColoredPlaneGlass(Colors.RED)),
    /**
     * Next buttons.
     */
    NEXT(UltiCoreAPI.languageUtils.getWords("button_next"), UltiCoreAPI.versionAdaptor.getColoredPlaneGlass(Colors.RED)),
    /**
     * Back buttons.
     */
    BACK(UltiCoreAPI.languageUtils.getWords("button_back"), UltiCoreAPI.versionAdaptor.getSign()),
    /**
     * Quit buttons.
     */
    QUIT(UltiCoreAPI.languageUtils.getWords("button_quit"), UltiCoreAPI.versionAdaptor.getEndEye()),
    /**
     * Ok buttons.
     */
    OK(UltiCoreAPI.languageUtils.getWords("button_ok"), UltiCoreAPI.versionAdaptor.getColoredPlaneGlass(Colors.GREEN)),
    /**
     * Cancel buttons.
     */
    CANCEL(UltiCoreAPI.languageUtils.getWords("button_cancel"), UltiCoreAPI.versionAdaptor.getColoredPlaneGlass(Colors.RED));

    /**
     * The Name.
     */
    String name;
    /**
     * The Item stack.
     */
    ItemStack itemStack;

    Buttons(String name, ItemStack itemStack) {
        this.name = name;
        this.itemStack = itemStack;
    }

    /**
     * Get name string.
     *
     * @return Button 's name 按钮名称
     */
    public String getName(){
        return name;
    }

    /**
     * Get item stack item stack.
     *
     * @return Button 's material 按钮材质
     */
    public ItemStack getItemStack(){
        return itemStack;
    }
}
