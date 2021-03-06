package com.ultikits.utils;

import com.ultikits.main.UltiCore;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MessagesUtils {

    private MessagesUtils(){}

    @Contract(pure = true)
    public static @NotNull String info(String message) {
        return ChatColor.YELLOW + message;
    }

    @Contract(pure = true)
    public static @NotNull String warning(String message) {
        return ChatColor.RED + message;
    }

    @Contract(pure = true)
    public static @NotNull String unimportant(String message) {
        return ChatColor.GRAY + message;
    }


    public static String player_inventory_full = warning(UltiCore.getWords("player_inventory_full"));
    public static String not_enough_money = warning(UltiCore.getWords("not_enough_money"));
    public static String kit_already_claimed = warning(UltiCore.getWords("kit_already_claimed"));
    public static String claimed = info(UltiCore.getWords("claimed"));
    public static String bought = info(UltiCore.getWords("bought"));

}
