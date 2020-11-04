package com.ultikits.abstracts;

import com.ultikits.main.UltiCoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractConsoleCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            commandSender.sendMessage(ChatColor.RED + UltiCoreAPI.languageUtils.getWords("command_can_only_perform_in_console"));
            return false;
        }
        return onConsoleCommand(commandSender, command, strings);
    }

    protected abstract boolean onConsoleCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String[] strings);
}
