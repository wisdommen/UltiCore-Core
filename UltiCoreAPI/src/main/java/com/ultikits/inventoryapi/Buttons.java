package com.ultikits.inventoryapi;

import com.ultikits.enums.Colors;
import com.ultikits.main.UltiCore;
import org.bukkit.inventory.ItemStack;

/**
 * The enum Buttons.
 */
public enum Buttons {
    /**
     * Previous buttons.
     */
    PREVIOUS(UltiCore.getWords("button_previous"), UltiCore.versionAdaptor.getColoredPlaneGlass(Colors.RED)),
    /**
     * Next buttons.
     */
    NEXT(UltiCore.getWords("button_next"), UltiCore.versionAdaptor.getColoredPlaneGlass(Colors.RED)),
    /**
     * Back buttons.
     */
    BACK(UltiCore.getWords("button_back"), UltiCore.versionAdaptor.getSign()),
    /**
     * Quit buttons.
     */
    QUIT(UltiCore.getWords("button_quit"), UltiCore.versionAdaptor.getEndEye()),
    /**
     * Ok buttons.
     */
    OK(UltiCore.getWords("button_ok"), UltiCore.versionAdaptor.getColoredPlaneGlass(Colors.GREEN)),
    /**
     * Cancel buttons.
     */
    CANCEL(UltiCore.getWords("button_cancel"), UltiCore.versionAdaptor.getColoredPlaneGlass(Colors.RED));

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
