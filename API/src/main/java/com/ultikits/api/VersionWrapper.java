package com.ultikits.api;

import com.ultikits.enums.Colors;
import com.ultikits.enums.Sounds;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public interface VersionWrapper {

    ItemStack getColoredPlaneGlass(Colors plane);

    ItemStack getSign();

    ItemStack getEndEye();

    ItemStack getEmailMaterial(boolean isRead);

    ItemStack getHead(OfflinePlayer player);

    ItemStack getGrassBlock();

    Objective registerNewObjective(Scoreboard scoreboard, String name, String criteria, String displayName);

    Sound getSound(Sounds sound);

    ItemStack getBed(Colors bedColor);

    int getItemDurability(ItemStack itemStack);

    ItemStack getItemInHand(Player player, boolean isMainHand);
}
