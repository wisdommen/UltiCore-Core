package com.ultikits.utils;

import com.ultikits.main.UltiCoreAPI;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

public class EconomyUtils {

    private static @Nullable Class<?> getEconomy(){
        try {
            return Class.forName("com.minecraft.economy.apis.UltiEconomy");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean hasBalance(OfflinePlayer player, int amount){
        if (UltiCoreAPI.getIsVaultInstalled()){
            return UltiCoreAPI.getEcon().has(player, amount);
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
        if (UltiCoreAPI.getIsVaultInstalled()){
            return (int) Math.round(UltiCoreAPI.getEcon().getBalance(player));
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
        try{
            Object economy = getEconomy().newInstance();
            Object checkMoney = getEconomy().getMethod("checkBank", String.class).invoke(economy, player.getName());
            return (int) checkMoney;
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean withdraw(OfflinePlayer player, int amount){
        if (UltiCoreAPI.getIsVaultInstalled()){
            return UltiCoreAPI.getEcon().withdrawPlayer(player, amount).transactionSuccess();
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
