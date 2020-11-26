package com.ultikits.utils;

import com.ultikits.main.UltiCoreAPI;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

public class EconomyUtils {

    private static boolean isUltiEconomyLoaded;

    private static @Nullable Class<?> getEconomy(){
        try {
            Class<?> clazz = Class.forName("com.minecraft.economy.apis.UltiEconomy");
            isUltiEconomyLoaded = false;
            return clazz;
        } catch (Exception ignored) {
            isUltiEconomyLoaded = false;
        }
        return null;
    }

    private static boolean hasBalance(OfflinePlayer player, int amount){
        if (UltiCoreAPI.isVaultLoaded()){
            return UltiCoreAPI.getEcon().has(player, amount);
        }
        if (!isUltiEconomyLoaded){
            return false;
        }
        try{
            Object economy = getEconomy().newInstance();
            Object checkMoney = getEconomy().getMethod("checkMoney", String.class).invoke(economy, player.getName());
            return (int) checkMoney >= amount;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static int checkMoney(OfflinePlayer player){
        if (UltiCoreAPI.isVaultLoaded()){
            return (int) Math.round(UltiCoreAPI.getEcon().getBalance(player));
        }
        if (!isUltiEconomyLoaded){
            return 0;
        }
        try{
            Object economy = getEconomy().newInstance();
            Object checkMoney = getEconomy().getMethod("checkMoney", String.class).invoke(economy, player.getName());
            return (int) checkMoney;
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public static int checkBank(OfflinePlayer player){
        if (!isUltiEconomyLoaded){
            return 0;
        }
        try{
            Object economy = getEconomy().newInstance();
            Object checkMoney = getEconomy().getMethod("checkBank", String.class).invoke(economy, player.getName());
            return (int) checkMoney;
        } catch (Exception ignored){
        }
        return 0;
    }

    public static boolean withdraw(OfflinePlayer player, int amount){
        if (UltiCoreAPI.isVaultLoaded()){
            return UltiCoreAPI.getEcon().withdrawPlayer(player, amount).transactionSuccess();
        }
        if (!isUltiEconomyLoaded){
            return false;
        }
        try{
            if (hasBalance(player,amount)){
                Object economy = getEconomy().newInstance();
                getEconomy().getMethod("takeFrom", String.class, Integer.class).invoke(economy, player.getName(), amount);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
