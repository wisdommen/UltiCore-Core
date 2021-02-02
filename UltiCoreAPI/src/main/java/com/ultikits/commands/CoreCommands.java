package com.ultikits.commands;

import com.ultikits.checker.UltiCoreAPIVersionChecker;
import com.ultikits.main.UltiCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.ultikits.utils.MessagesUtils.info;
import static com.ultikits.utils.MessagesUtils.warning;

public class CoreCommands implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (args.length) {
            case 1:
                switch (args[0]) {
                    case "upgrade":
                        if (UltiCoreAPIVersionChecker.isOutDate) {
                            UltiCoreAPIVersionChecker.downloadNewVersion();
                        } else {
                            UltiCoreAPIVersionChecker.deleteOldVersion();
                            sender.sendMessage(warning(UltiCore.languageUtils.getString("plugin_up_to_date")));
                        }
                        return true;
                }
            case 2:
                if (!args[0].equals("upgrade")){
                    return false;
                }
                String name = args[1];
                if (UltiCore.getInstance().getServer().getPluginManager().getPlugin(name) == null){
                    return false;
                }
                switch (name.toUpperCase()){
                    case "ULTITOOLS":
                        sender.sendMessage(info(String.format(UltiCore.languageUtils.getString("upgrade_other_plugins"), "UltiTools")));
                        try {
                            Class clazz = Class.forName("com.ultikits.ultitools.checker.VersionChecker");
                            Object object = clazz.newInstance();
                            Field field = clazz.getDeclaredField("isOutDate");
                            boolean isOutDate = field.getBoolean(object);
                            if (isOutDate){
                                sender.sendMessage(info(String.format(UltiCore.languageUtils.getString("download_other_plugins"), "UltiTools")));
                                Method method = clazz.getDeclaredMethod("downloadNewVersion");
                                method.setAccessible(true);
                                method.invoke(object);
                                method.setAccessible(false);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            UltiCore.getInstance().getServer().getPluginManager().disablePlugin(UltiCore.getInstance().getServer().getPluginManager().getPlugin(name));
                                            Method method = clazz.getDeclaredMethod("deleteOldVersion");
                                            method.setAccessible(true);
                                            method.invoke(object);
                                            method.setAccessible(false);
                                            UltiCore.getInstance().getServer().getPluginManager().enablePlugin(UltiCore.getInstance().getServer().getPluginManager().getPlugin(name));
                                        }catch (Exception e){
                                            sender.sendMessage(warning(String.format(UltiCore.languageUtils.getString("upgrade_other_plugins_failed"), "UltiTools")));
                                        }
                                    }
                                }.runTaskLater(UltiCore.getInstance(), 200L);
                            }
                            sender.sendMessage(info(String.format(UltiCore.languageUtils.getString("no_update_for_other_plugins"), "UltiTools")));
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    case "ULTIECONOMY":
                    case "ULTILEVEL":
                }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.isOp()){
            return null;
        }
        if (args.length == 1 ) {
            List<String> tabCommands = new ArrayList<>();
            tabCommands.add("upgrade");
            return tabCommands;
        } else if (args.length == 2) {
            List<String> tabCommands = new ArrayList<>();
            tabCommands.add("UltiTools");
            return tabCommands;
        }
        return null;
    }
}
